package com.example.sem_i;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListHistoryAdapter extends RecyclerView.Adapter<ListHistoryAdapter.ViewHolder> {

    private ArrayList<String> organizer;
    private ArrayList<String> title;
    private ArrayList<String> tgl;
    private ArrayList<String> lokasi, idInfTrain, idTrans, url, status;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListHistoryAdapter(Context context, ArrayList<String> organizer, ArrayList<String> title, ArrayList<String> tgl
            , ArrayList<String> lokasi, ArrayList<String> idfTrain, ArrayList<String> url, ArrayList<String> idTrans, ArrayList<String> status){
        this.organizer = organizer;
        this.title = title;
        this.tgl = tgl;
        this.url = url;
        this.status = status;
        this.lokasi = lokasi;
        this.idTrans = idTrans;
        this.idInfTrain = idfTrain;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ListHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_posted, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.imageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        holder.tvtitle.setText(title.get(position));
        holder.tvOrganizer.setText(organizer.get(position));
        holder.tvtanggal.setText(tgl.get(position));
        holder.tvlocation.setText(lokasi.get(position));

        if (!status.get(position).equals("Sudah Dibayar")){
            holder.switchCompat.setChecked(false);
            holder.switchCompat.setText("Belum Dibayar");
        }else{
            holder.switchCompat.setChecked(true);
            holder.switchCompat.setText("Sudah Dibayar");
            holder.switchCompat.setTextColor(R.color.colorAccent);
        }

        if (!url.get(position).equals("")){
            Picasso.get()
                    .load(url.get(position))
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VerificationActivity.class);
                intent.putExtra("id", idInfTrain.get(position));
                intent.putExtra("idTrans", idTrans.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return organizer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrganizer, tvtitle, tvtanggal, tvlocation, noTrans;
        ImageView imageView;
        LinearLayout container;
        SwitchCompat switchCompat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.postcontainer);
            tvOrganizer = itemView.findViewById(R.id.posttv_trainingProvider);
            tvtitle = itemView.findViewById(R.id.posttv_title);
            tvtanggal = itemView.findViewById(R.id.posttv_date);
            tvlocation = itemView.findViewById(R.id.posttv_location);
            imageView = itemView.findViewById(R.id.postimg_train);
            switchCompat = itemView.findViewById(R.id.switchPost);
            noTrans = itemView.findViewById(R.id.noEventHistory);
        }
    }
}
