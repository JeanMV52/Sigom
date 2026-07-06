package com.udla.Sigom.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Repuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    Long id;

    @Column(length = 20, unique = true, nullable = false)
    @Required
    String codigo;

    @Column(length = 100, nullable = false)
    @Required
    String nombre;

    @Required
    Double precioUnitario;

    @Required
    @DisplaySize(9)
    Integer cantDisponible;

    @PrePersist @PreUpdate
    private void validarCambios() {
        if (cantDisponible < 0) {
            throw new javax.validation.ValidationException(
                "La cantidad disponible no puede ser negativa."
            );
        }
        if (precioUnitario < 0) {
            throw new javax.validation.ValidationException(
                "El precio unitario no puede ser negativo."
            );
        }
        if (this.id != null && tieneProcesosEnCurso()) {
            throw new javax.validation.ValidationException(
                "No se puede modificar el repuesto '" + this.nombre +
                "' porque está asociado a Órdenes de Trabajo en proceso."
            );
        }
    }

    @PreRemove
    private void preRemove() {
        if (tieneAsociaciones()) {
            throw new javax.validation.ValidationException(
                "No se puede eliminar el repuesto '" + this.nombre +
                "' porque está asociado a Órdenes de Trabajo."
            );
        }
    }

    public String getEstadoStock() {
        if (this.cantDisponible != null && this.cantDisponible < 5) {
            return "BAJO";
        }
        return "OK";
    }

    private boolean tieneProcesosEnCurso() {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("default");
            EntityManager em = emf.createEntityManager();
            try {
                Long count = em.createQuery(
                    "SELECT COUNT(o) FROM OrdenTrabajo o " +
                    "WHERE (o.estado = 'EN_PROCESO' OR o.estado = 'PENDIENTE') " +
                    "AND EXISTS (SELECT 1 FROM o.detalles d WHERE d.repuesto.id = :repuestoId)",
                    Long.class
                ).setParameter("repuestoId", this.id).getSingleResult();
                return count > 0;
            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean tieneAsociaciones() {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("default");
            EntityManager em = emf.createEntityManager();
            try {
                Long count = em.createQuery(
                    "SELECT COUNT(o) FROM OrdenTrabajo o " +
                    "WHERE EXISTS (SELECT 1 FROM o.detalles d WHERE d.repuesto.id = :repuestoId)",
                    Long.class
                ).setParameter("repuestoId", this.id).getSingleResult();
                return count > 0;
            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
