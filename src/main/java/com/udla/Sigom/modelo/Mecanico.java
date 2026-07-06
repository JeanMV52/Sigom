package com.udla.Sigom.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Mecanico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    Long id;

    @Column(length = 10, unique = true, nullable = false)
    @Required
    String cedula;

    @Column(length = 100, nullable = false)
    @Required
    String nombre;

    @Column(length = 15)
    String telefono;

    @Enumerated(EnumType.STRING)
    @Required
    EstadoMecanico estado;

    @PrePersist
    private void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoMecanico.ACTIVO;
        }
    }

    @PreRemove
    private void preRemove() {
        if (hasOrdenesTrabajo()) {
            throw new javax.validation.ValidationException(
                "No se puede eliminar el mecánico '" + this.nombre +
                "' porque tiene Órdenes de Trabajo asociadas."
            );
        }
    }

    private boolean hasOrdenesTrabajo() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM OrdenTrabajo o WHERE o.mecanico.id = :id",
                Long.class
            ).setParameter("id", this.id).getSingleResult();
            return count > 0;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}