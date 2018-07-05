package br.ufma.cliente.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.ufma.cliente.R;
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
import br.ufma.lsdi.cddl.message.TechnologyID;
import br.ufma.lsdi.cddl.type.CDDLConfig;
import br.ufma.lsdi.cddl.type.CEPRule;
import br.ufma.lsdi.cddl.type.ClientId;
import br.ufma.lsdi.cddl.type.Host;
import br.ufma.lsdi.cddl.type.Topic;

public class TesteActivity extends AppCompatActivity {

    private ListView list;
    private List<String> resultado;
    private final CDDL cddl = CDDL.getInstance();
    private final String clientId = "ivan.rodrigues@lsdi.ufma.br";
    private Subscriber sub;
    private Publisher pub;
    CDDLConfig config;
    ArrayAdapter<String> adapter;

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);
        resultado = new ArrayList<String>();

        list = findViewById(R.id.listview);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resultado);

        list.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        iniciarCDDL(this);
        String topic = "ivan.rodrigues@lsdi.ufma.br/Location";
        //publicar dado de contexto

        //iniciar sensores
        // List<String> sensorList = Arrays.asList("Location", "BMI160 Accelerometer");
        List<String> sensorList = Arrays.asList("Location", "3-axis Accelerometer");
        //  sensorList = Arrays.asList("K2HH Acceleration");
        startSensores(sensorList);
        mostrarSensores();

        subscrever();
    }


    private void mostrarSensores() {
        List<String> sensors = cddl.getInternalSensorList();

        for (String sen : sensors) {

            Log.d("Sensors", sen);

        }

    }

    //iniciando CDDL
    public void iniciarCDDL(Context context) {

        config = CDDLConfig.builder()
                //.host(Host.of("tcp://lsdi.ufma.br:1883"))
                //.host(Host.of("tcp://192.168.100.4:1883"))
                .host(Host.of("tcp://iot.eclipse.org:1883"))
                .clientId(ClientId.of(clientId))
                .build();

        cddl.init(context, config);
        cddl.startScan();

    }

    //subscreve em um topico
    public void subscrever() {
        String sql = "select * from ContextMessage where serviceName = '3-axis Accelerometer'";
        String epl = "select avg(sensorValue[0]*sensorValue[0]+sensorValue[1]*sensorValue[1]+sensorValue[2]*sensorValue[2]) as valor1 " +
                "from ContextMessage.win:time_batch(2sec) " +
                //"where serviceName = 'BMI160 Accelerometer'";
                "where serviceName = '3-axis Accelerometer'";
        sub = Subscriber.of(cddl);
        Monitor monitor = Monitor.of(config);

        MonitorToken token = monitor.addRule(CEPRule.of(epl));

        sub = Subscriber.of(cddl);
        sub.setCallback(new Callback() {
            @Override
            public void messageArrived(MapEvent mapEvent) {

                Object val1 = (Double) mapEvent.getProperties().get("valor1");

                resultado.add(new Date() + " - " + val1.toString());
                Collections.reverse(resultado);
                //else Collections.reverse(galaxiesList);
                adapter.notifyDataSetChanged();


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

}
