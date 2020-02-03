package com.example.sem_i;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    TextInputEditText mEmail, mNamaOrganizer, mPassword, mConPass;
    FloatingActionButton mSbmButton;
    LinearLayout mSignin;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    String userID;
    SharedPrefManager sharedPrefManager;

    public static String ip = "http://192.168.43.79/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG, "onCreat: inisialisasi");

        mEmail = findViewById(R.id.email);
        mNamaOrganizer = findViewById(R.id.nama_organizer);
        mPassword = findViewById(R.id.password);
        mConPass = findViewById(R.id.password);
        mSbmButton = findViewById(R.id.btn_sbmt);
        mSignin = findViewById(R.id.tv_signin);
        progressBar = findViewById(R.id.progress_bar1);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        sharedPrefManager = new SharedPrefManager(this);



        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mSbmButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mail", email);
                editor.apply();
                final String namaOrganizer = mNamaOrganizer.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String conPass = mConPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }
                if (TextUtils.isEmpty(namaOrganizer)){
                    mNamaOrganizer.setError("Nama Perusahaan is Required");
                    return;
                }
                if (password.length()<6){
                    mPassword.setError("Password Must be more than 6 Character");
                    return;
                }
                if (!conPass.equals(password)){
                    mConPass.setError("Confirm Password Not Same With The First One");
                    return;
                }

                addData(email, namaOrganizer);
                progressBar.setVisibility(View.VISIBLE);
                mSbmButton.setVisibility(View.GONE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("namaOrganizer", namaOrganizer);
                            user.put("email", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess : User Profile is Created for "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFuoler : "+ e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            sharedPrefManager.saveSPBolean(sharedPrefManager.SP_LOGINED, true);
                        }else{
                            Toast.makeText(RegisterActivity.this, "Error !" +task.getException().getMessage(),Toast.LENGTH_SHORT);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }

    public void addData(String email, String namaOrganizer){
        AndroidNetworking.post(ip+"android/inserttrainprov.php")
                .addBodyParameter("nama_organizer", namaOrganizer)
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

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Realy Exit?")
                .setMessage("Are You Sure Want to Exit")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RegisterActivity.super.onBackPressed();
                        moveTaskToBack(true);
                    }
                }).create().show();
    }

}
