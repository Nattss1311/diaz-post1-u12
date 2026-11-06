package com.empresa.reembolsos.procesamiento.validacion;

public record ResultadoValidacion(boolean valida, String motivoRechazo) {
    public static ResultadoValidacion ok() {
        return new ResultadoValidacion(true, null);
    }
    public static ResultadoValidacion rechazar(String motivo) {
        return new ResultadoValidacion(false, motivo);
    }
}