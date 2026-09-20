package com.digitalfix.workorders.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Estructura estándar para devolver errores en las respuestas de la API.
 * Garantiza que los clientes frontend o móviles consuman los errores siempre con el mismo formato.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estructura estándar de errores de la API")
public class ErrorApi {
    @Schema(description = "Fecha y hora en la que ocurrió el error")
    private LocalDateTime fechaHora;
    @Schema(description = "Código de estado HTTP", example = "400")
    private int estadoHttp;
    @Schema(description = "Mensaje corto del error HTTP", example = "Bad Request")
    private String error;
    @Schema(description = "Mensaje detallado para el desarrollador o usuario", example = "No se puede pasar a EN_EJECUCION sin haber sido ASIGNADA previamente")
    private String mensaje;
    @Schema(description = "Ruta de la API donde ocurrió el error", example = "/api/workorders/1/status")
    private String ruta;
}
