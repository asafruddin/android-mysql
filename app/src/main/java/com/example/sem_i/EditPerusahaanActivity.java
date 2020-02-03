package com.example.sem_i;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.util.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class EditPerusahaanActivity extends AppCompatActivity {
    private static final String TAG = "EditPerusahaanActivity";
    EditText namaDir, noTelpDir, noTelpKan, npwp;
    AutoCompleteTextView namaPerus;
    ImageButton btnNext;
    ArrayList<String> Id = new ArrayList<String>();
    ArrayList<String> Name = new ArrayList<String>();
    ArrayList<String> id;
    ArrayList<String> name;
    ArrayList<String> direktur;
    ArrayList<String> tlp_dir;
    ArrayList<String> tlp_kan;
    ArrayList<String> mNpwp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perusahaan);

        Log.d(TAG, "onCreat: inisialisasi");

        namaDir = findViewById(R.id.txtNamaDirektur);
        noTelpDir = findViewById(R.id.txtNoTelpDir);
        noTelpKan = findViewById(R.id.txtNoTelpKantor);
        npwp = findViewById(R.id.txtNpwp);
        btnNext = findViewById(R.id.btnNext);

        AndroidNetworking.initialize(getApplicationContext());
        getDataPerusahaan();

        namaPerus = findViewById(R.id.txtNamaPerusahaan);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Name);
        namaPerus.setThreshold(1);
        namaPerus.setAdapter(adapter);

        namaPerus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String perusahaan = namaPerus.getText().toString();
                getCompany(perusahaan);

            }
        });
        namaPerus.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            @Override
            public void onDismiss() {
                namaDir.setText("");
                noTelpDir.setText("");
                noTelpKan.setText("");
                npwp.setText("");
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nPerusahaan = namaPerus.getText().toString();
                String nDirektur = namaDir.getText().toString();
                String nTelpDir = noTelpDir.getText().toString();
                String nTelpKan = noTelpKan.getText().toString();
                String mNpwp = npwp.getText().toString();

                if (nPerusahaan.isEmpty())
                    namaPerus.setError("Harus Diisi");
                else if (nDirektur.isEmpty())
                    namaDir.setError("Harus Diisi");
                else if (nTelpDir.isEmpty())
                    noTelpDir.setError("Harus Diisi");
                else if (nTelpKan.isEmpty())
                    noTelpKan.setError("Harus Diisi");
                else if (mNpwp.isEmpty())
                    npwp.setError("Harus Diisi");
                else {
                    if (CollectionUtils.isEmpty(direktur))
                        addData(nPerusahaan, nDirektur, nTelpDir, nTelpKan, mNpwp);
                    else
                        update(nPerusahaan, nDirektur, nTelpDir, nTelpKan, mNpwp);
                }
                Intent i = new Intent(getApplicationContext(), EditTrainProvActivity.class);
                i.putExtra("namaPerusahaan", nPerusahaan);
                startActivity(i);

            }
        });
    }

    public void addData(String perusahaan, String direktur, String telpDir, String telpKan, String npwp){
        AndroidNetworking.post(RegisterActivity.ip+"android/insertperusahaan.php")
                .addBodyParameter("nama_perusahaan", perusahaan)
                .addBodyParameter("nama_direktur", direktur)
                .addBodyParameter("no_telp_dir", telpDir)
                .addBodyParameter("no_telp_kan", telpKan)
                .addBodyParameter("npwp", npwp)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: "+ response);
                        Toast.makeText(getApplicationContext(), "Data Successfully Stored", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Failed"+ anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getDataPerusahaan(){
        AndroidNetworking.get(RegisterActivity.ip+"android/getPerusahaan.php")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);{
                            try {
                                for (int i = 0; i<response.length(); i++){
                                    JSONObject data = response.getJSONObject(i);
                                    Id.add(data.getString("idPerusahaan"));
                                    Name.add(data.getString("namaPerusahaan"));
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

    public void getCompany(String nama){
        AndroidNetworking.get(RegisterActivity.ip+"android/getCompany.php")
                .addQueryParameter("nama", nama)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);{
                            try {
                                id = new ArrayList<String>();
                                name = new ArrayList<String>();
                                direktur = new ArrayList<String>();
                                tlp_dir = new ArrayList<String>();
                                tlp_kan = new ArrayList<String>();
                                mNpwp1 = new ArrayList<String>();
                                for (int i = 0; i<response.length(); i++){
                                    JSONObject data = response.getJSONObject(i);
                                    id.add(data.getString("idPerusahaan"));
                                    name.add(data.getString("namaPerusahaan"));
                                    direktur.add(data.getString("direktur"));
                                    tlp_dir.add(data.getString("tlp_dir"));
                                    tlp_kan.add(data.getString("tlp_kan"));
                                    mNpwp1.add(data.getString("npwp"));
                                }

                                namaDir.setText(direktur.get(0));
                                noTelpDir.setText(tlp_dir.get(0));
                                noTelpKan.setText(tlp_kan.get(0));
                                npwp.setText(mNpwp1.get(0));
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

    public void update(String perusahaan, String direktur, String telp_dir, String telp_kan,
                       String npwp){
        AndroidNetworking.post(RegisterActivity.ip+"android/updatePerusahaan.php")
                .addBodyParameter("perusahaan", perusahaan)
                .addBodyParameter("direktur", direktur)
                .addBodyParameter("telp_dir", telp_dir)
                .addBodyParameter("telp_kan", telp_kan)
                .addBodyParameter("npwp", npwp)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse" + response);
                        Toast.makeText(getApplicationContext(), "Data Successfully Stored", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+ anError, Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
