package com.udla.Sigom.modelo;
import java.util.*;
import org.openxava.annotations.*;
import javax.persistence.*;

@Entity
@View(members = "id; cedula, nombre; telefono, correo")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(length = 10, unique = true, nullable = false)
    @Required
    private String cedula;

    @Column(length = 100, nullable = false)
    @Required
    private String nombre;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @ListProperties("placa, marca, modelo, anio")
    private Collection<Vehiculo> vehiculos;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public Collection<Vehiculo> getVehiculos() { return vehiculos; }
    public void setVehiculos(Collection<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }
}