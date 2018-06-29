package br.ufma.cliente.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.ufma.cliente.R;
import br.ufma.cliente.activity.MainActivity;
import br.ufma.cliente.adapter.TrajetoAdapter;
import br.ufma.cliente.domain.model.Trajeto;
import br.ufma.cliente.domain.model.Usuario;
import br.ufma.cliente.domain.model.UsuarioLocalizacao;
import br.ufma.cliente.retrofit.RetrofitInicializador;
import br.ufma.cliente.service.OnUpdateListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leeo on 04/04/2017.
 */

public class TrajetoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public Context context;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    @BindView(R.id.swipeContainer)
    public SwipeRefreshLayout swipeRefreshLayout;


    private List<UsuarioLocalizacao> usuarioLocalizacaos = new ArrayList<>();

    private Usuario usuario;

    public static TrajetoFragment newInstance(String param1, String param2) {
        TrajetoFragment fragment = new TrajetoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TrajetoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trajeto, container, false);
        ButterKnife.bind(this, view);
        // presos = new ArrayList<Preso>();
        //presos = (List<Preso>) bundle.getSerializable("presos");
        usuarioLocalizacaos = new ArrayList<UsuarioLocalizacao>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_trajeto);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        try {
            Bundle bundle = getArguments();
            usuario = (Usuario) bundle.getSerializable("usuario");
            getTrajetos();
        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getTrajetos();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.toolbar.setTitle("Trajetos");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void getTrajetos() throws Exception {
        swipeRefreshLayout.setRefreshing(true);
        try {

            Call<List<UsuarioLocalizacao>> call = new RetrofitInicializador().getTrajeto().getTrajeto(usuario);
            call.enqueue(new Callback<List<UsuarioLocalizacao>>() {
                @Override
                public void onResponse(Call<List<UsuarioLocalizacao>> call, Response<List<UsuarioLocalizacao>> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.body() != null) {
                        usuarioLocalizacaos = response.body();
                        if (usuarioLocalizacaos.size() > 0) {
                            mAdapter = new TrajetoAdapter(context, usuarioLocalizacaos, updateListener());
                            mRecyclerView.setAdapter(mAdapter);

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<UsuarioLocalizacao>> call, Throwable t) {
                    t.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }


    }

    private OnUpdateListener updateListener() {
        return new OnUpdateListener() {
            @Override
            public void onUpdate(UsuarioLocalizacao usuarioLocalizacao) {
                changeFragment(new RotaFragment(), usuarioLocalizacao);
            }
        };
    }

    private void changeFragment(Fragment fragment, UsuarioLocalizacao usuarioLocalizacao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuarioLocalizacao", usuarioLocalizacao);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }


}
