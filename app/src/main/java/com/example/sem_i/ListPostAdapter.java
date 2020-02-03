package com.example.sem_i;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListPostAdapter extends RecyclerView.Adapter<ListPostAdapter.ViewHolder>{

    private ArrayList<String> organizer;
    private ArrayList<String> title;
    private ArrayList<String> tgl;
    private ArrayList<String> lokasi, idInfTrain, idTrainProv, url;
    private LayoutInflater layoutInflater;
    private Context context;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private String date;

    public ListPostAdapter(Context context, ArrayList<String> organizer, ArrayList<String> title, ArrayList<String> tgl
    , ArrayList<String> lokasi, ArrayList<String> idfTrain, ArrayList<String> url, ArrayList<String> idTrainProv){
        this.organizer = organizer;
        this.title = title;
        this.tgl = tgl;
        this.url = url;
        this.lokasi = lokasi;
        this.idTrainProv = idTrainProv;
        this.idInfTrain = idfTrain;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ListPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_posted, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        calendar = Calendar.getInstance();

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = simpleDateFormat.format(calendar.getTime());

        holder.tvtitle.setText(title.get(position));
        holder.tvOrganizer.setText(organizer.get(position));
        holder.tvtanggal.setText(tgl.get(position));
        holder.tvlocation.setText(lokasi.get(position));

        if (tgl.get(position).compareTo(date)<0){
            holder.switchCompat.setChecked(false);
            holder.switchCompat.setText("Sudah Terlaksana");
        }else{
            holder.switchCompat.setChecked(true);
            holder.switchCompat.setText("Belum Terlaksana");
            holder.switchCompat.setTextColor(R.color.colorAccent);
        }

        if (!url.get(position).equals("")){
            Picasso.get()
                    .load(url.get(position))
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return organizer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvOrganizer, tvtitle, tvtanggal, tvlocation, noEvent;
        ImageView imageView;
        LinearLayout container;
        SwitchCompat switchCompat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrganizer = itemView.findViewById(R.id.posttv_trainingProvider);
            tvtitle = itemView.findViewById(R.id.posttv_title);
            tvtanggal = itemView.findViewById(R.id.posttv_date);
            tvlocation = itemView.findViewById(R.id.posttv_location);
            imageView = itemView.findViewById(R.id.postimg_train);
            container = itemView.findViewById(R.id.postcontainer);
            switchCompat = itemView.findViewById(R.id.switchPost);
            noEvent = itemView.findViewById(R.id.noEventPosted);
        }
    }
}
