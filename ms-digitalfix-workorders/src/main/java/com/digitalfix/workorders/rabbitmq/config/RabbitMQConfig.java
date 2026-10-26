package com.digitalfix.workorders.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres de colas — constantes publicas para reusar en @RabbitListener
    public static final String Q_EMAIL         = "q.cmd.email";
    public static final String Q_DISPATCH      = "q.cmd.dispatch";
    public static final String Q_REPORT        = "q.cmd.report";
    public static final String Q_EMAIL_DLQ     = "q.cmd.email.dlq";
    public static final String Q_DISPATCH_DLQ  = "q.cmd.dispatch.dlq";
    public static final String Q_REPORT_DLQ    = "q.cmd.report.dlq";

    // Routing keys usadas por el producer
    public static final String RK_EMAIL_SEND      = "email.send";
    public static final String RK_DISPATCH_TICKET = "dispatch.ticket";
    public static final String RK_REPORT_GEN      = "report.gen";

    @Value("${app.rabbitmq.exchange.direct}")
    private String exchangeDirect;

    @Value("${app.rabbitmq.exchange.topic}")
    private String exchangeTopic;

    @Value("${app.rabbitmq.exchange.dlx}")
    private String exchangeDlx;

    // EXCHANGES
    /**
     * Exchange direct: enruta por routing key exacta.
     * El producer siempre publica aqui con la routing key precisa.
     */
    @Bean
    public DirectExchange cmdDirectExchange() {
        return new DirectExchange(exchangeDirect);
    }

    /**
     * Exchange topic: enruta por patron (email.*, dispatch.#, report.*).
     * Permite flexibilidad si en el futuro se agregan sub-tipos de evento.
     */
    @Bean
    public TopicExchange cmdTopicExchange() {
        return new TopicExchange(exchangeTopic);
    }

    /**
     * Exchange dead-letter (DLX): recibe mensajes rechazados (NACK sin requeue).
     * Las colas principales apuntan aqui mediante x-dead-letter-exchange.
     */
    @Bean
    public DirectExchange cmdDeadDlxExchange() {
        return new DirectExchange(exchangeDlx);
    }

    // COLAS PRINCIPALES (con dead-letter hacia cmd.dead.dlx)
    /**
     * q.cmd.email — notificaciones email/push al cliente.
     * Configura x-dead-letter-exchange y x-dead-letter-routing-key
     * para que los NACK sin requeue vayan automaticamente a la DLQ.
     */
    @Bean
    public Queue qEmail() {
        return QueueBuilder.durable(Q_EMAIL)
                .withArgument("x-dead-letter-exchange", exchangeDlx)
                .withArgument("x-dead-letter-routing-key", "email.dlq")
                .build();
    }

    /**
     * q.cmd.dispatch — ticket de despacho al tecnico.
     */
    @Bean
    public Queue qDispatch() {
        return QueueBuilder.durable(Q_DISPATCH)
                .withArgument("x-dead-letter-exchange", exchangeDlx)
                .withArgument("x-dead-letter-routing-key", "dispatch.dlq")
                .build();
    }

    /**
     * q.cmd.report — generacion de PDF / informe tecnico.
     */
    @Bean
    public Queue qReport() {
        return QueueBuilder.durable(Q_REPORT)
                .withArgument("x-dead-letter-exchange", exchangeDlx)
                .withArgument("x-dead-letter-routing-key", "report.dlq")
                .build();
    }


    // COLAS DLQ (simples, sin dead-letter propio)
    @Bean
    public Queue qEmailDlq() {
        return QueueBuilder.durable(Q_EMAIL_DLQ).build();
    }

    @Bean
    public Queue qDispatchDlq() {
        return QueueBuilder.durable(Q_DISPATCH_DLQ).build();
    }

    @Bean
    public Queue qReportDlq() {
        return QueueBuilder.durable(Q_REPORT_DLQ).build();
    }

    // BINDINGS DIRECT (routing key exacta)
    @Bean
    public Binding bindEmailDirect(Queue qEmail, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(qEmail).to(cmdDirectExchange).with(RK_EMAIL_SEND);
    }

    @Bean
    public Binding bindDispatchDirect(Queue qDispatch, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(qDispatch).to(cmdDirectExchange).with(RK_DISPATCH_TICKET);
    }

    @Bean
    public Binding bindReportDirect(Queue qReport, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(qReport).to(cmdDirectExchange).with(RK_REPORT_GEN);
    }

    // BINDINGS TOPIC (patrones)
    @Bean
    public Binding bindEmailTopic(Queue qEmail, TopicExchange cmdTopicExchange) {
        return BindingBuilder.bind(qEmail).to(cmdTopicExchange).with("email.*");
    }

    @Bean
    public Binding bindDispatchTopic(Queue qDispatch, TopicExchange cmdTopicExchange) {
        return BindingBuilder.bind(qDispatch).to(cmdTopicExchange).with("dispatch.#");
    }

    @Bean
    public Binding bindReportTopic(Queue qReport, TopicExchange cmdTopicExchange) {
        return BindingBuilder.bind(qReport).to(cmdTopicExchange).with("report.*");
    }

    // BINDINGS DLQ (sobre cmd.dead.dlx)
    @Bean
    public Binding bindEmailDlq(Queue qEmailDlq, DirectExchange cmdDeadDlxExchange) {
        return BindingBuilder.bind(qEmailDlq).to(cmdDeadDlxExchange).with("email.dlq");
    }

    @Bean
    public Binding bindDispatchDlq(Queue qDispatchDlq, DirectExchange cmdDeadDlxExchange) {
        return BindingBuilder.bind(qDispatchDlq).to(cmdDeadDlxExchange).with("dispatch.dlq");
    }

    @Bean
    public Binding bindReportDlq(Queue qReportDlq, DirectExchange cmdDeadDlxExchange) {
        return BindingBuilder.bind(qReportDlq).to(cmdDeadDlxExchange).with("report.dlq");
    }

    // ===================================================================
    // CONVERTER JSON
    // ===================================================================

    /**
     * Serializa los mensajes a JSON en vez de binario Java.
     * Sin esto el mensaje es ilegible en la Management UI de RabbitMQ
     * y queda acoplado a las clases Java de este proyecto.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
