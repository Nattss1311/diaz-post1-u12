package com.empresa.reembolsos.procesamiento.calculo;

import java.math.BigDecimal;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;

/**
 * Estrategia de calculo del monto aprobado para una categoria de gasto (patron Strategy, RF2).
 * <p>
 * Cada implementacion encapsula la formula de una sola categoria. Para soportar una categoria
 * nueva basta con crear otra clase {@code @Component} que implemente esta interfaz:
 * {@link SelectorCalculadora} la registra automaticamente sin modificar codigo existente (OCP).
 */
public interface CalculadoraReembolso {

    /**
     * @return categoria de gasto que atiende esta estrategia; se usa como clave en {@link SelectorCalculadora}
     */
    CategoriaGasto getCategoria();

    /**
     * Calcula el monto aprobado aplicando la formula propia de la categoria.
     *
     * @param solicitud solicitud ya validada por la cadena de validadores
     * @return monto aprobado, nunca mayor que el monto solicitado
     */
    BigDecimal calcular(SolicitudReembolso solicitud);
}