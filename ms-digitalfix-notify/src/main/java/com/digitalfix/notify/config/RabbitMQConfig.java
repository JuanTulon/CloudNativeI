package com.digitalfix.notify.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Infraestructura RabbitMQ declarada en el lado del consumidor.
 *
 * La declaracion es IDEMPOTENTE: si los exchanges/colas ya existen en el broker
 * (porque los creo ms-digitalfix-workorders al arrancar), esta declaracion no falla
 * ni duplica nada. Si el consumidor arranca primero, los crea el.
 *
 * IMPORTANTE: los nombres deben coincidir EXACTAMENTE con los del producer.
 * Un typo NO da error visible: los mensajes se pierden en silencio.
 */
@Configuration
public class RabbitMQConfig {

    // Nombres de exchanges
    static final String EXCHANGE_DIRECT  = "cmd.direct";
    static final String EXCHANGE_TOPIC   = "cmd.topic";
    static final String EXCHANGE_DLX     = "cmd.dead.dlx";

    // Nombres de colas (constantes publicas para @RabbitListener)
    public static final String Q_EMAIL        = "q.cmd.email";
    public static final String Q_DISPATCH     = "q.cmd.dispatch";
    public static final String Q_REPORT       = "q.cmd.report";
    public static final String Q_EMAIL_DLQ    = "q.cmd.email.dlq";
    public static final String Q_DISPATCH_DLQ = "q.cmd.dispatch.dlq";
    public static final String Q_REPORT_DLQ   = "q.cmd.report.dlq";

    // EXCHANGES
    @Bean
    public DirectExchange cmdDirectExchange() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    @Bean
    public TopicExchange cmdTopicExchange() {
        return new TopicExchange(EXCHANGE_TOPIC);
    }

    @Bean
    public DirectExchange cmdDeadDlxExchange() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    // COLAS PRINCIPALES
    @Bean
    public Queue qEmail() {
        return QueueBuilder.durable(Q_EMAIL)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "email.dlq")
                .build();
    }

    @Bean
    public Queue qDispatch() {
        return QueueBuilder.durable(Q_DISPATCH)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "dispatch.dlq")
                .build();
    }

    @Bean
    public Queue qReport() {
        return QueueBuilder.durable(Q_REPORT)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "report.dlq")
                .build();
    }

    // COLAS DLQ
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

    // BINDINGS DIRECT
    @Bean
    public Binding bindEmailDirect(Queue qEmail, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(qEmail).to(cmdDirectExchange).with("email.send");
    }

    @Bean
    public Binding bindDispatchDirect(Queue qDispatch, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(qDispatch).to(cmdDirectExchange).with("dispatch.ticket");
    }

    @Bean
    public Binding bindReportDirect(Queue qReport, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(qReport).to(cmdDirectExchange).with("report.gen");
    }

    // BINDINGS TOPIC
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

    // BINDINGS DLQ
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

    // CONVERTER de JSON

    /**
     * Serializa/deserializa mensajes como JSON.
     * Sin esto el consumidor no puede reconstruir el objeto OrdenEventoMensaje.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
