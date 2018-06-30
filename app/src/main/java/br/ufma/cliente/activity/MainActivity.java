package br.ufma.cliente.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufma.cliente.domain.model.Pessoa;
import br.ufma.cliente.domain.model.Usuario;
import br.ufma.cliente.fragments.MapsFragment;
import br.ufma.cliente.R;
import br.ufma.cliente.fragments.RotaFragment;
import br.ufma.cliente.fragments.TrajetoFragment;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    private final String PREFS_PRIVATE = "PREFS_PRIVATE";
    private SharedPreferences sharedPreferences;

    private Usuario usuario;
    private Pessoa pessoa;
    public static MenuItem menuItem;


    private TextView tName;
    private TextView tEmail;

    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String objeto = sharedPreferences.getString("usuario", "");

            usuario = new Usuario();
            usuario = objectMapper.readValue(objeto, Usuario.class);

            tName = (TextView) header.findViewById(R.id.tvNome);
            tEmail = (TextView) header.findViewById(R.id.tvEmail);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!TextUtils.isEmpty(usuario.getEmail())) {
            tEmail.setText(usuario.getEmail().toLowerCase());
            tName.setText(usuario.getPessoa().getNome());
        }

        changeFragment(new TrajetoFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menuItem = menu.findItem(R.id.action_settings);
        menuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_trajeto:
                changeFragment(new TrajetoFragment());
                break;
            case R.id.nav_map:
                //changeFragment(new MapsFragment());
                startActivity(new Intent(this, TesteActivity.class));
                break;
            case R.id.nav_sair:
                logout();
                break;
        }

        if (id == R.id.nav_trajeto) {
            // Handle the camera action
            changeFragment(new TrajetoFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", usuario);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    private void logout() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("usuario", "");
        prefsEditor.putBoolean("IsLoggedIn", false);
//        prefsEditor.putBoolean("IsWelcomeVisited", false);
        prefsEditor.commit();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }



}
