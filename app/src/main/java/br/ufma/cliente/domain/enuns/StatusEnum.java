package br.ufma.cliente.domain.enuns;

public enum StatusEnum {

    AGUARDANDO_INICIO(1L),
    ANDANDO(2L),
    CORRENDO(3L),
    PARADO(4L),
    FINALIZADO(5L);

    private Long value;

    StatusEnum(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

}
