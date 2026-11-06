package com.empresa.reembolsos;

import com.empresa.reembolsos.aplicacion.SolicitudReembolsoService;
import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.EstadoSolicitud;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SolicitudIntegracionTest {

    @Autowired
    private SolicitudReembolsoService servicio;

    @Test
    void testFlujoCompletoCreacionYAprobacion() {
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setEmpleadoId("EMP-001");
        solicitud.setCategoria(CategoriaGasto.VIAJE);
        solicitud.setDiasViaje(2);
        solicitud.setMontoSolicitado(new BigDecimal("120000"));
        solicitud.setFechaGasto(LocalDate.now());
        solicitud.setDocumentosSoporte(true);

        // Flujo de creación y validación
        SolicitudReembolso creada = servicio.crear(solicitud);
        assertNotNull(creada.getId());
        assertEquals(EstadoSolicitud.PENDIENTE, creada.getEstado());
        assertEquals(new BigDecimal("120000"), creada.getMontoAprobado());

        // Flujo de cambio de estado a APROBADA
        SolicitudReembolso aprobada = servicio.cambiarEstado(creada.getId(), EstadoSolicitud.APROBADA);
        assertEquals(EstadoSolicitud.APROBADA, aprobada.getEstado());
    }
}