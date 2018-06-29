package br.ufma.cliente.service;

import java.util.List;

import br.ufma.cliente.domain.model.TipoUsuario;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TipoUsuarioService {


    @GET("listarTipoUsuario")
    Call<List<TipoUsuario>> getTipoUsuario();

}
