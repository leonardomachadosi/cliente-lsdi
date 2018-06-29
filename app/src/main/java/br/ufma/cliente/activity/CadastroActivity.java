package br.ufma.cliente.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import br.ufma.cliente.R;
import br.ufma.cliente.domain.enuns.TipoUsuarioEnum;
import br.ufma.cliente.domain.model.Pessoa;
import br.ufma.cliente.domain.model.TipoUsuario;
import br.ufma.cliente.domain.model.Usuario;
import br.ufma.cliente.retrofit.RetrofitInicializador;
import br.ufma.cliente.util.Criptografia;
import br.ufma.cliente.util.Mask;
import br.ufma.cliente.util.PermissionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    @BindView(R.id.edtNome)
    public EditText edtNome;
    @BindView(R.id.edtEmail)
    public EditText edtEmail;
    @BindView(R.id.edtTelefone)
    public EditText edtTelefone;
    @BindView(R.id.edtSenha)
    public EditText edtSenha;
    @BindView(R.id.edtConfirmaSenha)
    public EditText edtConfSenha;

    private Usuario usuario;

    public static final String TAG = "LoginActivity";
    private final String PREFS_PRIVATE = "PREFS_PRIVATE";
    private SharedPreferences sharedPreferences;
    private Boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        ButterKnife.bind(this);
        edtTelefone.addTextChangedListener(Mask.insert("(##)#########", edtTelefone));
        sharedPreferences = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);

    }

    @OnClick(R.id.btnRegistrar)
    public void cadastrarUsuario(){
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(edtNome.getText().toString().toUpperCase().trim());
        pessoa.setTelefone(edtTelefone.getText().toString());
        Usuario usuario = new Usuario();
        usuario.setEmail(edtEmail.getText().toString().trim());
        usuario.setPessoa(pessoa);
        usuario.setTipoUsuario(new TipoUsuario(TipoUsuarioEnum.PEDESTRE.getValue()));
        String confSenha = edtConfSenha.getText().toString();
        String senha = edtSenha.getText().toString();

        if (pessoa.getNome().isEmpty()){
            Toast.makeText(this, "Nome Obrigatório", Toast.LENGTH_SHORT).show();
            edtNome.setError("");
            return;
        }


        if (usuario.getEmail().isEmpty()){
            Toast.makeText(this, "Email Obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.isEmpty() && !confSenha.isEmpty() ){
            if(senha.equals(confSenha)){
                usuario.setSenha(Criptografia.md5(senha.toString().trim()));
            }else{
                Toast.makeText(this, "Senha não confere", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(this, "Senha Obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }


        try{
            Gson gson = new Gson();

            Log.d("obj", gson.toJson(usuario));
            Call<Usuario> call = new RetrofitInicializador().salvarUsuario().salvarUsuario(usuario);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                    Usuario user = response.body();
                    if (user != null ){
                        if(user.getId() != null){
                            Toast.makeText(getApplicationContext(), "Usuario Salvo com Sucesso", Toast.LENGTH_SHORT).show();
                            ObjectMapper mapper = new ObjectMapper();
                            String objeto = null;
                            try {
                                objeto = mapper.writeValueAsString(user);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            SharedPreferences.Editor preEditor = sharedPreferences.edit();
                            preEditor.putString("usuario", objeto);
                            preEditor.putBoolean("IsLoggedIn", true);
                            preEditor.commit();

                            startActivity(intent);
                            finish();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    t.printStackTrace();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
