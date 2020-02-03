package com.example.sem_i;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.ViewHolder>{

    private ArrayList<String> organizer;
    private ArrayList<String> title;
    private ArrayList<String> tgl;
    private ArrayList<String> lokasi, idInfTrain, idTrainProv, url;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListDataAdapter(Context context, ArrayList<String> organizer, ArrayList<String> title
            , ArrayList<String> tgl, ArrayList<String> lokasi, ArrayList<String> idInfTrain, ArrayList<String> idTrainProv
            , ArrayList<String> url){
        this.organizer = organizer;
        this.title = title;
        this.tgl = tgl;
        this.url = url;
        this.lokasi = lokasi;
        this.idTrainProv = idTrainProv;
        this.idInfTrain = idInfTrain;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ListDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.imageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        String mOrganizer = organizer.get(position);
        final String mtitle = title.get(position);
        String mdate = tgl.get(position);
        String mlocation = lokasi.get(position);

        holder.tvtitle.setText(mtitle);
        holder.tvOrganizer.setText(mOrganizer);
        holder.tvtanggal.setText(mdate);
        holder.tvlocation.setText(mlocation);
        if (!url.get(position).equals("")) {
            Picasso.get()
                    .load(url.get(position))
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", idInfTrain.get(position));
                intent.putExtra("idTrainProv", idTrainProv.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return organizer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrganizer, tvtitle, tvtanggal, tvlocation;
        ImageView imageView;
        LinearLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrganizer = itemView.findViewById(R.id.tv_trainingProvider);
            tvtitle = itemView.findViewById(R.id.tv_title);
            tvtanggal = itemView.findViewById(R.id.tv_date);
            tvlocation = itemView.findViewById(R.id.tv_location);
            imageView = itemView.findViewById(R.id.img_train);
            container = itemView.findViewById(R.id.container);
        }
    }
}