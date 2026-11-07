package com.empresa.reembolsos.procesamiento.notificacion;

import com.empresa.reembolsos.dominio.EstadoSolicitud;
import com.empresa.reembolsos.dominio.SolicitudReembolso;

/**
 * Suscriptor que reacciona a los cambios de estado de una solicitud (patron Observer, RF3).
 * <p>
 * El servicio de aplicacion recibe todas las implementaciones como {@code List<ObservadorCambioEstado>}
 * y las notifica sin conocerlas; agregar un interesado nuevo (por ejemplo, un panel de metricas)
 * consiste en crear otra clase {@code @Component} que implemente esta interfaz.
 */
public interface ObservadorCambioEstado {

    /**
     * Se invoca despues de persistir el nuevo estado de la solicitud.
     *
     * @param solicitud      solicitud con su estado ya actualizado
     * @param estadoAnterior estado que tenia la solicitud antes del cambio
     */
    void onCambioEstado(SolicitudReembolso solicitud, EstadoSolicitud estadoAnterior);
}