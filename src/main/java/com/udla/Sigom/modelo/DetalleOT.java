package com.udla.Sigom.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Embeddable
@Getter @Setter
public class DetalleOT {

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList(descriptionProperties = "nombre")
    Repuesto repuesto;

    @Required
    Integer cantidad;

    @ReadOnly
    Double precioUnitario;

    @ReadOnly
    Double subtotal;

    @PrePersist @PreUpdate
    private void procesarDetalle() {
        if (cantidad != null && cantidad < 0) {
            throw new javax.validation.ValidationException(
                "La cantidad no puede ser negativa."
            );
        }
        if (this.repuesto != null) {
            this.precioUnitario = this.repuesto.getPrecioUnitario();
            if (this.cantidad != null && this.cantidad > 0) {
                this.subtotal = (this.precioUnitario != null && this.precioUnitario >= 0)
                    ? this.precioUnitario * this.cantidad
                    : null;
            }
        }
    }
}