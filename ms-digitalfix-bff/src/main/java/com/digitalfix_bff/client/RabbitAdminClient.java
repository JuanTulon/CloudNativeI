package com.digitalfix_bff.client;

import com.digitalfix_bff.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Feign client que se comunica con ms-digitalfix-rabbitmq-admin (puerto 8086).
 *
 * El BFF actua como proxy: recibe la llamada del frontend (con JWT validado),
 * y la reenvía al microservicio admin sin exponerlo directamente a internet.
 *
 * URL configurable via variable de entorno RABBIT_ADMIN_URL para Docker/EC2.
 */
@FeignClient(
    name = "rabbit-admin",
    url = "${RABBIT_ADMIN_URL:http://localhost:8086}",
    configuration = FeignClientConfig.class
)
public interface RabbitAdminClient {

    // COLAS
    @GetMapping("/rabbit/queues")
    List<Map<String, Object>> listarColas();

    @PostMapping("/rabbit/queues")
    Map<String, String> crearCola(@RequestBody Map<String, Object> dto);

    @DeleteMapping("/rabbit/queues/{name}")
    Map<String, String> eliminarCola(@PathVariable("name") String name);


    // EXCHANGES
    @GetMapping("/rabbit/exchanges")
    List<Map<String, Object>> listarExchanges();

    @PostMapping("/rabbit/exchanges")
    Map<String, String> crearExchange(@RequestBody Map<String, Object> dto);

    @DeleteMapping("/rabbit/exchanges/{name}")
    Map<String, String> eliminarExchange(@PathVariable("name") String name);

    // BINDINGS
    @GetMapping("/rabbit/bindings")
    List<Map<String, Object>> listarBindings();

    @PostMapping("/rabbit/bindings")
    Map<String, String> crearBinding(@RequestBody Map<String, Object> dto);

    @DeleteMapping("/rabbit/bindings")
    Map<String, String> eliminarBinding(@RequestBody Map<String, Object> dto);
}
