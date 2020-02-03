package com.example.sem_i;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditTrainProvActivity extends AppCompatActivity {
    private static final String TAG = "EditTrainProvActivity";
    String jabatan, mail;
    EditText naPerusahaan, naTrainProv, mBidang, mPJ, mAlamat, mWebsite, mEmail, noTelpTrain;
    Spinner spinnerJabatan;
    ImageButton btnSimpan;
    ArrayList<String> jabat  = new ArrayList<String>();
    ArrayList<String> namaTrainProv, bidang, nama_pj, alamat, website, tlp_train, perusahaan, mjabat;
    LoginActivity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_train_prov);

        naPerusahaan = findViewById(R.id.txtNamaPerusahaan);
        naTrainProv = findViewById(R.id.txtNamaOrganizer);
        mBidang = findViewById(R.id.txtBidang);
        mPJ = findViewById(R.id.txtNamaPj);
        mAlamat =findViewById(R.id.txtAlamat);
        mWebsite = findViewById(R.id.txtWebsite);
        mEmail = findViewById(R.id.txtEmail);
        noTelpTrain = findViewById(R.id.txtNoTelpProv);
        spinnerJabatan = findViewById(R.id.jabatan);
        btnSimpan = findViewById(R.id.btnSubmit);

        AndroidNetworking.initialize(getApplicationContext());

        getJabatan();

        naPerusahaan.setText(getIntent().getStringExtra("namaPerusahaan"));
        loginActivity = new LoginActivity();
        SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
        mail = sharedPreferences.getString("mail", "");
        mEmail.setText(mail);
        getDataTrainProv(mail);

        spinnerJabatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jabatan = spinnerJabatan.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String perusahaan = naPerusahaan.getText().toString().trim();
                String trainProv = naTrainProv.getText().toString().trim();
                String bidang = mBidang.getText().toString().trim();
                String namaPj = mPJ.getText().toString().trim();
                String alamat = mAlamat.getText().toString().trim();
                String website = mWebsite.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String tlp_train = noTelpTrain.getText().toString().trim();

                updateTrainProv(perusahaan, jabatan, bidang, trainProv, namaPj, alamat, website, email, tlp_train);
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

            }
        });

    }

    public void getDataTrainP(final String email){
        AndroidNetworking.get(RegisterActivity.ip+"android/getTrainProvTrain.php")
                .addQueryParameter("email", email)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                namaTrainProv = new ArrayList<String>();

                                for (int i = 0; i<response.length(); i++){
                                    JSONObject data = response.getJSONObject(i);
                                    namaTrainProv.add(data.getString("nama_trainprov"));
                                }
                                naTrainProv.setText(namaTrainProv.get(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getDataTrainProv(final String email){
        AndroidNetworking.get(RegisterActivity.ip+"android/getTrainProv.php")
                .addQueryParameter("email", email)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                namaTrainProv = new ArrayList<String>();
                                bidang = new ArrayList<String>();
                                nama_pj = new ArrayList<String>();
                                alamat = new ArrayList<String>();
                                website = new ArrayList<String>();
                                tlp_train = new ArrayList<String>();
                                perusahaan = new ArrayList<String>();
                                mjabat = new ArrayList<String>();

                                for (int i = 0; i<response.length(); i++){
                                    JSONObject data = response.getJSONObject(i);
                                    namaTrainProv.add(data.getString("nama_trainprov"));
                                    bidang.add(data.getString("bidang"));
                                    nama_pj.add(data.getString("nama_pj"));
                                    alamat.add(data.getString("alamat"));
                                    website.add(data.getString("website"));
                                    tlp_train.add(data.getString("no_tlp_train"));
                                    perusahaan.add(data.getString("perusahaan"));
                                    mjabat.add(data.getString("jabatan"));
                                }
                                if (namaTrainProv !=null && namaTrainProv.size() == 0){
                                    getDataTrainP(mail);
                                }else{
                                    naTrainProv.setText(namaTrainProv.get(0));
                                    mBidang.setText(bidang.get(0));
                                    mPJ.setText(nama_pj.get(0));
                                    mAlamat.setText(alamat.get(0));
                                    mWebsite.setText(website.get(0));
                                    noTelpTrain.setText(tlp_train.get(0));
                                    selectSpinner(spinnerJabatan, mjabat.get(0));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getJabatan(){
        AndroidNetworking.get(RegisterActivity.ip+"android/getjabatan.php")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                for (int i=0;i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    jabat.add(data.getString("jabatan"));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditTrainProvActivity.this,
                                        android.R.layout.simple_list_item_1, jabat);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerJabatan.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateTrainProv(String perusahaan, String jabatan, String bidang, String trainprov, String namapj,
                                String alamat, String website, String email, String tlpTrain){
        AndroidNetworking.post(RegisterActivity.ip+"android/updateTrainProv.php")
                .addBodyParameter("perusahaan", perusahaan)
                .addBodyParameter("jabatan", jabatan)
                .addBodyParameter("bidang", bidang)
                .addBodyParameter("trainprov", trainprov)
                .addBodyParameter("nama_pj", namapj)
                .addBodyParameter("alamat", alamat)
                .addBodyParameter("website", website)
                .addBodyParameter("email", email)
                .addBodyParameter("tlp_train", tlpTrain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse" + response);
                        Toast.makeText(getApplicationContext(), "Data Stored ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void selectSpinner(Spinner spnr, String value){
        for (int i = 0; i < spnr.getCount(); i++){
            if (spnr.getItemAtPosition(i).toString().equals(value)){
                spnr.setSelection(i);
                return;
            }
        }
    }
}

