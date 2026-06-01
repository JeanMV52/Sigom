package com.udla.Sigom.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
public class Mecanico {

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

    @Enumerated(EnumType.STRING)
    private EstadoMecanico estado;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public EstadoMecanico getEstado() { return estado; }
    public void setEstado(EstadoMecanico estado) { this.estado = estado; }
}