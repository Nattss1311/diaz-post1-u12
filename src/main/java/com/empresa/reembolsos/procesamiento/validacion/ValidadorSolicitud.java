package com.empresa.reembolsos.procesamiento.validacion;

import java.util.List;

import com.empresa.reembolsos.dominio.SolicitudReembolso;

/**
 * Eslabon base de la cadena de validacion (patron Chain of Responsibility, RF1).
 * <p>
 * Cada subclase implementa una sola verificacion en {@link #validarPropio}. Si la verificacion
 * falla, la cadena se corta (fail-fast); si pasa, la solicitud continua al siguiente eslabon,
 * sin que ningun eslabon sepa cuantos hay despues de el. El orden se arma en {@link CadenaValidadores}.
 */
public abstract class ValidadorSolicitud {
    private ValidadorSolicitud siguiente;

    public ValidadorSolicitud siguiente(ValidadorSolicitud siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public ResultadoValidacion validar(SolicitudReembolso solicitud, List<SolicitudReembolso> pendientesDelEmpleado) {
        ResultadoValidacion propio = validarPropio(solicitud, pendientesDelEmpleado);
        if (!propio.valida() || siguiente == null) {
            return propio;
        }
        return siguiente.validar(solicitud, pendientesDelEmpleado);
    }

    /**
     * Verificacion propia de este eslabon.
     *
     * @param solicitud             solicitud a verificar
     * @param pendientesDelEmpleado solicitudes PENDIENTE del mismo empleado, ya cargadas por el servicio
     *                              (los validadores nunca consultan la base de datos)
     * @return {@link ResultadoValidacion#ok()} o un rechazo con su motivo
     */
    protected abstract ResultadoValidacion validarPropio(
            SolicitudReembolso solicitud, List<SolicitudReembolso> pendientesDelEmpleado);
}