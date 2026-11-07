package com.empresa.reembolsos.adaptadores.rest;

import com.empresa.reembolsos.aplicacion.SolicitudInvalidaException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Traduce los rechazos de la cadena de validacion a respuestas HTTP 400 con el motivo en el cuerpo.
 */
@RestControllerAdvice
public class ManejadorErroresRest {

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<Map<String, String>> manejarSolicitudInvalida(SolicitudInvalidaException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
