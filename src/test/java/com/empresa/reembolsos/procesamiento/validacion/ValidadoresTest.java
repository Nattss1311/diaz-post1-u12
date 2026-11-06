package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidadoresTest {

    @Test
    void testValidadorDocumentosSoporteRechazaSiNoHayDocumentos() {
        ValidadorDocumentosSoporte validador = new ValidadorDocumentosSoporte();
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setDocumentosSoporte(false);

        ResultadoValidacion res = validador.validarPropio(solicitud, Collections.emptyList());
        assertFalse(res.valida());
        assertEquals("Faltan documentos soporte", res.motivoRechazo());
    }

    @Test
    void testValidadorMontoMaximoRechazaSiExcedeTope() {
        ValidadorMontoMaximo validador = new ValidadorMontoMaximo();
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setCategoria(CategoriaGasto.VIAJE);
        solicitud.setMontoSolicitado(new BigDecimal("200000")); // Tope viaje: 150000

        ResultadoValidacion res = validador.validarPropio(solicitud, Collections.emptyList());
        assertFalse(res.valida());
        assertTrue(res.motivoRechazo().contains("Monto excede el tope de la categoria VIAJE"));
    }

    @Test
    void testValidadorDuplicadosRechazaSiHayMismaCategoriaYMismoMes() {
        ValidadorDuplicados validador = new ValidadorDuplicados();
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setCategoria(CategoriaGasto.CAPACITACION);
        solicitud.setFechaGasto(LocalDate.of(2026, 5, 10));

        SolicitudReembolso pendiente = new SolicitudReembolso();
        pendiente.setCategoria(CategoriaGasto.CAPACITACION);
        pendiente.setFechaGasto(LocalDate.of(2026, 5, 20));

        ResultadoValidacion res = validador.validarPropio(solicitud, List.of(pendiente));
        assertFalse(res.valida());
        assertEquals("Ya existe una solicitud pendiente de la misma categoria en el periodo", res.motivoRechazo());
    }
}