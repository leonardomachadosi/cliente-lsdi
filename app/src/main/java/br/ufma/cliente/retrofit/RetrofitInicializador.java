package br.ufma.cliente.retrofit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.ufma.cliente.service.TipoUsuarioService;
import br.ufma.cliente.service.TrajetoService;
import br.ufma.cliente.service.UsuarioLocalizacaoService;
import br.ufma.cliente.service.UsuarioService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitInicializador {
    private Retrofit retrofit;

    public RetrofitInicializador() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl("http://extranet1.seap.ma.gov.br:8080/sigame/API/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

    public TipoUsuarioService getTipoUsuario() {
        return retrofit.create(TipoUsuarioService.class);
    }

    public UsuarioService salvarUsuario() {
        return retrofit.create(UsuarioService.class);
    }

    public UsuarioService login() {
        return retrofit.create(UsuarioService.class);
    }

    public UsuarioLocalizacaoService getTrajeto() {
        return retrofit.create(UsuarioLocalizacaoService.class);
    }

    public UsuarioLocalizacaoService salvarTrajeto() {
        return retrofit.create(UsuarioLocalizacaoService.class);
    }
}
