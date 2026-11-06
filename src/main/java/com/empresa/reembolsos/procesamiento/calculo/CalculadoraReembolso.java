package com.empresa.reembolsos.procesamiento.calculo;

import java.math.BigDecimal;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;

public interface CalculadoraReembolso {
    CategoriaGasto getCategoria();
    BigDecimal calcular(SolicitudReembolso solicitud);
}