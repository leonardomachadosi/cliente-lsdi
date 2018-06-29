package br.ufma.cliente.domain.model;

import java.io.Serializable;

public class Status implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nome;

    public Status(Long id) {
        this.id = id;
    }

    public Status() {
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
