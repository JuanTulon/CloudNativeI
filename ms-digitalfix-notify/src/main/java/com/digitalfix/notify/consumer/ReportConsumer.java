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
 * CONSUMER: escucha q.cmd.report y genera el informe tecnico / certificado de trabajo.
 *
 * Se activa cuando una orden de trabajo pasa al estado CERRADA.
 * El informe documenta:
 *   - Los datos de la orden completada
 *   - El tecnico que ejecuto el trabajo
 *   - La fecha y direccion del servicio
 *
 * En produccion: generaria un PDF con iText/JasperReports, lo subria a S3
 * y enviaria la URL al cliente y al supervisor para su archivo.
 *
 *  - ACK/NACK explicito (acknowledge-mode=manual configurado en application.properties).
 *  - NACK sin requeue en caso de error → el mensaje va automaticamente a q.cmd.report.dlq.
 *  - Log estructurado con contexto completo para trazabilidad.
 */
@Service
public class ReportConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReportConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.Q_REPORT)
    public void recibirReporte(OrdenEventoMensaje evento,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("[REPORT-CONSUMER] Iniciando generacion de informe → orden={} | tecnico={} | estado={} → {}",
                    evento.getIdOrdenTrabajo(),
                    evento.getTecnicoAsignadoId(),
                    evento.getEstadoAnterior(),
                    evento.getEstadoNuevo());

            // Genera el informe tecnico de cierre de la orden.
            // En produccion: PDF con iText / JasperReports → upload S3 → email URL.
            log.info("[REPORT-CONSUMER] 📄 Generando informe tecnico de cierre:");
            log.info("  → Orden N°:    {}", evento.getIdOrdenTrabajo());
            log.info("  → Cliente:     {}", evento.getClienteId());
            log.info("  → Tecnico:     {}", evento.getTecnicoAsignadoId());
            log.info("  → Direccion:   {}", evento.getDireccion());
            log.info("  → Estado final: {}", evento.getEstadoNuevo());
            log.info("  → TraceId:     {}", evento.getTraceId());

            // Simula tiempo de generacion del PDF (procesamiento, firma digital, upload)
            Thread.sleep(300);

            // ACK: mensaje procesado exitosamente, el broker lo elimina de q.cmd.report
            channel.basicAck(tag, false);

            log.info("[REPORT-CONSUMER] ✅ Informe generado y archivado | eventId={} | correlationId={}",
                    evento.getEventId(), evento.getCorrelationId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // NACK sin requeue → el mensaje va a q.cmd.report.dlq automaticamente
            // gracias a x-dead-letter-exchange y x-dead-letter-routing-key de la cola
            channel.basicNack(tag, false, false);
            log.error("[REPORT-CONSUMER] ❌ Hilo interrumpido, mensaje enviado a DLQ | eventId={} | orden={}",
                    evento.getEventId(), evento.getIdOrdenTrabajo());

        } catch (Exception e) {
            // NACK sin requeue → DLQ para inspeccion y reintento manual
            channel.basicNack(tag, false, false);
            log.error("[REPORT-CONSUMER] ❌ Error generando informe, mensaje enviado a DLQ | eventId={} | orden={} | error={}",
                    evento.getEventId(), evento.getIdOrdenTrabajo(), e.getMessage(), e);
        }
    }
}
