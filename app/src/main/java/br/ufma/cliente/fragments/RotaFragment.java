package br.ufma.cliente.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import br.ufma.cliente.R;
import br.ufma.cliente.activity.MainActivity;
import br.ufma.cliente.domain.model.UsuarioLocalizacao;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.Publisher;
import br.ufma.lsdi.cddl.Subscriber;
import butterknife.ButterKnife;

/**
 * Created by Leeo on 04/04/2017.
 */

public class RotaFragment extends SupportMapFragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public Context context;

    private GoogleMap mMap;
    private LatLng origem, destino;
    private static final String GOOGLE_KEY_DIRECTIONS = "AIzaSyB299emCUuKMkZHXIHQc5u1Po7ZnrEA3S0";

    private final CDDL cddl = CDDL.getInstance();
    private final String clientId = "ivan.rodrigues@lsdi.ufma.br";
    private Subscriber sub;
    private Publisher pub;
    private List<String> sensorList;

    private final String PREFS_PRIVATE = "PREFS_PRIVATE";
    private SharedPreferences sharedPreferences;
    private UsuarioLocalizacao usuarioLocalizacao;

    public static RotaFragment newInstance(String param1, String param2) {
        RotaFragment fragment = new RotaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RotaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        usuarioLocalizacao = (UsuarioLocalizacao) bundle.getSerializable("usuarioLocalizacao");
        getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setOnMapClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        //origem = new LatLng(-2.5497997, -44.2538819);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (usuarioLocalizacao != null) {
            rota(usuarioLocalizacao);
        } else {
            Toast.makeText(getActivity(), "Erro ao buscar a rota", Toast.LENGTH_SHORT).show();
        }
    }


    private void rota(UsuarioLocalizacao usuarioLocalizacao) {
        GoogleDirection.withServerKey(GOOGLE_KEY_DIRECTIONS)
                .from(new LatLng(usuarioLocalizacao.getTrajeto().getLatitudeInicial(), usuarioLocalizacao.getTrajeto().getLongitudeInicial()))
                .to(new LatLng(usuarioLocalizacao.getTrajeto().getLatitudeFinal(), usuarioLocalizacao.getTrajeto().getLongitudeFinal()))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            // Do something
                            mMap.clear();
                            Route route = direction.getRouteList().get(0);
                            int legCount = route.getLegList().size();
                            for (int index = 0; index < legCount; index++) {
                                Leg leg = route.getLegList().get(index);
                                mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()).title(leg.getStartAddress()));
                                if (index == legCount - 1) {
                                    mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title(leg.getEndAddress()));
                                }
                                List<Step> stepList = leg.getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(),
                                        stepList, 5, Color.LTGRAY, 3, Color.BLUE);
                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    mMap.addPolyline(polylineOption);
                                }
                            }
                            setCameraWithCoordinationBounds(route);

                        } else {
                            // Do something
                            Toast.makeText(getActivity(), "Erro na conexão", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                        t.printStackTrace();
                    }
                });

    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

}
