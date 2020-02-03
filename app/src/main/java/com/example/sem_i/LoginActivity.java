package com.example.sem_i;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    TextInputEditText mEmail, mPassword;
    FloatingActionButton mLoginBtn;
    LinearLayout mSignUp;
    ProgressBar progressBar;
    SharedPrefManager sharedPrefManager;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.edt_user);
        mPassword = findViewById(R.id.edt_pass);
        mLoginBtn = findViewById(R.id.btn_lgn);
        fAuth = FirebaseAuth.getInstance();
        mSignUp = findViewById(R.id.tv_signup);
        progressBar = findViewById(R.id.progress_bar);
        sharedPrefManager = new SharedPrefManager(this);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mail", email);
                editor.apply();


                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }
                if(password.length()<6){
                    mPassword.setError("Password Must be more than 6 Character");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mLoginBtn.setVisibility(View.GONE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged Successfully", Toast.LENGTH_SHORT).show();
                            startActivity((new Intent(getApplicationContext(), MainActivity.class)));
                            sharedPrefManager.saveSPBolean(sharedPrefManager.SP_LOGINED, true);
                        }else{
                            Toast.makeText(LoginActivity.this, "Error !!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });



        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        if (sharedPrefManager.getLogined()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }
    public String getEmail() {
        return email;
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Realy Exit?")
                .setMessage("Are You Sure Want to Exit")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.super.onBackPressed();
                        moveTaskToBack(true);
                    }
                }).create().show();
    }
}
