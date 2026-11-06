package com.empresa.reembolsos.procesamiento.calculo;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;

@Component
public class CalculadoraViaje implements CalculadoraReembolso {
    private static final BigDecimal TOPE_DIARIO = new BigDecimal("150000");

    @Override public CategoriaGasto getCategoria() { return CategoriaGasto.VIAJE; }

    @Override
    public BigDecimal calcular(SolicitudReembolso solicitud) {
        var topeTotal = TOPE_DIARIO.multiply(BigDecimal.valueOf(solicitud.getDiasViaje()));
        return solicitud.getMontoSolicitado().min(topeTotal);
    }
}