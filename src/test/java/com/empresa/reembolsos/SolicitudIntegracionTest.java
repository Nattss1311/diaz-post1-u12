package com.empresa.reembolsos;

import com.empresa.reembolsos.aplicacion.SolicitudInvalidaException;
import com.empresa.reembolsos.aplicacion.SolicitudReembolsoService;
import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.EstadoSolicitud;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ReembolsosApplication.class)
@ExtendWith(OutputCaptureExtension.class)
class SolicitudIntegracionTest {

    @Autowired
    private SolicitudReembolsoService servicio;

    private static SolicitudReembolso nuevaSolicitud(CategoriaGasto categoria, String monto, int dias, boolean documentos) {
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setEmpleadoId("EMP-" + UUID.randomUUID());
        solicitud.setCategoria(categoria);
        solicitud.setDiasViaje(dias);
        solicitud.setMontoSolicitado(new BigDecimal(monto));
        solicitud.setFechaGasto(LocalDate.now());
        solicitud.setDocumentosSoporte(documentos);
        return solicitud;
    }

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

    @ParameterizedTest(name = "{0}: solicitado {1} -> aprobado {3}")
    @CsvSource({
            "VIAJE,          120000,  2, 120000",
            "REPRESENTACION, 1500000, 0, 1500000",
            "CAPACITACION,   1000000, 0, 800000"
    })
    void testFlujoCompletoParaLasTresCategorias(CategoriaGasto categoria, String monto, int dias, String esperado) {
        SolicitudReembolso creada = servicio.crear(nuevaSolicitud(categoria, monto, dias, true));

        assertNotNull(creada.getId());
        assertEquals(EstadoSolicitud.PENDIENTE, creada.getEstado());
        assertEquals(0, new BigDecimal(esperado).compareTo(creada.getMontoAprobado()));
    }

    @Test
    void testRechazaSolicitudSinDocumentosSoporteConMensajeClaro() {
        SolicitudReembolso sinDocumentos = nuevaSolicitud(CategoriaGasto.REPRESENTACION, "500000", 0, false);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> servicio.crear(sinDocumentos));
        assertEquals("Faltan documentos soporte", ex.getMessage());
    }

    @Test
    void testContabilidadSoloReaccionaCuandoLaSolicitudSeAprueba(CapturedOutput salida) {
        SolicitudReembolso rechazada = servicio.crear(nuevaSolicitud(CategoriaGasto.REPRESENTACION, "400000", 0, true));
        servicio.cambiarEstado(rechazada.getId(), EstadoSolicitud.RECHAZADA);
        assertTrue(salida.getOut().contains("Notificando a " + rechazada.getEmpleadoId()));
        assertFalse(salida.getOut().contains("para " + rechazada.getEmpleadoId()));

        SolicitudReembolso aprobada = servicio.crear(nuevaSolicitud(CategoriaGasto.CAPACITACION, "1000000", 0, true));
        servicio.cambiarEstado(aprobada.getId(), EstadoSolicitud.APROBADA);
        assertTrue(salida.getOut().contains("Contabilidad: programar pago de 800000"));
        assertTrue(salida.getOut().contains("para " + aprobada.getEmpleadoId()));
        assertTrue(salida.getAll().contains("Auditoria: solicitud " + aprobada.getId()));
    }
}
