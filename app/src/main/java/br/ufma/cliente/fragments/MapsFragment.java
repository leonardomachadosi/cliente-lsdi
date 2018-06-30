package br.ufma.cliente.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.ufma.cliente.R;
import br.ufma.cliente.activity.MainActivity;
import br.ufma.cliente.domain.enuns.StatusEnum;
import br.ufma.cliente.domain.model.Localizacao;
import br.ufma.cliente.domain.model.Status;
import br.ufma.cliente.domain.model.Trajeto;
import br.ufma.cliente.domain.model.Usuario;
import br.ufma.cliente.domain.model.UsuarioLocalizacao;
import br.ufma.cliente.retrofit.RetrofitInicializador;
import br.ufma.cliente.util.DateUtil;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.Callback;
import br.ufma.lsdi.cddl.Monitor;
import br.ufma.lsdi.cddl.Publisher;
import br.ufma.lsdi.cddl.Subscriber;
import br.ufma.lsdi.cddl.message.CommandRequest;
import br.ufma.lsdi.cddl.message.ContextMessage;
import br.ufma.lsdi.cddl.message.MOUUID;
import br.ufma.lsdi.cddl.message.MapEvent;
import br.ufma.lsdi.cddl.message.MonitorToken;
import br.ufma.lsdi.cddl.message.QueryMessage;
import br.ufma.lsdi.cddl.message.QueryResponseMessage;
import br.ufma.lsdi.cddl.message.SensorData;
import br.ufma.lsdi.cddl.message.ServiceList;
import br.ufma.lsdi.cddl.message.ServiceMessage;
import br.ufma.lsdi.cddl.message.TechnologyID;
import br.ufma.lsdi.cddl.type.CDDLConfig;
import br.ufma.lsdi.cddl.type.CEPRule;
import br.ufma.lsdi.cddl.type.ClientId;
import br.ufma.lsdi.cddl.type.Host;
import br.ufma.lsdi.cddl.type.Topic;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private LatLng origem, destino;
    private static final String GOOGLE_KEY_DIRECTIONS = "AIzaSyB299emCUuKMkZHXIHQc5u1Po7ZnrEA3S0";

    private final CDDL cddl = CDDL.getInstance();
    private final String clientId = "ivan.rodrigues@lsdi.ufma.br";
    private Subscriber sub, subscriber;
    private Publisher pub;
    private List<String> sensorList;

    private final String PREFS_PRIVATE = "PREFS_PRIVATE";
    private SharedPreferences sharedPreferences;

    private Localizacao localizacao;

    private String descOrigem, descDestino = "";
    private Usuario usuario;

    private String topicEpl = "";

    QueryMessage queryMessage = null;
    private CDDLConfig config;
    Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
        localizacao = new Localizacao();
        ButterKnife.bind(getActivity());
        gson = new Gson();
        MainActivity.menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_settings) {

                    try {
                        usuario = new Usuario();
                        Bundle bundle = getArguments();
                        usuario = (Usuario) bundle.getSerializable("usuario");

                        Trajeto trajeto = new Trajeto(localizacao.getLatitude(), localizacao.getLongitude(),
                                destino.latitude, destino.longitude, localizacao.getData(),
                                usuario, descOrigem, descDestino);
                        UsuarioLocalizacao usuarioLocalizacao = new UsuarioLocalizacao(trajeto.getDataTrajeto(),
                                localizacao, usuario, trajeto,
                                new Status(StatusEnum.AGUARDANDO_INICIO.getValue()));

                        salvarTrajeto(usuarioLocalizacao);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
    }

    private void salvarTrajeto(UsuarioLocalizacao usuarioLocalizacao) throws Exception {

        try {

            Gson gson = new Gson();
            Log.d("objeto", gson.toJson(usuarioLocalizacao));

            Call<UsuarioLocalizacao> call = new RetrofitInicializador().salvarTrajeto().salvarTrajeto(usuarioLocalizacao);
            call.enqueue(new retrofit2.Callback<UsuarioLocalizacao>() {
                @Override
                public void onResponse(Call<UsuarioLocalizacao> call, Response<UsuarioLocalizacao> response) {

                    if (response.body() != null) {
                        UsuarioLocalizacao novoUsuarioLocalizacao = response.body();
                        if (novoUsuarioLocalizacao.getId() != null) {
                            getActivity().onBackPressed();
                            changeFragment(new TrajetoFragment());
                        }
                    }
                }

                @Override
                public void onFailure(Call<UsuarioLocalizacao> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.toolbar.setTitle("Novo Trajeto");
        //iniciar CDDL
        iniciarCDDL(getActivity());

        //mostrando sensores internos
        mostrarSensores();


        //publicar dado de contexto
        //publisherContext(new ContextMessage("String","location","São Luis"));

        //subscrever tópico
        subscrever("ivan.rodrigues@lsdi.ufma.br/Location");
        // subscreverAcellerometro("ivan.rodrigues@lsdi.ufma.br/BMI160 Accelerometer");


        //iniciar sensores
        sensorList = Arrays.asList("Location");
        //  sensorList = Arrays.asList("K2HH Acceleration");
        startSensores(sensorList);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        //origem = new LatLng(-2.5497997, -44.2538819);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origem, 18));

        destino = latLng;

        GoogleDirection.withServerKey(GOOGLE_KEY_DIRECTIONS)
                .from(origem)
                .to(latLng)
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
                                mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()).title(descOrigem = leg.getStartAddress()));
                                if (index == legCount - 1) {
                                    mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title(descDestino = leg.getEndAddress()));
                                }
                                List<Step> stepList = leg.getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(),
                                        stepList, 5, Color.LTGRAY, 3, Color.BLUE);
                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    mMap.addPolyline(polylineOption);
                                }
                            }
                            setCameraWithCoordinationBounds(route);

                            if (localizacao.getLongitude() != null) {
                                MainActivity.menuItem.setVisible(true);
                            }

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

    //iniciando CDDL
    public void iniciarCDDL(Context context) {

        config = CDDLConfig.builder()
                //.host(Host.of("tcp://lsdi.ufma.br:1883"))
                .host(Host.of("tcp://192.168.100.4:1883"))
                //.host(Host.of("tcp://localhost:1883"))
                .clientId(ClientId.of(clientId))
                .build();

        cddl.init(context, config);
        cddl.startScan();

    }

    //subscreve em um topico
    public void subscrever(String topic) {
        sub = Subscriber.of(cddl);
        sub.setCallback(new Callback() {
            @Override
            public void messageArrived(ContextMessage contextMessage) {
                Gson gson = new Gson();
                //Log.d("subscriber", gson.toJson(contextMessage));
                SensorData sensorData = gson.fromJson(contextMessage.getBody(), SensorData.class);
                Log.d("Location", gson.toJson(sensorData));
                localizacao.setLatitude(sensorData.getSensorValue()[0]);
                localizacao.setLongitude(sensorData.getSensorValue()[1]);
                localizacao.setData(DateUtil.toDate(new Date(), DateUtil.DATA_SEPARADO_POR_TRACO_AMERICANO));
                origem = new LatLng(localizacao.getLatitude(), localizacao.getLongitude());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mMap.setMyLocationEnabled(true);
            }

            @Override
            public void onConnectSuccess() {
                sub.subscribe(Topic.of(topic));
                Log.d("subscriber", "Conectado com sucesso");
            }

            @Override
            public void messageArrived(ServiceList serviceList) {
                Log.d("Service List", gson.toJson(serviceList));
            }

            @Override
            public void onConnectFailure(Throwable exception) {
                Log.d("subscriber", "Falha ao Conectar");
            }

            @Override
            public void onSubscribeSuccess(Topic topic) {
                Log.d("subscriber", "Subscrito com sucesso");
            }

            @Override
            public void onSubscribeFailure(Throwable cause) {
                Log.d("subscriber", "Falha ao Subscrever");
            }
        });

        sub.connect();
    }


    private void mostrarSensores() {
        List<String> sensors = cddl.getInternalSensorList();

        for (String sen : sensors) {
            Log.d("Sensors", sen);
        }
    }


    //publica em um tópico
    private void startSensores(List<String> sensorList) {

        CommandRequest comandRequest = new CommandRequest(clientId,
                new MOUUID(TechnologyID.INTERNAL.id, "localhost"),
                "start-sensors", sensorList);

        pub = Publisher.of(cddl);
        pub.setCallback(new Callback() {
            @Override
            public void onPublishFailure(Throwable cause) {
                Log.d("PUBLISHER", "Falha ao publicar");
            }

            @Override
            public void onConnectSuccess() {
                pub.publish(comandRequest);
                //pub.publish(queryMessage);
                Log.d("PUBLISHER", "publicado");
            }

            @Override
            public void onConnectFailure(Throwable exception) {
                Log.d("PUBLISHER", "Falha ao conectar");
            }
        });
        pub.connect();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSensor();
    }

    public void stopSensor() {
        if (cddl != null) cddl.stopScan();
        if (pub != null) pub.disconnect();
        if (sub != null) sub.disconnect();
        super.onDestroy();
    }

    private void changeFragment(Fragment fragment) {
        stopSensor();
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", usuario);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }


}
