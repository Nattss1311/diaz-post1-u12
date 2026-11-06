package com.empresa.reembolsos.procesamiento.calculo;

import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadorasTest {

    @Test
    void testCalculadoraViajeMultiplicaPorDias() {
        CalculadoraViaje calc = new CalculadoraViaje();
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setDiasViaje(3);
        solicitud.setMontoSolicitado(new BigDecimal("500000")); // Tope: 3 días * 150000 = 450000

        BigDecimal resultado = calc.calcular(solicitud);
        assertEquals(new BigDecimal("450000"), resultado);
    }

    @Test
    void testCalculadoraRepresentacionAplicaTopeMensual() {
        CalculadoraRepresentacion calc = new CalculadoraRepresentacion();
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setMontoSolicitado(new BigDecimal("2500000")); // Tope mensual: 2000000

        BigDecimal resultado = calc.calcular(solicitud);
        assertEquals(new BigDecimal("2000000"), resultado);
    }

    @Test
    void testCalculadoraCapacitacionAplicaOchentaPorCiento() {
        CalculadoraCapacitacion calc = new CalculadoraCapacitacion();
        SolicitudReembolso solicitud = new SolicitudReembolso();
        solicitud.setMontoSolicitado(new BigDecimal("1000000")); // 80% = 800000 (menor al tope de 3000000)

        BigDecimal resultado = calc.calcular(solicitud);
        assertEquals(new BigDecimal("800000.0"), resultado);
    }
}