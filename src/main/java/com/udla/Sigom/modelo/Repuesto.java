package com.udla.Sigom.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
public class Repuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    @Required
    private String codigo;

    @Column(length = 100, nullable = false)
    @Required
    private String nombre;

    @Required
    private Double precioUnitario;

    @Required
    private Integer cantDisponible;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
    public Integer getCantDisponible() { return cantDisponible; }
    public void setCantDisponible(Integer cantDisponible) { this.cantDisponible = cantDisponible; }
}
