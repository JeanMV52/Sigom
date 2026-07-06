package com.udla.Sigom.modelo;
import java.util.*;
import org.openxava.annotations.*;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
public class Cliente {

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

    @Column(length = 100)
    String correo;

    @OneToMany(mappedBy = "cliente")
    @ListProperties("placa, marca, modelo, anio")
    Collection<Vehiculo> vehiculos;

    @PreRemove
    private void preRemove() {
        if (tieneVehiculos()) {
            throw new javax.validation.ValidationException(
                "No se puede eliminar el cliente '" + this.nombre +
                "' porque tiene vehículos y/o Órdenes de Trabajo asociados."
            );
        }
    }

    private boolean tieneVehiculos() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(v) FROM Vehiculo v WHERE v.cliente.id = :id",
                Long.class
            ).setParameter("id", this.id).getSingleResult();
            return count > 0;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}