package br.ufma.cliente.domain.model;

import java.io.Serializable;

public class TipoUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nome;

    public TipoUsuario(Long id) {
        this.id = id;
    }

    public TipoUsuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
