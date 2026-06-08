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
    private Long id;

    @Column(length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoOT estado;

    @Required
    private Date fechaIngreso;

    private Date fechaCierre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @DescriptionsList(descriptionProperties = "placa")
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList(descriptionProperties = "nombre", condition = "${estado} = 'ACTIVO'")
    private Mecanico mecanico;

    private Double costoManoObra;

    @ReadOnly
    private Double costoTotal;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<DetalleOT> detalles;

    private static final int MAX_OT_EN_PROCESO = 3;

    @PrePersist @PreUpdate
    private void validarYCalcular() {
        if (this.mecanico != null) {

            if (EstadoMecanico.INACTIVO.equals(this.mecanico.getEstado())) {
                throw new ValidationException(
                        "No se puede asignar la OT al mecánico '"
                                + this.mecanico.getNombre()
                                + "' porque está INACTIVO."
                );
            }

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
                            "El mecánico '" + this.mecanico.getNombre()
                                    + "' ya tiene " + otsEnProceso
                                    + " órdenes EN_PROCESO. "
                                    + "Máximo permitido: " + MAX_OT_EN_PROCESO + "."
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
        double manoObra = (this.costoManoObra != null) ? this.costoManoObra : 0;
        this.costoTotal = manoObra + subtotalRepuestos;

        if (EstadoOT.TERMINADA.equals(this.estado) && this.fechaCierre == null) {
            this.fechaCierre = new Date();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstadoOT getEstado() { return estado; }
    public void setEstado(EstadoOT estado) { this.estado = estado; }
    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public Date getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(Date fechaCierre) { this.fechaCierre = fechaCierre; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public Mecanico getMecanico() { return mecanico; }
    public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }
    public Double getCostoManoObra() { return costoManoObra; }
    public void setCostoManoObra(Double costoManoObra) { this.costoManoObra = costoManoObra; }
    public Double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Double costoTotal) { this.costoTotal = costoTotal; }
    public Collection<DetalleOT> getDetalles() { return detalles; }
    public void setDetalles(Collection<DetalleOT> detalles) { this.detalles = detalles; }
}