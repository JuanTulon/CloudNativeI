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
 * CONSUMER: escucha q.cmd.dispatch y genera el ticket de despacho al tecnico.
 *
 * El ticket de despacho es la notificacion formal al tecnico que le indica:
 * - Que orden de trabajo debe atender
 * - La direccion a donde debe ir
 * - Los datos del cliente
 *
 * En produccion: enviaria un SMS, notificacion push a la app del tecnico
 * o un email con el detalle completo del trabajo.
 */
@Service
public class DispatchConsumer {

    private static final Logger log = LoggerFactory.getLogger(DispatchConsumer.class);

    /**
     * Recibe eventos TICKET_DESPACHO desde q.cmd.dispatch.
     *
     * Solo se publican en la transicion ASIGNADA (el supervisor asigna un tecnico).
     *
     */
    @RabbitListener(queues = RabbitMQConfig.Q_DISPATCH)
    public void recibirDespacho(OrdenEventoMensaje evento,
                                Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("[DISPATCH-CONSUMER] Procesando ticket → tecnico={} | orden={} | direccion={}",
                    evento.getTecnicoAsignadoId(),
                    evento.getIdOrdenTrabajo(),
                    evento.getDireccion());

            // Genera el ticket de despacho para el tecnico.
            // app movil push, SMS gateway, sistema ERP, etc.
            log.info("[DISPATCH-CONSUMER] 📋 Generando ticket de despacho:");
            log.info("  → Tecnico:    {}", evento.getTecnicoAsignadoId());
            log.info("  → Orden:      #{}", evento.getIdOrdenTrabajo());
            log.info("  → Direccion:  {}", evento.getDireccion());
            log.info("  → Cliente:    {}", evento.getClienteId());
            log.info("  → TraceId:    {}", evento.getTraceId());

            // Simula tiempo de generacion del ticket (PDF, push notification, etc.)
            Thread.sleep(150);

            // ACK: mensaje procesado exitosamente
            channel.basicAck(tag, false);

            log.info("[DISPATCH-CONSUMER] ✅ Ticket generado | eventId={} | correlationId={}",
                    evento.getEventId(), evento.getCorrelationId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // NACK sin requeue → DLQ (q.cmd.dispatch.dlq)
            channel.basicNack(tag, false, false);
            log.error("[DISPATCH-CONSUMER] ❌ Hilo interrumpido, mensaje enviado a DLQ | eventId={}", evento.getEventId());

        } catch (Exception e) {
            // NACK sin requeue → DLQ
            channel.basicNack(tag, false, false);
            log.error("[DISPATCH-CONSUMER] ❌ Error generando ticket, mensaje enviado a DLQ | eventId={} | error={}",
                    evento.getEventId(), e.getMessage(), e);
        }
    }
}
