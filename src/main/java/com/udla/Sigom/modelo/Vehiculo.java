package com.udla.Sigom.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @DescriptionsList(descriptionProperties = "nombre")
    private Cliente cliente;

    @Column(length = 10, unique = true, nullable = false)
    @Required
    private String placa;

    @Column(length = 30, unique = true, nullable = false)
    @Required
    private String numChasis;

    @Column(length = 30, nullable = false)
    @Required
    private String marca;

    @Column(length = 30, nullable = false)
    @Required
    private String modelo;

    @Column(length = 4)
    private Integer anio;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getNumChasis() { return numChasis; }
    public void setNumChasis(String numChasis) { this.numChasis = numChasis; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
}
