package com.digitalfix.notify.consumer;

import com.digitalfix.notify.config.RabbitMQConfig;
import com.digitalfix.notify.dto.OrdenEventoMensaje;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * CONSUMER: escucha q.cmd.email y procesa notificaciones email/push al cliente.
 *
 * Corre en su propio hilo: para cuando este metodo se ejecuta,
 * el workorders-svc ya le respondio al BFF hace rato (respuesta asincrona).
 *
 *  - ACK/NACK explicito (acknowledge-mode=manual en properties).
 *  - NACK sin requeue en caso de error → el mensaje va automaticamente a q.cmd.email.dlq.
 *  - Idempotencia: se puede agregar control por eventId si hay riesgo de duplicados.
 */
@Service
public class EmailNotifyConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotifyConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.Q_EMAIL)
    public void recibirEmail(OrdenEventoMensaje evento,
                             Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("[EMAIL-CONSUMER] Procesando evento → type={} | orden={} | cliente={} | estado={} → {}",
                    evento.getType(),
                    evento.getIdOrdenTrabajo(),
                    evento.getClienteId(),
                    evento.getEstadoAnterior(),
                    evento.getEstadoNuevo());

            // Logica de notificacion segun el tipo de evento y estado
            // En produccion aqui iria: JavaMailSender, servicio de push, etc.
            switch (evento.getEstadoNuevo()) {
                case "ASIGNADA" ->
                    log.info("[EMAIL-CONSUMER] ✉ Enviando email a cliente {} → 'Su orden #{} ha sido asignada a un tecnico'",
                            evento.getClienteId(), evento.getIdOrdenTrabajo());
                case "EN_DESPLAZAMIENTO" ->
                    log.info("[EMAIL-CONSUMER] ✉ Enviando email a cliente {} → 'El tecnico {} ya va en camino a {}'",
                            evento.getClienteId(), evento.getTecnicoAsignadoId(), evento.getDireccion());
                case "CERRADA" ->
                    log.info("[EMAIL-CONSUMER] ✉ Enviando email a cliente {} → 'Su orden #{} ha sido completada exitosamente'",
                            evento.getClienteId(), evento.getIdOrdenTrabajo());
                case "CANCELADA" ->
                    log.info("[EMAIL-CONSUMER] ✉ Enviando email a cliente {} → 'Su orden #{} fue cancelada'",
                            evento.getClienteId(), evento.getIdOrdenTrabajo());
                default ->
                    log.warn("[EMAIL-CONSUMER] Estado desconocido: {}", evento.getEstadoNuevo());
            }

            // Simula latencia de envio externo (servicio email, proveedor push)
            Thread.sleep(200);

            // ACK: confirma que el mensaje fue procesado correctamente
            // El broker lo elimina de la cola principal
            channel.basicAck(tag, false);

            log.info("[EMAIL-CONSUMER] ✅ Notificacion enviada | eventId={} | correlationId={}",
                    evento.getEventId(), evento.getCorrelationId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // NACK sin requeue: el mensaje va a la DLQ (q.cmd.email.dlq) automaticamente
            channel.basicNack(tag, false, false);
            log.error("[EMAIL-CONSUMER] ❌ Hilo interrumpido, mensaje enviado a DLQ | eventId={}", evento.getEventId());

        } catch (Exception e) {
            // NACK sin requeue: el mensaje va a la DLQ para inspeccion manual
            channel.basicNack(tag, false, false);
            log.error("[EMAIL-CONSUMER] ❌ Error procesando notificacion, mensaje enviado a DLQ | eventId={} | error={}",
                    evento.getEventId(), e.getMessage(), e);
        }
    }
}
