package br.ufma.cliente.service;

import java.util.List;

import br.ufma.cliente.domain.model.Usuario;
import br.ufma.cliente.domain.model.UsuarioLocalizacao;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioLocalizacaoService {


    @POST("listarTrajetoPorUsuario")
    Call<List<UsuarioLocalizacao>> getTrajeto(@Body Usuario usuario);

    @POST("salvarTrajeto")
    Call<UsuarioLocalizacao> salvarTrajeto(@Body UsuarioLocalizacao UsuarioLocalizacao);

}
