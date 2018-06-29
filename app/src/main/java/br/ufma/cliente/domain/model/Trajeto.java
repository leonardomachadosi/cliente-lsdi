package br.ufma.cliente.domain.model;

import java.io.Serializable;
import java.util.Date;

import br.ufma.cliente.util.DateUtil;

public class Trajeto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    private Double latitudeInicial;

    private Double longitudeInicial;

    private Double latitudeFinal;

    private Double longitudeFinal;

    private String dataTrajeto;

    private Usuario usuario;

    private String descricaoOrigem;

    private String descricaoDestino;


    public Trajeto(Double latitudeInicial, Double longitudeInicial, Double latitudeFinal,
                   Double longitudeFinal, String dataTrajeto, Usuario usuario,
                   String descricaoOrigem, String descricaoDestino) {
        this.latitudeInicial = latitudeInicial;
        this.longitudeInicial = longitudeInicial;
        this.latitudeFinal = latitudeFinal;
        this.longitudeFinal = longitudeFinal;
        this.dataTrajeto = dataTrajeto;
        this.usuario = usuario;
        this.descricaoOrigem = descricaoOrigem;
        this.descricaoDestino = descricaoDestino;
    }

    public Trajeto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitudeInicial() {
        return latitudeInicial;
    }

    public void setLatitudeInicial(Double latitudeInicial) {
        this.latitudeInicial = latitudeInicial;
    }

    public Double getLongitudeInicial() {
        return longitudeInicial;
    }

    public void setLongitudeInicial(Double longitudeInicial) {
        this.longitudeInicial = longitudeInicial;
    }

    public Double getLatitudeFinal() {
        return latitudeFinal;
    }

    public void setLatitudeFinal(Double latitudeFinal) {
        this.latitudeFinal = latitudeFinal;
    }

    public Double getLongitudeFinal() {
        return longitudeFinal;
    }

    public void setLongitudeFinal(Double longitudeFinal) {
        this.longitudeFinal = longitudeFinal;
    }

    public String getDataTrajeto() {
        return dataTrajeto;
    }

    public void setDataTrajeto(String dataTrajeto) {
        this.dataTrajeto = dataTrajeto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDescricaoOrigem() {
        return descricaoOrigem;
    }

    public void setDescricaoOrigem(String descricaoOrigem) {
        this.descricaoOrigem = descricaoOrigem;
    }

    public String getDescricaoDestino() {
        return descricaoDestino;
    }

    public void setDescricaoDestino(String descricaoDestino) {
        this.descricaoDestino = descricaoDestino;
    }
}
