package br.ufma.cliente.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import br.ufma.cliente.R;
import br.ufma.cliente.domain.model.Usuario;
import br.ufma.cliente.retrofit.RetrofitInicializador;
import br.ufma.cliente.util.Criptografia;
import br.ufma.cliente.util.PermissionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netty.util.internal.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private final String PREFS_PRIVATE = "PREFS_PRIVATE";
    private SharedPreferences sharedPreferences;
    private Boolean isLoggedIn;

    @BindView(R.id.input_cpf)
    public EditText edEmail;

    @BindView(R.id.input_password)
    public EditText edSenha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        preferencies();
        sharedPreferences = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);
    }

    @OnClick(R.id.btn_login)
    public void logar() {
        String email = edEmail.getText().toString();
        String senha = edSenha.getText().toString();

        if (senha.isEmpty()) {
            Toast.makeText(this, "A senha é necessária", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "o E-mail é necessário", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(Criptografia.md5(senha));

        try {
            login(usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Solicita as permissÃµes
        String[] permissoes = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        boolean teste = PermissionUtils.validate(this, 0, permissoes);

    }

    @OnClick(R.id.link_signup)
    public void goToLogin() {
        startActivity(new Intent(this, CadastroActivity.class));
    }

    private void preferencies() {
        sharedPreferences = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false);
        if (isLoggedIn) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void login(Usuario usuario) throws Exception {

        try {
            Gson gson = new Gson();
            Log.d("login", gson.toJson(usuario));
            Call<Usuario> call = new RetrofitInicializador().login().login(usuario);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    Usuario user = response.body();
                    if (user != null) {
                        if (user.getId() != null) {
                            Toast.makeText(getApplicationContext(), "Usuario Salvo com Sucesso", Toast.LENGTH_SHORT).show();
                            ObjectMapper mapper = new ObjectMapper();
                            String objeto = null;
                            try {
                                objeto = mapper.writeValueAsString(user);
                            } catch (Exception e) {
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
                    Toast.makeText(getApplicationContext(), "Erro na conexão", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erro na conexão", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


}

