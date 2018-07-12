package br.ufma.cliente.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import br.ufma.cliente.domain.model.UsuarioLocalizacao;
import br.ufma.cliente.domain.model.auxiliary.Parado;
import br.ufma.cliente.domain.model.auxiliary.Resultado;
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
import br.ufma.lsdi.cddl.message.SensorData;
import br.ufma.lsdi.cddl.message.ServiceList;
import br.ufma.lsdi.cddl.message.TechnologyID;
import br.ufma.lsdi.cddl.type.CDDLConfig;
import br.ufma.lsdi.cddl.type.CEPRule;
import br.ufma.lsdi.cddl.type.ClientId;
import br.ufma.lsdi.cddl.type.Host;
import br.ufma.lsdi.cddl.type.Topic;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Leeo on 04/04/2017.
 */

public class RotaFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public Context context;

    private GoogleMap mMap;
    private LatLng origem, destino;
    private static final String GOOGLE_KEY_DIRECTIONS = "AIzaSyB299emCUuKMkZHXIHQc5u1Po7ZnrEA3S0";
    Gson gson;

    private final CDDL cddl = CDDL.getInstance();
    private final String clientId = "ivan.rodrigues@lsdi.ufma.br";
    private Subscriber sub, subscriber;
    private Publisher pub, publicador;

    private final String PREFS_PRIVATE = "PREFS_PRIVATE";
    private SharedPreferences sharedPreferences;
    private UsuarioLocalizacao usuarioLocalizacao;

    private Localizacao localizacao;
    private Resultado resultado;


    private String customTopic;

    private CDDLConfig config;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rota, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.rota);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        localizacao = new Localizacao();
        resultado = new Resultado();
        usuarioLocalizacao = (UsuarioLocalizacao) bundle.getSerializable("usuarioLocalizacao");
        changeTitleItemMenu("Finalizar");
        MainActivity.menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    publicarFim();
                    UsuarioLocalizacao mUsuarioLocalizacao = new UsuarioLocalizacao();
                    mUsuarioLocalizacao.setTrajeto(usuarioLocalizacao.getTrajeto());
                    mUsuarioLocalizacao.setStatus(new Status(StatusEnum.FINALIZADO.getValue()));
                    mUsuarioLocalizacao.setLocalizacao(localizacao);
                    mUsuarioLocalizacao.setUsuario(usuarioLocalizacao.getTrajeto().getUsuario());
                    sendUpdateLocationToServer(mUsuarioLocalizacao);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
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
            rota(usuarioLocalizacao, Color.LTGRAY);
        } else {
            Toast.makeText(getActivity(), "Erro ao buscar a rota", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity.toolbar.setTitle("Trajeto");
        //iniciar CDDL
        iniciarCDDL(getActivity());

        //mostrando sensores internos
        mostrarSensores();

        String topic = "ivan.rodrigues@lsdi.ufma.br/Location";
        customTopic = "ivan.rodrigues@lsdi.ufma.br/LocationCustom";
        //publicar dado de contexto

        //iniciar sensores
        List<String> sensorList = Arrays.asList("Location", "K6DS3TR Accelerometer");
        //List<String> sensorList = Arrays.asList("Location", "BMI160 Accelerometer");
        //  sensorList = Arrays.asList("BMI160 Accelerometer"); IVAN's sensor
        if (usuarioLocalizacao.getStatus().getId().equals(StatusEnum.AGUARDANDO_INICIO.getValue())) {

            startSensores(sensorList);
            subscrever();

            subDois(topic);
        } else {
            try {
                if (mMap != null) {
                    mMap.clear();
                }
                getStatusUsuario(usuarioLocalizacao);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //iniciando CDDL
    public void iniciarCDDL(Context context) {

        config = CDDLConfig.builder()
                .host(Host.of("tcp://iot.eclipse.org:1883"))
               // .host(Host.of("tcp://lsdi.ufma.br:1883"))
                .clientId(ClientId.of(clientId))
                .build();

        cddl.init(context, config);
        cddl.startScan();

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

    private void mostrarSensores() {
        List<String> sensors = cddl.getInternalSensorList();

        for (String sen : sensors) {
            Log.d("Sensors", sen);
        }
    }

    //subscreve em um topico
    public void subscrever() {

        String epl = "select avg(sensorValue[0]*sensorValue[0]+sensorValue[1]*sensorValue[1]+sensorValue[2]*sensorValue[2]) as valor1 " +
                "from ContextMessage.win:time_batch(2sec) " +
                "where serviceName = 'K6DS3TR Accelerometer'";
        //  "where serviceName = 'K6DS3TR Accelerometer'";

        sub = Subscriber.of(cddl);
        Monitor monitor = Monitor.of(config);

        MonitorToken token = monitor.addRule(CEPRule.of(epl));

        sub = Subscriber.of(cddl);
        sub.setCallback(new Callback() {
            @Override
            public void messageArrived(MapEvent mapEvent) {

                Object val1 = (Double) mapEvent.getProperties().get("valor1");

                Double valor = (Double) val1;

                UsuarioLocalizacao userLocation = new UsuarioLocalizacao();
                userLocation.setLocalizacao(localizacao);
                userLocation.setTrajeto(usuarioLocalizacao.getTrajeto());
                userLocation.setData(DateUtil.toDate(new Date(), DateUtil.DATA_SEPARADO_POR_TRACO_AMERICANO));
                userLocation.setUsuario(usuarioLocalizacao.getTrajeto().getUsuario());
                userLocation.setMedia(valor);

                if (valor <= Double.valueOf(100.01)) {
                    userLocation.setStatus(new Status(StatusEnum.PARADO.getValue()));
                } else if (valor > Double.valueOf(100.01) && valor <= Double.valueOf(135.99)) {
                    userLocation.setStatus(new Status(StatusEnum.ANDANDO.getValue()));
                } else {
                    userLocation.setStatus(new Status(StatusEnum.CORRENDO.getValue()));
                }

                // usuarioLocalizacaos.add(userLocation);
                publicarCustom(userLocation);
                setMarker(userLocation);

                try {
                    sendUpdateLocationToServer(userLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectSuccess() {
                sub.subscribe(token);
                Log.d("Sucess", "Conectado com sucesso");

            }

            @Override
            public void onConnectFailure(Throwable exception) {
                Log.d("Falha", "Erro ao conectar");
            }

            @Override
            public void onSubscribeSuccess(Topic topic) {
                Log.d("Subss", "Subescrito com sucesso");
            }

            @Override
            public void onSubscribeFailure(Throwable cause) {
                Log.d("Falha", "Erro ao subescrever");
            }
        });

        sub.connect();
    }

    public void setMarker(UsuarioLocalizacao user) {
        LatLng now = new LatLng(user.getLocalizacao().getLatitude(), user.getLocalizacao().getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(now)
                .title("Movimentando" + user.getStatus().getId()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
    }

    public void setCustomMarker(Parado parado) {
        LatLng now = new LatLng(parado.getLatitude(), parado.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(now)
                .title("Tempo Parado: " + parado.getSegundo() + " segundos"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
    }

    private void publicarCustom(UsuarioLocalizacao usuarioLocalizacao) {

        try {
            publicador = Publisher.of(cddl);
            gson = new Gson();

            ContextMessage contextMessage =
                    new ContextMessage("String", "UsuarioLocalizacao" + usuarioLocalizacao.getTrajeto().getId(),
                            gson.toJson(usuarioLocalizacao));

            publicador.setCallback(new Callback() {
                @Override
                public void onConnectSuccess() {
                    publicador.publish(contextMessage);
                    Log.d("Publicador", "Publicado com Sucesso!");
                }

                @Override
                public void onConnectFailure(Throwable exception) {
                    Log.d("Publicador", exception.getMessage());
                }

                @Override
                public void onPublishFailure(Throwable cause) {
                    Log.d("Publicador", cause.getMessage());
                }
            });

            publicador.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void changeTitleItemMenu(String title) {
        MainActivity.menuItem.setTitle(title);
    }

    public void stopSensor() {
        if(cddl != null){
            cddl.stopScan();
        }
        if(sub != null){
            sub.disconnect();
        }
        if(pub != null){
            pub.disconnect();
        }
    }

    public void sendUpdateLocationToServer(UsuarioLocalizacao userLocation) throws Exception {

        gson = new Gson();
        Log.d("obj", gson.toJson(userLocation));
        try {
            Call<UsuarioLocalizacao> call = new RetrofitInicializador().salvarTrajeto().salvarTrajeto(userLocation);
            call.enqueue(new retrofit2.Callback<UsuarioLocalizacao>() {
                @Override
                public void onResponse(Call<UsuarioLocalizacao> call, Response<UsuarioLocalizacao> response) {
                    if (response.body() != null) {
                        usuarioLocalizacao = response.body();
                        if (usuarioLocalizacao.getStatus().getId().equals(StatusEnum.FINALIZADO.getValue())) {
                            Toast.makeText(getActivity().getApplicationContext(), "Finalizado com sucesso.", Toast.LENGTH_SHORT).show();
                            try {
                                getStatusUsuario(usuarioLocalizacao);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    private void subDois(String topic) {

        subscriber = Subscriber.of(cddl);
        subscriber.setCallback(new Callback() {
            @Override
            public void messageArrived(ContextMessage contextMessage) {
                Gson gson = new Gson();
                Log.d("subscriber", gson.toJson(contextMessage));
                SensorData sensorData = gson.fromJson(contextMessage.getBody(), SensorData.class);
                if (sensorData.getSensorName().equals("Location")) {
                    localizacao = new Localizacao();
                    localizacao.setLatitude(sensorData.getSensorValue()[0]);
                    localizacao.setLongitude(sensorData.getSensorValue()[1]);
                    localizacao.setData(DateUtil.toDate(new Date(), DateUtil.DATA_SEPARADO_POR_TRACO_AMERICANO));

                    MainActivity.menuItem.setVisible(true);
                    //mMap.setMyLocationEnabled(true);
                }
            }

            @Override
            public void onConnectSuccess() {
                subscriber.subscribe(Topic.of(topic));
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

        subscriber.connect();

    }

    private void rota(UsuarioLocalizacao usuarioLocalizacao, int color) {
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
                            Route route = direction.getRouteList().get(0);
                            int legCount = route.getLegList().size();
                            for (int index = 0; index < legCount; index++) {
                                Leg leg = route.getLegList().get(index);
                                //mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()).title(leg.getStartAddress()));
                                if (index == legCount - 1) {
                                   // mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title(leg.getEndAddress()));
                                }
                                List<Step> stepList = leg.getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(),
                                        stepList, 5, color, 3, Color.BLUE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
     //   stopSensor();
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopSensor();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void getStatusUsuario(UsuarioLocalizacao usuarioLocalizacao) throws Exception {
        try {
            Call<Resultado> call = new RetrofitInicializador().getStatusUsuario().getStatusUsuario(new Resultado(usuarioLocalizacao.getTrajeto().getId()));
            call.enqueue(new retrofit2.Callback<Resultado>() {
                @Override
                public void onResponse(Call<Resultado> call, Response<Resultado> response) {

                    if (response.body() != null) {
                        resultado = response.body();

                        if (resultado.getAndando() != null) {
                            //TRAJETO PERCORRIDO ANDANDO
                            Trajeto trajetoAndando = new Trajeto();
                            UsuarioLocalizacao userAndando = new UsuarioLocalizacao();
                            trajetoAndando.setLatitudeInicial(resultado.getAndando().getLatideInicial());
                            trajetoAndando.setLongitudeInicial(resultado.getAndando().getLongitudeInicial());
                            trajetoAndando.setLatitudeFinal(resultado.getAndando().getLatitudeFinal());
                            trajetoAndando.setLongitudeFinal(resultado.getAndando().getLongitudeFinal());
                            userAndando.setTrajeto(trajetoAndando);
                            rota(userAndando, Color.BLUE);
                        }

                        if (resultado.getCorrendo() != null) {

                            //TRAJETO PERCORRIDO CORRENDO
                            Trajeto trajetoCorrendo = new Trajeto();
                            UsuarioLocalizacao userCorrendo = new UsuarioLocalizacao();
                            trajetoCorrendo.setLatitudeInicial(resultado.getCorrendo().getLatideInicial());
                            trajetoCorrendo.setLongitudeInicial(resultado.getCorrendo().getLongitudeInicial());
                            trajetoCorrendo.setLatitudeFinal(resultado.getCorrendo().getLatitudeFinal());
                            trajetoCorrendo.setLongitudeFinal(resultado.getCorrendo().getLongitudeFinal());
                            userCorrendo.setTrajeto(trajetoCorrendo);
                            rota(userCorrendo, Color.GREEN);
                        }

                        if (resultado.getParados() != null && !resultado.getParados().isEmpty()) {
                            //PONTOS NO QUAL O USUÁRIO FICOU PARADO
                            for (Parado parado : resultado.getParados()) {
                                setCustomMarker(parado);

                            }
                        }


                    }

                }

                @Override
                public void onFailure(Call<Resultado> call, Throwable t) {
                    t.printStackTrace();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void publicarFim() {
        mMap.clear();
        stopSensor();
    }
}