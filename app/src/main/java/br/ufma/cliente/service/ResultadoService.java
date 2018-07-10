package br.ufma.cliente.service;

import br.ufma.cliente.domain.model.auxiliary.Resultado;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ResultadoService {

    @POST("getStatusUsuario")
    Call<Resultado> getStatusUsuario(@Body Resultado resultado);
}
