package br.ufma.cliente.service;

import br.ufma.cliente.domain.model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {

    @POST("salvarUsuario")
    Call<Usuario> salvarUsuario(@Body Usuario usuario);

    @POST("login")
    Call<Usuario> login(@Body Usuario usuario);

}
