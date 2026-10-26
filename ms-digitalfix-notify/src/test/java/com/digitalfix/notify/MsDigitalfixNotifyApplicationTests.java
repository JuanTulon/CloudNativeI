package com.digitalfix.notify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        // Deshabilita la conexion real a RabbitMQ en tests unitarios
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
})
class MsDigitalfixNotifyApplicationTests {

    @Test
    void contextLoads() {
    }
}
