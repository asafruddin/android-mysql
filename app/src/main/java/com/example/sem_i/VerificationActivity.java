package com.example.sem_i;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity {
    String idTrans;
    ImageButton btnUpload;
    LinearLayout btnChoose;
    ImageView imageView;
    StorageReference storageReference;
    private StorageTask storageTask;
    public Uri imageUri;
    private final static String TAG = "VerificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        storageReference = FirebaseStorage.getInstance().getReference("Verification");
        AndroidNetworking.initialize(getApplicationContext());

        idTrans = getIntent().getStringExtra("idTrans");
        btnChoose = findViewById(R.id.btnChoseVer);
        btnUpload = findViewById(R.id.btnUploadVer);
        imageView = findViewById(R.id.imageViewVer);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storageTask !=null && storageTask.isInProgress()){
                    Toast.makeText(getApplicationContext(), "Proses Mengunggah", Toast.LENGTH_LONG).show();
                }else {
                    imageUploader();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void imageUploader(){
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
                                    addImageVerification(downloadUrl, idTrans);
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
            Toast.makeText(getApplicationContext(), "Pilih Gambar Terlebih Dahulu", Toast.LENGTH_LONG).show();
    }

    private String getExtansion(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void addImageVerification(String url, String id){
        AndroidNetworking.post(RegisterActivity.ip+"android/updateImageTrans.php")
                .addBodyParameter("idTransaksi", id)
                .addBodyParameter("url", url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse" + response);
                        onSuccessVerification();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError"+anError);
                    }
                });
    }


    public void onSuccessVerification(){
        new AlertDialog.Builder(this).setTitle("Verifikasi Berhasil")
                .setMessage("Permintaan Gabung Event Telah Berhasil")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).create().show();
    }
}
