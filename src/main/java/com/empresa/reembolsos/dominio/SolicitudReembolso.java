package com.empresa.reembolsos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class SolicitudReembolso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String empleadoId;
    
    @Enumerated(EnumType.STRING)
    private CategoriaGasto categoria;
    
    private BigDecimal montoSolicitado;
    private BigDecimal montoAprobado;
    private LocalDate fechaGasto;
    private int diasViaje;
    private boolean documentosSoporte;
    
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    public SolicitudReembolso() {
    }

    public SolicitudReembolso(Long id, String empleadoId, CategoriaGasto categoria, 
                              BigDecimal montoSolicitado, BigDecimal montoAprobado, 
                              LocalDate fechaGasto, int diasViaje, 
                              boolean documentosSoporte, EstadoSolicitud estado) {
        this.id = id;
        this.empleadoId = empleadoId;
        this.categoria = categoria;
        this.montoSolicitado = montoSolicitado;
        this.montoAprobado = montoAprobado;
        this.fechaGasto = fechaGasto;
        this.diasViaje = diasViaje;
        this.documentosSoporte = documentosSoporte;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(String empleadoId) { this.empleadoId = empleadoId; }

    public CategoriaGasto getCategoria() { return categoria; }
    public void setCategoria(CategoriaGasto categoria) { this.categoria = categoria; }

    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public void setMontoSolicitado(BigDecimal montoSolicitado) { this.montoSolicitado = montoSolicitado; }

    public BigDecimal getMontoAprobado() { return montoAprobado; }
    public void setMontoAprobado(BigDecimal montoAprobado) { this.montoAprobado = montoAprobado; }

    public LocalDate getFechaGasto() { return fechaGasto; }
    public void setFechaGasto(LocalDate fechaGasto) { this.fechaGasto = fechaGasto; }

    public int getDiasViaje() { return diasViaje; }
    public void setDiasViaje(int diasViaje) { this.diasViaje = diasViaje; }

    public boolean isDocumentosSoporte() { return documentosSoporte; }
    public void setDocumentosSoporte(boolean documentosSoporte) { this.documentosSoporte = documentosSoporte; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}