package com.udla.Sigom.modelo;

import java.util.*;
import javax.persistence.*;
import javax.validation.ValidationException;
import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;

@Setter
@Getter
@Entity
@View(members =
        "General [id, estado; descripcion];" +
                "Asignacion [fechaIngreso, fechaCierre; vehiculo, mecanico];" +
                "Precios [costoManoObra, costoTotal];" +
                "detalles"
)
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    Long id;

    @Column(length = 255)
    String descripcion;

    @Enumerated(EnumType.STRING)
    @Required
    EstadoOT estado;

    @Required
    Date fechaIngreso;

    @ReadOnly
    Date fechaCierre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @DescriptionsList(descriptionProperties = "placa")
    Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @DescriptionsList(descriptionProperties = "nombre", condition = "${estado} = 'ACTIVO'")
    Mecanico mecanico;

    Double costoManoObra;

    @ReadOnly
    Double costoTotal;

    @ElementCollection
    @ListProperties("repuesto.nombre, cantidad, precioUnitario, subtotal")
    Collection<DetalleOT> detalles;

    private static final int MAX_OT_EN_PROCESO = 3;

    @PrePersist
    private void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoOT.PENDIENTE;
        }
        if (this.fechaIngreso == null) {
            this.fechaIngreso = new Date();
        }
        validarYCalcular();
    }

    @PreUpdate
    private void preUpdate() {
        validarYCalcular();
    }

    private void validarYCalcular() {
        if (this.mecanico == null) {
            throw new ValidationException(
                "El mecánico es requerido para crear o actualizar una Orden de Trabajo."
            );
        }

        if (EstadoOT.PENDIENTE.equals(this.estado) && this.id == null) {
            // Primera creación es OK
        } else if (!canTransitionTo(this.estado)) {
            throw new ValidationException(
                "Transición de estado no válida. Las OT deben seguir: Pendiente → En Proceso → Terminada."
            );
        }

        if (EstadoMecanico.INACTIVO.equals(this.mecanico.getEstado())) {
            throw new ValidationException(
                "No se puede asignar la OT al mecánico '" + this.mecanico.getNombre() +
                "' porque está INACTIVO."
            );
        }

        if (EstadoOT.EN_PROCESO.equals(this.estado)) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
            EntityManager emAislado = emf.createEntityManager();
            try {
                Long otActualId = (this.id != null) ? this.id : -1L;
                Long otsEnProceso = emAislado.createQuery(
                        "SELECT COUNT(o) FROM OrdenTrabajo o " +
                                "WHERE o.mecanico.id = :mecanicoId " +
                                "AND o.estado = :estado " +
                                "AND o.id <> :otActualId", Long.class)
                        .setParameter("mecanicoId", this.mecanico.getId())
                        .setParameter("estado", EstadoOT.EN_PROCESO)
                        .setParameter("otActualId", otActualId)
                        .getSingleResult();

                if (otsEnProceso >= MAX_OT_EN_PROCESO) {
                    throw new ValidationException(
                            "El mecánico '" + this.mecanico.getNombre() +
                                    "' ya tiene " + otsEnProceso +
                                    " órdenes EN_PROCESO. Máximo permitido: " + MAX_OT_EN_PROCESO + "."
                    );
                }
            } finally {
                if (emAislado.isOpen()) {
                    emAislado.close();
                }
            }
        }

        double subtotalRepuestos = 0;
        if (detalles != null) {
            for (DetalleOT detalle : detalles) {
                if (detalle.getSubtotal() != null) {
                    subtotalRepuestos += detalle.getSubtotal();
                }
            }
        }
        double manoObra = (this.costoManoObra != null && this.costoManoObra > 0) ? this.costoManoObra : 0;
        this.costoTotal = manoObra + subtotalRepuestos;

        if (EstadoOT.TERMINADA.equals(this.estado) && this.fechaCierre == null) {
            this.fechaCierre = new Date();
        }
    }

    private boolean canTransitionTo(EstadoOT nuevoEstado) {
        if (this.id == null) {
            return EstadoOT.PENDIENTE.equals(nuevoEstado);
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        try {
            OrdenTrabajo existente = em.find(OrdenTrabajo.class, this.id);
            if (existente == null) return false;

            EstadoOT estadoActual = existente.getEstado();

            if (EstadoOT.PENDIENTE.equals(estadoActual)) {
                return EstadoOT.EN_PROCESO.equals(nuevoEstado) || EstadoOT.PENDIENTE.equals(nuevoEstado);
            } else if (EstadoOT.EN_PROCESO.equals(estadoActual)) {
                return EstadoOT.TERMINADA.equals(nuevoEstado) || EstadoOT.EN_PROCESO.equals(nuevoEstado);
            } else if (EstadoOT.TERMINADA.equals(estadoActual)) {
                return EstadoOT.TERMINADA.equals(nuevoEstado);
            }
            return false;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}