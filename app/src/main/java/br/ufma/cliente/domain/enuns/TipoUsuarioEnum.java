package br.ufma.cliente.domain.enuns;

public enum TipoUsuarioEnum {

    ADMINISTRADOR(1L),
    PEDESTRE(2L);

    private Long value;

    TipoUsuarioEnum(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

}
