package com.udla.Sigom.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
public class DetalleOT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrdenTrabajo ordenTrabajo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @DescriptionsList(descriptionProperties = "nombre")
    private Repuesto repuesto;

    @Required
    private Integer cantidad;

    @ReadOnly
    private Double precioUnitario;

    @ReadOnly
    private Double subtotal;

    @PrePersist @PreUpdate
    private void procesarDetalle() {
        if (this.repuesto != null) {
            this.precioUnitario = this.repuesto.getPrecioUnitario();
            if (this.cantidad != null) {
                this.subtotal = this.precioUnitario * this.cantidad;
            }
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrdenTrabajo getOrdenTrabajo() { return ordenTrabajo; }
    public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) { this.ordenTrabajo = ordenTrabajo; }
    public Repuesto getRepuesto() { return repuesto; }
    public void setRepuesto(Repuesto repuesto) { this.repuesto = repuesto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}