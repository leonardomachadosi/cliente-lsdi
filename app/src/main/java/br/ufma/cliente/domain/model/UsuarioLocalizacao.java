package br.ufma.cliente.domain.model;

import java.io.Serializable;
import java.util.Date;


public class UsuarioLocalizacao implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String data;

    private Localizacao localizacao;

    private Usuario usuario;
    private Trajeto trajeto;

    private Status status;

    public UsuarioLocalizacao(String data, Localizacao localizacao, Usuario usuario, Trajeto trajeto, Status status) {
        this.data = data;
        this.localizacao = localizacao;
        this.usuario = usuario;
        this.trajeto = trajeto;
        this.status = status;
    }

    public UsuarioLocalizacao(Long id) {
        this.id = id;
    }

    public UsuarioLocalizacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Trajeto getTrajeto() {
        return trajeto;
    }

    public void setTrajeto(Trajeto trajeto) {
        this.trajeto = trajeto;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}