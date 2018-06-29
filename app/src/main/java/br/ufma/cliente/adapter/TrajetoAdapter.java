package br.ufma.cliente.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import br.ufma.cliente.R;
import br.ufma.cliente.domain.enuns.StatusEnum;
import br.ufma.cliente.domain.model.Trajeto;
import br.ufma.cliente.domain.model.UsuarioLocalizacao;
import br.ufma.cliente.service.OnUpdateListener;
import br.ufma.cliente.util.DateUtil;


/**
 * Created by Leeo on 25/10/2016.
 */

public class TrajetoAdapter extends RecyclerView.Adapter<TrajetoAdapter.TrajetoViewHolder> {

    private LayoutInflater mLayoutInflater;
    final Dialog dialog;
    private final Context context;
    private static final String TAG = "TrajetoAdapter";
    public List<UsuarioLocalizacao> usuarioLocalizacaos;
    private OnUpdateListener onUpdateListener;

    private Trajeto trajeto = new Trajeto();


    public TrajetoAdapter(Context context, List<UsuarioLocalizacao> usuarioLocalizacaos, OnUpdateListener onUpdateListener) {
        this.usuarioLocalizacaos = usuarioLocalizacaos;
        this.context = context;
        dialog = new Dialog(context);
        this.onUpdateListener = onUpdateListener;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public TrajetoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.adapter_trajeto, parent, false);
        TrajetoViewHolder mvh = new TrajetoViewHolder(v);
        trajeto = new Trajeto();
        return mvh;
    }

    @Override
    public void onBindViewHolder(final TrajetoViewHolder holder, final int position) {

        holder.titulo.setText(usuarioLocalizacaos.get(position).getTrajeto().getDescricaoOrigem());
        holder.situacao.setText(usuarioLocalizacaos.get(position).getTrajeto().getDescricaoDestino());

        if (usuarioLocalizacaos.get(position).getStatus().getId().equals(StatusEnum.AGUARDANDO_INICIO.getValue())) {
            holder.toggleButton.setBackgroundResource(R.mipmap.map_green);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuarioLocalizacaos.get(position).getStatus().getId().equals(StatusEnum.AGUARDANDO_INICIO.getValue())){
                    onUpdateListener.onUpdate(usuarioLocalizacaos.get(position));
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return usuarioLocalizacaos.size();
    }

    public static class TrajetoViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo;
        private TextView situacao;
        private RelativeLayout relativeLayout;
        private ToggleButton toggleButton;

        public TrajetoViewHolder(final View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.cardtitle);
            situacao = (TextView) itemView.findViewById(R.id.tv_situacao);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.content_trajeto);
            toggleButton = (ToggleButton) itemView.findViewById(R.id.tb_map);
        }
    }

}
