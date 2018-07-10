package br.ufma.cliente.domain.model.auxiliary;

import java.io.Serializable;


public class Parado implements Serializable {

    private Long id;
    private Double latitude;
    private Double longitude;
    private Integer segundo;
    private Long codTrajeto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getSegundo() {
        return segundo;
    }

    public void setSegundo(Integer segundo) {
        this.segundo = segundo;
    }

    public Long getCodTrajeto() {
        return codTrajeto;
    }

    public void setCodTrajeto(Long codTrajeto) {
        this.codTrajeto = codTrajeto;
    }
}
