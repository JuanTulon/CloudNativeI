package com.digitalfix.catalogo.exception;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ErrorApi {
    private LocalDateTime fechaHora;
    private Integer estadoHttp;
    private String error;
    private String mensaje;
    private String ruta;
}
