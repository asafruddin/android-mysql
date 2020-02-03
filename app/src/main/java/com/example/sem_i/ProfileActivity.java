package com.example.sem_i;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    static final String TAG = "ProfileActivity";
    FloatingActionButton fab;
    TextView tv_train_prov, tv_perusahaan, noEventPosted;
    Button btnEdt, btnUpload;
    ArrayList<String> perusahaan, trainProv, imageUrl, id;
    ArrayList<String> organizer, title, tanggal, lokasi, idInfTrain, idTrainProv, url;
    ImageView img;
    StorageReference storageReference;
    private StorageTask storageTask;
    public Uri imageUri;
    String mail;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storageReference = FirebaseStorage.getInstance().getReference("Image");

        fab = findViewById(R.id.btn_float);
        tv_perusahaan = findViewById(R.id.tv_perusahaan);
        tv_train_prov = findViewById(R.id.tv_organizer);
        btnEdt = findViewById(R.id.btn_edtPrifile);
        img = findViewById(R.id.imViewProfile);
        btnUpload = findViewById(R.id.btnUpload);
        noEventPosted = findViewById(R.id.noEventPosted);

        AndroidNetworking.initialize(getApplicationContext());

        recyclerView = findViewById(R.id.postedRecycler);
        recyclerView.setHasFixedSize(true);
        organizer = new ArrayList<String>();
        title = new ArrayList<String>();
        tanggal = new ArrayList<String>();
        lokasi = new ArrayList<String>();
        idInfTrain = new ArrayList<String>();
        idTrainProv = new ArrayList<String>();
        url = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
        mail = sharedPreferences.getString("mail", "");
        getData(mail);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storageTask !=null && storageTask.isInProgress()){
                    Toast.makeText(getApplicationContext(), "Proses Mengunggah", Toast.LENGTH_SHORT).show();
                }else {
                    fileUploader();
                }
            }
        });

        btnEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditPerusahaanActivity.class));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CollectionUtils.isEmpty(perusahaan))
                    startActivity(new Intent(getApplicationContext(), PostActivity.class));
                else{
                    onUnCompleteData();
                }

            }
        });
    }

    public void getDataTrain(String email){
        AndroidNetworking.get(RegisterActivity.ip+"android/getTrainProvProfile.php")
                .addQueryParameter("email", email)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                imageUrl = new ArrayList<>();
                                trainProv = new ArrayList<>();
                                for (int i = 0; i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    trainProv.add(data.getString("trainProv"));
                                    imageUrl.add(data.getString("imageUrl"));
                                }
                                tv_train_prov.setText(trainProv.get(0));
                                if (!imageUrl.get(0).equals("")){
                                    Picasso.get()
                                            .load(imageUrl.get(0))
                                            .fit()
                                            .centerCrop()
                                            .into(img);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!! " + anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getData(final String email){
        AndroidNetworking.get(RegisterActivity.ip+"android/getTrainProv.php")
                .addQueryParameter("email", email)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                perusahaan = new ArrayList<String>();
                                trainProv = new ArrayList<String>();
                                imageUrl = new ArrayList<>();
                                id = new ArrayList<>();
                                for (int i = 0; i< response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    perusahaan.add(data.getString("perusahaan"));
                                    trainProv.add(data.getString("nama_trainprov"));
                                    imageUrl.add(data.getString("imageUrl"));
                                    id.add(data.getString("id_trainprov"));
                                }
                                if (CollectionUtils.isEmpty(perusahaan)){
                                    tv_perusahaan.setText("Perusahaan");
                                }else {
                                    tv_perusahaan.setText(perusahaan.get(0));
                                }

                                if (!imageUrl.get(0).equals("")){
                                    Picasso.get()
                                            .load(imageUrl.get(0))
                                            .fit()
                                            .centerCrop()
                                            .into(img);
                                }
                                if (trainProv != null && trainProv.size() != 0){
                                    tv_train_prov.setText(trainProv.get(0));
                                    getInfTrainProv(id.get(0));
                                }else{
                                    getDataTrain(mail);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError" + anError);
                        Toast.makeText(getApplicationContext(), "Error!!! " + anError, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }

    private void fileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    private void fileUploader(){
        if (imageUri != null){
            final StorageReference Ref = storageReference.child(System.currentTimeMillis()+"."+getExtansion(imageUri));
            storageTask = Ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSuccess: uri= " + uri.toString());
                                    String downloadUrl = uri.toString();
                                    updateImage(downloadUrl, mail);
                                }
                            });
                            //String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            Toast.makeText(getApplicationContext(), "Gambar Terupload", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
        else
            Toast.makeText(getApplicationContext(), "Click pada Gambar", Toast.LENGTH_LONG).show();


    }

    private String getExtansion(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void updateImage(String downloadUrl, String email){
        AndroidNetworking.post(RegisterActivity.ip+"android/updateImage.php")
                .addBodyParameter("url", downloadUrl)
                .addBodyParameter("email", email)
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

    public void getInfTrainProv(final String id){
        AndroidNetworking.get(RegisterActivity.ip+"android/getInfoTrainWhereProv.php")
                .addQueryParameter("id", id)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                for (int i = 0; i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    organizer.add(data.getString("nama_trainprov"));
                                    title.add(data.getString("title"));
                                    tanggal.add(data.getString("tgl"));
                                    lokasi.add(data.getString("lokasi"));
                                    idInfTrain.add(data.getString("id"));
                                    idTrainProv.add(id);
                                    url.add(data.getString("url"));
                                }
                                if (organizer.size()!=0 && organizer !=null){
                                    ListPostAdapter adapter = new ListPostAdapter(ProfileActivity.this, organizer, title, tanggal, lokasi, idInfTrain, url, idTrainProv);
                                    recyclerView.setAdapter(adapter);
                                    noEventPosted.setVisibility(View.GONE);
                                }else
                                    noEventPosted.setVisibility(View.VISIBLE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "Error!!! "+ anError, Toast.LENGTH_SHORT);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void onUnCompleteData(){
        new AlertDialog.Builder(this).setTitle("Data Belum Lengkap")
                .setMessage("Silahkan Lengkapi Data Anda Terlebih Dahulu!!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), EditPerusahaanActivity.class));
                    }
                }).create().show();
    }
}