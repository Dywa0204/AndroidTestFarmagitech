package com.dywa.e_doc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dywa.e_doc.data.adapter.DocumentListAdapter;
import com.dywa.e_doc.data.adapter.PatientListAdapter;
import com.dywa.e_doc.data.request.DetailPatientRequest;
import com.dywa.e_doc.data.request.DocumentListRequest;
import com.dywa.e_doc.data.request.PatientListRequest;
import com.dywa.e_doc.data.response.DetailPatientResponse;
import com.dywa.e_doc.data.response.DocumentListResponse;
import com.dywa.e_doc.data.response.LoginResponse;
import com.dywa.e_doc.data.response.PatientListResponse;
import com.dywa.e_doc.data.response.model.DocumentData;
import com.dywa.e_doc.data.response.model.PatientData;
import com.dywa.e_doc.data.service.ApiService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentListActivity extends AppCompatActivity {
    private final String TAG = "SDKSDK";

    DetailPatientRequest detailPatientRequest;
    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String token;

    TextView patient_name;
    TextView patient_id;
    TextView gender;
    TextView date_birth;
    TextView address;
    ImageButton back_btn;
    ImageView user_image;

    EditText search;

    private RecyclerView recyclerView;
    DocumentListRequest documentListRequest;
    DocumentListResponse.Response documentListResponse;

    private DocumentListAdapter documentListAdapter;
    private List<DocumentData> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        search = findViewById(R.id.search);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    documentListRequest = new DocumentListRequest(v.getText().toString());
                    getDocumentList(documentListRequest, token);

                    return true;
                }
                return false;
            }
        });

        setupView();
        setupRecyclerView();

        patient_name = findViewById(R.id.patient_name);
        patient_id = findViewById(R.id.patient_id);
        gender = findViewById(R.id.gender);
        date_birth = findViewById(R.id.date_birth);
        address = findViewById(R.id.address);
        back_btn = findViewById(R.id.back_btn);
        user_image = findViewById(R.id.user_image);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String patient_id = getIntent().getStringExtra("intent_id");
        Log.d(TAG, patient_id);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_key", null);

        detailPatientRequest = new DetailPatientRequest(patient_id);
        getPatientDetail(detailPatientRequest, token);

        documentListRequest = new DocumentListRequest(patient_id);
        getDocumentList(documentListRequest, token);
    }

    private void setupView() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        documentListAdapter = new DocumentListAdapter(results, new DocumentListAdapter.OnAdapterListener() {
            @Override
            public void onClick(DocumentData documentData) {
                Intent i = new Intent(DocumentListActivity.this, DocumentDetailActivity.class);
                i.putExtra("intent_patient_document_id", documentData.getPatient_document_id());
                i.putExtra("intent_doc_name", documentData.getTitle());
                startActivity(i);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(documentListAdapter);
    }

    private void getDocumentList(DocumentListRequest documentListRequest, String token) {
        ApiService.endpoint(token).getDocumentList(documentListRequest)
                .enqueue(new Callback<DocumentListResponse>() {
                    @Override
                    public void onResponse(Call<DocumentListResponse> call, Response<DocumentListResponse> response) {
                        if(response.isSuccessful()) {
                            documentListResponse = response.body().getResponse();
                            documentListAdapter.setData(documentListResponse.getData());
                        } else {
                            Log.d(TAG, "Error");
                            new SweetAlert().error(DocumentListActivity.this, "Oops...", "Terjadi Kesalahan ...");
                        }
                    }

                    @Override
                    public void onFailure(Call<DocumentListResponse> call, Throwable t) {
                        Log.d( TAG, t.toString());
                        new SweetAlert().error(DocumentListActivity.this, "Oops...", "Terjadi Kesalahan ...");
                    }
                });
    }

    private void getPatientDetail(DetailPatientRequest detailPatientRequest, String token) {
        ApiService.endpoint(token).getPatientDetail(detailPatientRequest)
                .enqueue(new Callback<DetailPatientResponse>() {
                    @Override
                    public void onResponse(Call<DetailPatientResponse> call, Response<DetailPatientResponse> response) {
                        if(response.isSuccessful()) {
                            DetailPatientResponse.Response detailPatientResponse = response.body().getResponse();

                            String genderStr = Objects.equals(detailPatientResponse.getData().getGender().toString(), "L") ? "male" : "female";
                            String genderStrIndo = Objects.equals(detailPatientResponse.getData().getGender().toString(), "L") ? "Laki-laki" : "Perempuan";

                            patient_name.setText(detailPatientResponse.getData().getPatient_name().toString());
                            patient_id.setText("No. RM : " + detailPatientResponse.getData().getPatient_id().toString());
                            gender.setText("Jenis Kelamin : " + genderStrIndo);
                            date_birth.setText("Tanggal Lahir : " + getDate(detailPatientResponse.getData().getDate_birth()));
                            address.setText("Alamat : " + detailPatientResponse.getData().getAddress().toString());

                            Picasso.get()
                                .load( "https://xsgames.co/randomusers/avatar.php?g=" + genderStr + "&id=" + detailPatientResponse.getData().getPatient_id().toString())
                                .placeholder(R.drawable.img_placeholder)
                                .error(R.drawable.img_placeholder)
                                .fit(). centerCrop()
                                .into(user_image);
                        } else {
                            new SweetAlert().error(DocumentListActivity.this, "Oops...", "Terjadi Kesalahan ...");
                        }
                    }

                    @Override
                    public void onFailure(Call<DetailPatientResponse> call, Throwable t) {
                        Log.d( TAG, t.toString());
                        new SweetAlert().error(DocumentListActivity.this, "Oops...", "Terjadi Kesalahan ...");
                    }
                });
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}