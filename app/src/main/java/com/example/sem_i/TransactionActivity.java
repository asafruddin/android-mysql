package com.example.sem_i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {
    private static final String TAG = "TransactionActivity";
    Spinner spinner;
    String idInf, bank, messeges, organizerinf;
    Button btnGabung;
    ArrayList<String> id, tile, organizer, tglMulai, tglSelesai, jamMulai, jamSelesai, tglTr, jumPeserta, harga, url;
    TextView code, sTitle, sOrganizer, sTglMulai, sTglSelesai, sJamMulai, sJamSelesa, sTglTr, sJumPeserta, sHarga;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        spinner = findViewById(R.id.spinnerBank);
        btnGabung = findViewById(R.id.btnJoinFinal);
        code = findViewById(R.id.SummaryCode);
        sTitle = findViewById(R.id.summaryTitle);
        sOrganizer = findViewById(R.id.summaryOrganizer);
        sTglMulai = findViewById(R.id.summaryDateStart);
        sTglSelesai = findViewById(R.id.summaryDateEnd);
        sJamMulai = findViewById(R.id.summaryTimeStart);
        sJamSelesa = findViewById(R.id.summaryTimeEnd);
        sTglTr = findViewById(R.id.SummaryDateNow);
        sJumPeserta = findViewById(R.id.jumPeserta);
        sHarga = findViewById(R.id.tvPrice);
        imageView = findViewById(R.id.summaryImage);


        idInf = getIntent().getStringExtra("idInf");

        AndroidNetworking.initialize(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
        final String mail = sharedPreferences.getString("mail", "");

        getPublication(idInf);

        messeges = "Dear "+organizer+"\n" +
                "Permintaan Gabung Pada Acara "+sTitle.getText().toString()+" dari "+organizerinf+" pada tanggal mulai "+sTglMulai.getText().toString()+" telah terkonfirmasi,\n" +
                "Langkah selanjutnya silahkan melakukan pembayaran untuk gabung dengan nominal pembayaran sebesar "+sHarga.getText().toString()+"\n" +
                "dengan jumlah peserta sebanya "+sJumPeserta.getText().toString()+", Pada nomor rekening admin noRek Atas Nama namaAdmin.\n" +
                "\n" +
                "Terima Kasih\n" +
                "Admin";

        sOrganizer.setText(organizerinf);

        getIdTransaksi();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_bank, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0)
                    bank = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnGabung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(bank)){
                    onUnselectedSpinner();
                }else{
                    String idTr = code.getText().toString();
                    updateTransaksi(idTr, bank);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    sendMail(mail, messeges);
                }

            }
        });
    }

    public void updateTransaksi(String id, String bank){
        AndroidNetworking.post(RegisterActivity.ip+"android/updateTransaksi.php")
                .addBodyParameter("id", id)
                .addBodyParameter("bank", bank)
                .addBodyParameter("status", "Menunggu Pembayaran")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse" + response);
                        Toast.makeText(getApplicationContext(), "Transaksi Sukses" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error" + anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getIdTransaksi(){
        AndroidNetworking.get(RegisterActivity.ip+"android/getTransaksi.php")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                id = new ArrayList<String>();
                                tile = new ArrayList<String>();
                                organizer = new ArrayList<String>();
                                tglMulai = new ArrayList<String>();
                                tglSelesai = new ArrayList<String>();
                                jamMulai = new ArrayList<String>();
                                jamSelesai = new ArrayList<String>();
                                tglTr = new ArrayList<String>();
                                jumPeserta = new ArrayList<String>();
                                harga = new ArrayList<String>();
                                url = new ArrayList<>();
                                for (int i = 0;i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    id.add(data.getString("idTr"));
                                    tile.add(data.getString("title"));
                                    organizer.add(data.getString("organizer"));
                                    tglMulai.add(data.getString("tglMulai"));
                                    tglSelesai.add(data.getString("tglSelesai"));
                                    jamMulai.add(data.getString("jamMulai"));
                                    jamSelesai.add(data.getString("jamSelesai"));
                                    tglTr.add(data.getString("tglTr"));
                                    jumPeserta.add(data.getString("jumPeserta"));
                                    harga.add(data.getString("harga"));
                                    url.add(data.getString("url"));
                                }
                                code.setText(id.get(0));
                                sTitle.setText(tile.get(0));
                                sTglMulai.setText(tglMulai.get(0));
                                sTglSelesai.setText(tglSelesai.get(0));
                                sJamMulai.setText(jamMulai.get(0));
                                sJamSelesa.setText(jamSelesai.get(0));
                                sTglTr.setText(tglTr.get(0));
                                sJumPeserta.setText(jumPeserta.get(0));
                                sHarga.setText(harga.get(0));
                                if (!url.get(0).equals("")){
                                    Picasso.get()
                                            .load(url.get(0))
                                            .fit()
                                            .centerCrop()
                                            .into(imageView);
                                }

                                updateOnCheckout(id.get(0));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!!"+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getPublication(String idInf){
        AndroidNetworking.get(RegisterActivity.ip+"android/getOrganizerInf.php")
                .addQueryParameter("idInf", idInf)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                for (int i=0; i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    organizerinf = data.getString("organizer");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                    }
                });
    }

    public void onUnselectedSpinner(){
        new AlertDialog.Builder(this).setTitle("Harus Pilih Bank")
                .setMessage("Harus pilih bank untuk pembayaran!!")
                .setPositiveButton(android.R.string.ok, null).create().show();
    }

    private void sendMail(String email, String body) {

        //Send Mail
        GMailSender sender = new GMailSender("asafruddin10@gmail.com","Rudi6288");
        try {
            sender.sendMail("", body, "asafruddin10@gmail.com", email, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateOnCheckout(String id){
        AndroidNetworking.post(RegisterActivity.ip+"/android/updateOnCheckout.php")
                .addBodyParameter("id", id)
                .addBodyParameter("status", "YA")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse" + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), DetailActivity.class));
    }
}
