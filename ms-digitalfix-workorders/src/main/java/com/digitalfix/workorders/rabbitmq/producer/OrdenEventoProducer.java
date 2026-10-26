package com.digitalfix.workorders.rabbitmq.producer;

import com.digitalfix.workorders.rabbitmq.dto.OrdenEventoMensaje;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * PRODUCER: publica eventos de ordenes de trabajo en el exchange direct.
 *
 * No conoce las colas ni a los consumidores, solo el exchange y la routing key.
 * Ahi esta el desacoplamiento: el broker decide a que cola(s) llega el mensaje
 * segun los bindings declarados en RabbitMQConfig.
 *
 * Siempre usa cmd.direct para mayor control. cmd.topic esta disponible
 * si en el futuro se necesita enrutar por patron.
 */
@Service
public class OrdenEventoProducer {

    private static final Logger log = LoggerFactory.getLogger(OrdenEventoProducer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.direct}")
    private String exchangeDirect;

    /**
     * Publica un evento en el exchange direct con la routing key indicada.
     * convertAndSend serializa usando el MessageConverter (Jackson2JsonMessageConverter)
     * configurado en RabbitMQConfig, produciendo un mensaje JSON legible.
     *
     * @param routingKey routing key exacta (ej. "email.send", "dispatch.ticket", "report.gen")
     * @param evento     envelope con todos los datos del evento
     */
    public void publicar(String routingKey, OrdenEventoMensaje evento) {
        rabbitTemplate.convertAndSend(exchangeDirect, routingKey, evento);

        log.info("[PRODUCER] Evento publicado → type={} | routingKey={} | correlationId={} | eventId={}",
                evento.getType(),
                routingKey,
                evento.getCorrelationId(),
                evento.getEventId());
    }
}
