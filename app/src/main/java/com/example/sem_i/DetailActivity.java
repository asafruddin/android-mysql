package com.example.sem_i;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG="DetailActivity";
    TextView tvLocation, tvTgl, tvEmail, tvJam, tvTitle, tvDesk;
    ArrayList<String> title, deskripsi, tglMulai, jamMulai, lokasi, tlpProv, url;
    Button btnJoin;
    private String id, idTrainProv;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    String date;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvLocation = findViewById(R.id.tvLocation);
        tvTgl = findViewById(R.id.tvDate);
        tvEmail = findViewById(R.id.tvTelp);
        tvJam = findViewById(R.id.tvTime);
        tvTitle = findViewById(R.id.tvTitleDetail);
        tvDesk = findViewById(R.id.tvDeskripsiDetail);
        btnJoin = findViewById(R.id.btnJoin);
        imageView = findViewById(R.id.app_bar_image);

        calendar = Calendar.getInstance();

        id = getIntent().getStringExtra("id");
        SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
        idTrainProv = sharedPreferences.getString("mail", "");
        AndroidNetworking.initialize(getApplicationContext());

        getDataDetail(id);
        getDataTrainProv(idTrainProv);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(calendar.getTime());
                String status = "Transaksi CheckOut";

                addDataTransaksi(idTrainProv, id, date, status);

                Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                intent.putExtra("idInfTrain", id);
                startActivity(intent);
            }
        });
    }

    public void getDataDetail(String id){
        AndroidNetworking.get(RegisterActivity.ip+"android/getInfoTrainWhere.php")
                .addQueryParameter("id", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                title = new ArrayList<>();
                                deskripsi = new ArrayList<>();
                                tglMulai = new ArrayList<>();
                                jamMulai = new ArrayList<>();
                                lokasi = new ArrayList<>();
                                tlpProv = new ArrayList<>();
                                url = new ArrayList<>();
                                for (int i = 0; i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    title.add(data.getString("title"));
                                    deskripsi.add(data.getString("deskripsi"));
                                    tglMulai.add(data.getString("tgl_mulai"));
                                    jamMulai.add(data.getString("jam_mulai"));
                                    tlpProv.add(data.getString("tlp_prov"));
                                    lokasi.add(data.getString("lokasi"));
                                    url.add(data.getString("url"));
                                }
                                tvLocation.setText(lokasi.get(0));
                                tvTgl.setText(tglMulai.get(0));
                                tvEmail.setText(tlpProv.get(0));
                                tvJam.setText(jamMulai.get(0));
                                tvTitle.setText(title.get(0));
                                tvDesk.setText(deskripsi.get(0));
                                if (!url.get(0).equals("")){
                                    Picasso.get()
                                            .load(url.get(0))
                                            .fit()
                                            .centerCrop()
                                            .into(imageView);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError"+ anError);
                        Toast.makeText(getApplicationContext(), "Error!!!"+ anError, Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void addDataTransaksi(String organizer, String idInf, String tgl, String status){
        AndroidNetworking.post(RegisterActivity.ip+"android/insertTransaksi.php")
                .addBodyParameter("organizer", organizer)
                .addBodyParameter("idInf", idInf)
                .addBodyParameter("tglTransaksi", tgl)
                .addBodyParameter("status", status)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse" + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError"+anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getDataTrainProv(String id){
        AndroidNetworking.get(RegisterActivity.ip+"android/getTrainProv.php")
                .addQueryParameter("email", id)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                for (int i = 0; i<response.length(); i++){
                                    JSONObject data = response.getJSONObject(i);
                                    if (data.getString("nama_pj").equals("")){
                                        onNotFullRegistered();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });
    }


    public void onNotFullRegistered(){
        new AlertDialog.Builder(this).setTitle("Belum Melengkapi Data !!!")
                .setMessage("Silahkan lengkapi data anda terlebih dahulu!!")
                .setPositiveButton(android.R.string.ok, null).create().show();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
