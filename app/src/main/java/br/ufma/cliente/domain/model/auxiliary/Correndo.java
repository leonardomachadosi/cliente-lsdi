package br.ufma.cliente.domain.model.auxiliary;

import java.io.Serializable;

public class Correndo implements Serializable {

    private Long codTrajeto;
    private Long codStatus;
    private Double latideInicial;
    private Double longitudeInicial;
    private Double latitudeFinal;
    private Double longitudeFinal;

    public Long getCodTrajeto() {
        return codTrajeto;
    }

    public void setCodTrajeto(Long codTrajeto) {
        this.codTrajeto = codTrajeto;
    }

    public Long getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(Long codStatus) {
        this.codStatus = codStatus;
    }

    public Double getLatideInicial() {
        return latideInicial;
    }

    public void setLatideInicial(Double latideInicial) {
        this.latideInicial = latideInicial;
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
}
