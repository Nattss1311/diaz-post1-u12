package com.empresa.reembolsos.procesamiento.validacion;

import java.util.List;

import org.springframework.stereotype.Component;

import com.empresa.reembolsos.dominio.SolicitudReembolso;

@Component
public class ValidadorDocumentosSoporte extends ValidadorSolicitud {
    @Override
    protected ResultadoValidacion validarPropio(SolicitudReembolso solicitud, List<SolicitudReembolso> pendientes) {
        return solicitud.isDocumentosSoporte()
                ? ResultadoValidacion.ok()
                : ResultadoValidacion.rechazar("Faltan documentos soporte");
    }
}