package com.example.sem_i;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";
    Spinner jenisSpin, statusSpin;
    CheckBox cbTour, cbBandara, cbSovenir, cbKonsum;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    SimpleDateFormat dateFormat;
    TextInputEditText tglMulai, tglSelesai, jamMulai, jamSelesai, title, deskripsi, kategori, instruktur, kota, hotel, jumPeserta, jualInt;
    ImageButton btnTglMulai, btnTglSelesai, btnJamMulai, btnJamSelesai, btnPost;
    LinearLayout btnChoose;
    TextView tvOrganizer;
    ArrayList<String> organizer;
    String jenis, status, downloadUrl;
    StorageTask storageTask;
    StorageReference storageReference;
    Uri imageUri;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        storageReference = FirebaseStorage.getInstance().getReference("ImagePosting");
        AndroidNetworking.initialize(getApplicationContext());

        jenisSpin = findViewById(R.id.spinnerJenis);
        statusSpin = findViewById(R.id.spinnerStatus);
        tvOrganizer = findViewById(R.id.tvTrainProvPost);
        title = findViewById(R.id.txtTitle);
        deskripsi = findViewById(R.id.txtDeskripsi);
        kategori = findViewById(R.id.txtKategori);
        instruktur = findViewById(R.id.txtInstruktur);
        kota = findViewById(R.id.txtKota);
        hotel = findViewById(R.id.txtHotel);
        jumPeserta = findViewById(R.id.txtJumPeserta);
        jualInt = findViewById(R.id.txtJualInt);
        tglMulai = findViewById(R.id.txtdateMulai);
        tglMulai.setEnabled(false);
        tglSelesai = findViewById(R.id.txtdateSelesai);
        tglSelesai.setEnabled(false);
        jamMulai = findViewById(R.id.txttimeMulai);
        jamMulai.setEnabled(false);
        jamSelesai = findViewById(R.id.txttimeSelesai);
        jamSelesai.setEnabled(false);
        cbTour = findViewById(R.id.cb_tour);
        cbBandara = findViewById(R.id.cb_bandara);
        cbSovenir = findViewById(R.id.cb_sovenir);
        cbKonsum = findViewById(R.id.cb_konsumsi);
        btnTglMulai = findViewById(R.id.dateMulai);
        btnTglSelesai = findViewById(R.id.dateSelesai);
        btnJamMulai = findViewById(R.id.timePickMulai);
        btnJamSelesai = findViewById(R.id.timePickSelesai);
        btnPost = findViewById(R.id.btnPost);
        btnChoose = findViewById(R.id.btnChooseImage);
        image = findViewById(R.id.imPosting);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

        ArrayAdapter<CharSequence> adapterJenis = ArrayAdapter.createFromResource(this,
                R.array.array_jenis, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.array_status, android.R.layout.simple_spinner_item);
        adapterJenis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
        String mail = sharedPreferences.getString("mail", "");
        getTrain(mail);

        jenisSpin.setAdapter(adapterJenis);
        statusSpin.setAdapter(adapterStatus);

        jenisSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position!=0)
                    jenis = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        statusSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0)
                    status = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnTglMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateStartDialog();
            }
        });
        btnTglSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateEndDialog();
            }
        });
        btnJamMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeStartDialog();
            }
        });
        btnJamSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeEndDialog();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUploader();
                String Organizer = tvOrganizer.getText().toString();
                String mtitle = title.getText().toString();
                String mdesk = deskripsi.getText().toString();
                String category = kategori.getText().toString();
                String startDate = tglMulai.getText().toString();
                String endDate = tglSelesai.getText().toString();
                String startTime = jamMulai.getText().toString();
                String endTime = jamSelesai.getText().toString();
                String instructure = instruktur.getText().toString();
                String city = kota.getText().toString();
                String mHotel = hotel.getText().toString();
                int participant = Integer.parseInt(jumPeserta.getText().toString());
                int sell = Integer.parseInt(jualInt.getText().toString());
                String facilities = "";

                if (cbTour.isChecked())
                    facilities = facilities+cbTour.getText().toString()+",";
                if (cbBandara.isChecked())
                    facilities = facilities+cbBandara.getText().toString()+",";
                if (cbSovenir.isChecked())
                    facilities = facilities+cbSovenir.getText().toString()+",";
                if (cbKonsum.isChecked())
                    facilities = facilities+cbKonsum.getText().toString();

                addPost(Organizer, mtitle, mdesk, category, jenis, status, startDate, endDate, startTime, endTime
                , instructure, city, mHotel, participant, sell, facilities, downloadUrl);
            }
        });
    }

    public void showDateStartDialog(){
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);

                tglMulai.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void showDateEndDialog(){
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);

                tglSelesai.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void showTimeStartDialog(){
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                jamMulai.setText(hourOfDay+":"+ minute);
            }
        },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    public void showTimeEndDialog(){
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                jamSelesai.setText(hourOfDay+":"+ minute);
            }
        },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    public void addPost(String organizer, String title, String deskripsi, String kategori, String jenis,
                        String status, String tgl_mulai, String tgl_selesai, String jam_mulai, String jam_selesai,
                        String instruktur, String kota, String hotel, int jum_peserta, int jual_int,
                        String fasilitas, String url){
        AndroidNetworking.post(RegisterActivity.ip+"android/insertPost.php")
                .addBodyParameter("nama_organizer", organizer)
                .addBodyParameter("nama_event", title)
                .addBodyParameter("deskripsi", deskripsi)
                .addBodyParameter("kategori", kategori)
                .addBodyParameter("jenis", jenis)
                .addBodyParameter("status", status)
                .addBodyParameter("tgl_mulai", tgl_mulai)
                .addBodyParameter("tgl_selesai", tgl_selesai)
                .addBodyParameter("jam_mulai", jam_mulai)
                .addBodyParameter("jam_selesai", jam_selesai)
                .addBodyParameter("instruktur", instruktur)
                .addBodyParameter("kota", kota)
                .addBodyParameter("hotel", hotel)
                .addBodyParameter("jum_peserta", String.valueOf(jum_peserta))
                .addBodyParameter("jual_int", String.valueOf(jual_int))
                .addBodyParameter("fasilitas", fasilitas)
                .addBodyParameter("url", url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse"+response);
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError"+anError);
                        Toast.makeText(getApplicationContext(), "Error!!!" + anError, Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void getTrain(final String email){
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
                                organizer = new ArrayList<String >();
                                for (int i=0; i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    organizer.add(data.getString("nama_trainprov"));
                                }
                                tvOrganizer.setText(organizer.get(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError"+ anError);
                        Toast.makeText(getApplicationContext(), "Error!!! "+anError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fileChooser(){
        Intent intent= new Intent();
        intent.setType(("image/*"));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    private void fileUploader(){
        if (imageUri !=null){
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
                                    downloadUrl = uri.toString();
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

        }else
            Toast.makeText(getApplicationContext(), "Pilih Gambar Terlebih Dahulu", Toast.LENGTH_LONG).show();
    }

    private String getExtansion(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }
}
