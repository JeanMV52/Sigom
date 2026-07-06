package com.udla.Sigom.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @DescriptionsList(descriptionProperties = "nombre")
    Cliente cliente;

    @Column(length = 10, unique = true, nullable = false)
    @Required
    String placa;

    @Column(length = 30, unique = true, nullable = false)
    @Required
    String numChasis;

    @Column(length = 30, nullable = false)
    @Required
    String marca;

    @Column(length = 30, nullable = false)
    @Required
    String modelo;

    @Column(length = 4)
    Integer anio;

    @PreRemove
    private void preRemove() {
        if (tieneOrdenes()) {
            throw new javax.validation.ValidationException(
                "No se puede eliminar el vehículo con placa '" + this.placa +
                "' porque tiene Órdenes de Trabajo asociadas."
            );
        }
    }

    private boolean tieneOrdenes() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM OrdenTrabajo o WHERE o.vehiculo.id = :id",
                Long.class
            ).setParameter("id", this.id).getSingleResult();
            return count > 0;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
