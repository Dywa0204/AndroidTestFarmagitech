package com.dywa.e_doc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dywa.e_doc.data.request.DetailPatientRequest;
import com.dywa.e_doc.data.request.DocumentDetailRequest;
import com.dywa.e_doc.data.response.DetailPatientResponse;
import com.dywa.e_doc.data.response.DocumentDetailResponse;
import com.dywa.e_doc.data.service.ApiService;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentDetailActivity extends AppCompatActivity {
    private final String TAG = "SDKSDK";

    DocumentDetailRequest documentDetailRequest;
    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String token;

    ImageButton back_btn;
    TextView doc_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        doc_name = findViewById(R.id.doc_name);
        back_btn = findViewById(R.id.back_btn);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_key", null);

        String patient_document_id = getIntent().getStringExtra("intent_patient_document_id");
        String docName = getIntent().getStringExtra("intent_doc_name");

        doc_name.setText(docName);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Log.d(TAG, patient_document_id);
        documentDetailRequest = new DocumentDetailRequest(patient_document_id);
        getDocumentDetail(documentDetailRequest, token);
    }

    private void getDocumentDetail(DocumentDetailRequest documentDetailRequest, String token) {
        ApiService.endpoint(token).getDocumentView(documentDetailRequest)
            .enqueue(new Callback<DocumentDetailResponse>() {
                @Override
                public void onResponse(Call<DocumentDetailResponse> call, Response<DocumentDetailResponse> response) {
                    if(response.isSuccessful()) {
                        ResponseBody documentDetailResponse = response.body().getResponse();
                    } else {
                        new SweetAlert().error(DocumentDetailActivity.this, "Oops...", "Error ...");
                    }
                }

                @Override
                public void onFailure(Call<DocumentDetailResponse> call, Throwable t) {
                    Log.d( TAG, t.toString());
                    new SweetAlert().error(DocumentDetailActivity.this, "Oops...", "Error ...");
                }
            });
    }
}