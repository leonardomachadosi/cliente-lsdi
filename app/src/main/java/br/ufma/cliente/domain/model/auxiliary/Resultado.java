package br.ufma.cliente.domain.model.auxiliary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Resultado implements Serializable {

    private Long codigoTrajeto;
    private Correndo correndo;
    private Andando andando;
    private List<Parado> parados;

    public Resultado(Long codigoTrajeto) {
        this.codigoTrajeto = codigoTrajeto;
    }

    public Resultado() {
    }

    public Correndo getCorrendo() {
        return correndo;
    }

    public void setCorrendo(Correndo correndo) {
        this.correndo = correndo;
    }

    public Andando getAndando() {
        return andando;
    }

    public void setAndando(Andando andando) {
        this.andando = andando;
    }

    public List<Parado> getParados() {
        if (parados == null) {
            parados = new ArrayList<Parado>();
        }
        return parados;
    }

    public void setParados(List<Parado> parados) {
        this.parados = parados;
    }

    public Long getCodigoTrajeto() {
        return codigoTrajeto;
    }

    public void setCodigoTrajeto(Long codigoTrajeto) {
        this.codigoTrajeto = codigoTrajeto;
    }
}
