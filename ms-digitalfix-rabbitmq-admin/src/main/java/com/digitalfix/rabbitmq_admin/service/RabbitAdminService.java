package com.digitalfix.rabbitmq_admin.service;

import com.digitalfix.rabbitmq_admin.dto.BindingRequestDTO;
import com.digitalfix.rabbitmq_admin.dto.ExchangeRequestDTO;
import com.digitalfix.rabbitmq_admin.dto.QueueRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio de gestion dinamica del broker RabbitMQ.
 *
 * Usa RabbitAdmin para:
 *  - Crear y eliminar colas.
 *  - Crear y eliminar exchanges (direct, topic, fanout, headers).
 *  - Crear y eliminar bindings entre exchanges y colas.
 *  - Consultar propiedades de colas (mensaje count, consumer count).
 *
 */
@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

    private final RabbitAdmin rabbitAdmin;

    // Lista de colas conocidas para listar (RabbitAdmin no tiene un API de listado nativo)
    // En produccion se usaria la RabbitMQ Management HTTP API para listar todas
    private final List<String> knownQueues = new ArrayList<>(List.of(
            "q.cmd.email", "q.cmd.dispatch", "q.cmd.report",
            "q.cmd.email.dlq", "q.cmd.dispatch.dlq", "q.cmd.report.dlq"
    ));

    private final List<String> knownExchanges = new ArrayList<>(List.of(
            "cmd.direct", "cmd.topic", "cmd.dead.dlx"
    ));

    private final List<BindingRequestDTO> knownBindings = new ArrayList<>();

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
        // Precarga los bindings conocidos del sistema
        inicializarBindingsConocidos();
    }

    // COLAS
    /**
     * Lista todas las colas conocidas junto con sus propiedades del broker.
     * Retorna nombre, cantidad de mensajes en cola y consumidores activos.
     */
    public List<Map<String, Object>> listarColas() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (String queueName : knownQueues) {
            Properties props = rabbitAdmin.getQueueProperties(queueName);
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", queueName);
            if (props != null) {
                info.put("messageCount", props.getOrDefault(RabbitAdmin.QUEUE_MESSAGE_COUNT, 0));
                info.put("consumerCount", props.getOrDefault(RabbitAdmin.QUEUE_CONSUMER_COUNT, 0));
                info.put("status", "activa");
            } else {
                info.put("messageCount", 0);
                info.put("consumerCount", 0);
                info.put("status", "no encontrada en broker");
            }
            resultado.add(info);
        }
        return resultado;
    }

    /**
     * Crea una nueva cola en el broker.
     * Si se especifica deadLetterExchange, configura x-dead-letter-exchange
     * y x-dead-letter-routing-key en los argumentos de la cola.
     */
    public Map<String, String> crearCola(QueueRequestDTO dto) {
        QueueBuilder builder = dto.isDurable()
                ? QueueBuilder.durable(dto.getName())
                : QueueBuilder.nonDurable(dto.getName());

        if (dto.getDeadLetterExchange() != null && !dto.getDeadLetterExchange().isBlank()) {
            builder.withArgument("x-dead-letter-exchange", dto.getDeadLetterExchange());
            if (dto.getDeadLetterRoutingKey() != null && !dto.getDeadLetterRoutingKey().isBlank()) {
                builder.withArgument("x-dead-letter-routing-key", dto.getDeadLetterRoutingKey());
            }
        }

        org.springframework.amqp.core.Queue queue = builder.build();
        rabbitAdmin.declareQueue(queue);

        if (!knownQueues.contains(dto.getName())) {
            knownQueues.add(dto.getName());
        }

        log.info("[RABBIT-ADMIN] Cola creada → name={} | durable={} | dlx={}",
                dto.getName(), dto.isDurable(), dto.getDeadLetterExchange());

        return Map.of(
                "mensaje", "Cola creada exitosamente",
                "cola", dto.getName()
        );
    }

    /**
     * Elimina una cola del broker.
     * Si la cola no existe, el broker retorna false sin lanzar excepcion.
     */
    public Map<String, String> eliminarCola(String nombre) {
        boolean eliminada = rabbitAdmin.deleteQueue(nombre);
        knownQueues.remove(nombre);

        log.info("[RABBIT-ADMIN] Cola eliminada → name={} | encontrada={}", nombre, eliminada);

        return Map.of(
                "mensaje", eliminada ? "Cola eliminada exitosamente" : "Cola no encontrada en el broker",
                "cola", nombre
        );
    }

    // EXCHANGES
    /**
     * Lista todos los exchanges conocidos del sistema.
     */
    public List<Map<String, Object>> listarExchanges() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (String exchangeName : knownExchanges) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", exchangeName);
            info.put("type", inferirTipoExchange(exchangeName));
            resultado.add(info);
        }
        return resultado;
    }

    /**
     * Crea un exchange en el broker segun el tipo especificado.
     * Tipos validos: direct, topic, fanout, headers.
     */
    public Map<String, String> crearExchange(ExchangeRequestDTO dto) {
        AbstractExchange exchange = switch (dto.getType().toLowerCase()) {
            case "topic"   -> new TopicExchange(dto.getName(), dto.isDurable(), false);
            case "fanout"  -> new FanoutExchange(dto.getName(), dto.isDurable(), false);
            case "headers" -> new HeadersExchange(dto.getName(), dto.isDurable(), false);
            default        -> new DirectExchange(dto.getName(), dto.isDurable(), false);
        };

        rabbitAdmin.declareExchange(exchange);

        if (!knownExchanges.contains(dto.getName())) {
            knownExchanges.add(dto.getName());
        }

        log.info("[RABBIT-ADMIN] Exchange creado → name={} | type={} | durable={}",
                dto.getName(), dto.getType(), dto.isDurable());

        return Map.of(
                "mensaje", "Exchange creado exitosamente",
                "exchange", dto.getName(),
                "type", dto.getType()
        );
    }

    /**
     * Elimina un exchange del broker.
     * Si el exchange tiene colas vinculadas, el broker rechaza la eliminacion.
     */
    public Map<String, String> eliminarExchange(String nombre) {
        boolean eliminado = rabbitAdmin.deleteExchange(nombre);
        knownExchanges.remove(nombre);

        log.info("[RABBIT-ADMIN] Exchange eliminado → name={} | encontrado={}", nombre, eliminado);

        return Map.of(
                "mensaje", eliminado ? "Exchange eliminado exitosamente" : "Exchange no encontrado en el broker",
                "exchange", nombre
        );
    }

    // BINDINGS
    /**
     * Lista todos los bindings conocidos del sistema.
     */
    public List<BindingRequestDTO> listarBindings() {
        return Collections.unmodifiableList(knownBindings);
    }

    /**
     * Crea un binding entre un exchange y una cola con la routing key indicada.
     */
    public Map<String, String> crearBinding(BindingRequestDTO dto) {
        Binding binding = BindingBuilder
                .bind(new org.springframework.amqp.core.Queue(dto.getQueue()))
                .to(new DirectExchange(dto.getExchange()))
                .with(dto.getRoutingKey());

        rabbitAdmin.declareBinding(binding);

        knownBindings.add(dto);

        log.info("[RABBIT-ADMIN] Binding creado → queue={} | exchange={} | routingKey={}",
                dto.getQueue(), dto.getExchange(), dto.getRoutingKey());

        return Map.of(
                "mensaje", "Binding creado exitosamente",
                "queue", dto.getQueue(),
                "exchange", dto.getExchange(),
                "routingKey", dto.getRoutingKey()
        );
    }

    /**
     * Elimina un binding del broker.
     */
    public Map<String, String> eliminarBinding(BindingRequestDTO dto) {
        Binding binding = BindingBuilder
                .bind(new org.springframework.amqp.core.Queue(dto.getQueue()))
                .to(new DirectExchange(dto.getExchange()))
                .with(dto.getRoutingKey());

        rabbitAdmin.removeBinding(binding);

        knownBindings.removeIf(b ->
                b.getQueue().equals(dto.getQueue()) &&
                b.getExchange().equals(dto.getExchange()) &&
                b.getRoutingKey().equals(dto.getRoutingKey())
        );

        log.info("[RABBIT-ADMIN] Binding eliminado → queue={} | exchange={} | routingKey={}",
                dto.getQueue(), dto.getExchange(), dto.getRoutingKey());

        return Map.of(
                "mensaje", "Binding eliminado exitosamente",
                "queue", dto.getQueue(),
                "exchange", dto.getExchange(),
                "routingKey", dto.getRoutingKey()
        );
    }

    // AUXILIARES
    private void inicializarBindingsConocidos() {
        agregarBinding("q.cmd.email",    "cmd.direct",   "email.send");
        agregarBinding("q.cmd.dispatch", "cmd.direct",   "dispatch.ticket");
        agregarBinding("q.cmd.report",   "cmd.direct",   "report.gen");
        agregarBinding("q.cmd.email",    "cmd.topic",    "email.*");
        agregarBinding("q.cmd.dispatch", "cmd.topic",    "dispatch.#");
        agregarBinding("q.cmd.report",   "cmd.topic",    "report.*");
        agregarBinding("q.cmd.email.dlq",    "cmd.dead.dlx", "email.dlq");
        agregarBinding("q.cmd.dispatch.dlq", "cmd.dead.dlx", "dispatch.dlq");
        agregarBinding("q.cmd.report.dlq",   "cmd.dead.dlx", "report.dlq");
    }

    private void agregarBinding(String queue, String exchange, String routingKey) {
        BindingRequestDTO dto = new BindingRequestDTO();
        dto.setQueue(queue);
        dto.setExchange(exchange);
        dto.setRoutingKey(routingKey);
        knownBindings.add(dto);
    }

    private String inferirTipoExchange(String name) {
        if (name.contains("topic"))  return "topic";
        if (name.contains("fanout")) return "fanout";
        if (name.contains("dlx") || name.contains("dead")) return "direct (DLX)";
        return "direct";
    }
}
