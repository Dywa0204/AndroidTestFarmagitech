package com.dywa.e_doc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dywa.e_doc.data.adapter.DocumentListAdapter;
import com.dywa.e_doc.data.request.DetailPatientRequest;
import com.dywa.e_doc.data.request.DocumentDetailRequest;
import com.dywa.e_doc.data.request.DocumentListRequest;
import com.dywa.e_doc.data.response.DetailPatientResponse;
import com.dywa.e_doc.data.response.DocumentListResponse;
import com.dywa.e_doc.data.response.model.DocumentData;
import com.dywa.e_doc.data.service.ApiService;
import com.rajat.pdfviewer.PdfViewerActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentListActivity extends AppCompatActivity {
    private final String TAG = "SDKSDK";

    DetailPatientRequest detailPatientRequest;
    DocumentListRequest documentListRequest;
    DocumentListResponse.Response documentListResponse;
    DocumentDetailRequest documentDetailRequest;

    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String token;

    public static SweetAlertDialog loading;

    TextView patient_name;
    TextView patient_id;
    TextView gender;
    TextView date_birth;
    TextView address;
    ImageButton back_btn;
    ImageView user_image;
    EditText search;

    private RecyclerView recyclerView;

    private DocumentListAdapter documentListAdapter;
    private final List<DocumentData> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_key", null);

        patient_name = findViewById(R.id.patient_name);
        patient_id = findViewById(R.id.patient_id);
        gender = findViewById(R.id.gender);
        date_birth = findViewById(R.id.date_birth);
        address = findViewById(R.id.address);
        back_btn = findViewById(R.id.back_btn);
        user_image = findViewById(R.id.user_image);
        search = findViewById(R.id.search);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        String patient_id = getIntent().getStringExtra("intent_id");

        detailPatientRequest = new DetailPatientRequest(patient_id);
        getPatientDetail(detailPatientRequest, token);

        documentListRequest = new DocumentListRequest(patient_id);
        getDocumentList(documentListRequest, token);


        setupView();
        setupRecyclerView();
    }

    private void setupView() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        documentListAdapter = new DocumentListAdapter(results, new DocumentListAdapter.OnAdapterListener() {
            @Override
            public void onClick(DocumentData documentData) {
                loading = new SweetAlert().loading(DocumentListActivity.this, "Preparing Pdf...");
                documentDetailRequest = new DocumentDetailRequest(Integer.parseInt(documentData.getPatient_document_id()));
                getDocumentDetail(documentDetailRequest, token, documentData.getTitle());
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

    public void getDocumentDetail(DocumentDetailRequest documentDetailRequest, String token, String title) {
        ApiService.endpoint(token).getDocumentView(documentDetailRequest)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            Log.d(TAG, "Success");

                            try {
                                new AsyncTask<Void, Long, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        previewPdf(response.body(), title);
                                        return null;
                                    }
                                }.execute();
                            } finally {
                                loading.hide();
                            }

                        } else {
                            try {
                                Log.d(TAG, "error " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            new SweetAlert().error(DocumentListActivity.this, "Oops...", "Error ...");
                            loading.hide();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d( TAG, "error : " + t);
                        new SweetAlert().error(DocumentListActivity.this, "Oops...", "Error ...");
                        loading.hide();
                    }
                });
    }

    public void previewPdf(ResponseBody body, String title) {
        try {
            new File("/data/data/" + getPackageName() + "/pdfFile").mkdir();
            File destinationFile = new File("/data/data/" + getPackageName() + "/pdfFile/downloadedFile.pdf");

            InputStream is = null;
            OutputStream os = null;

            try {
                Log.d(TAG, "File Size=" + body.contentLength());

                is = body.byteStream();
                os = new FileOutputStream(destinationFile);

                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                    progress +=count;
                    Log.d(TAG, "Progress: " + progress + "/" + body.contentLength() + " >>>> " + (float) progress/body.contentLength());
                }

                os.flush();

                Log.d(TAG, "File saved successfully!");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                startActivity(
                        PdfViewerActivity.Companion.launchPdfFromPath(
                                DocumentListActivity.this,
                                "/data/data/" + getPackageName() + "/pdfFile/downloadedFile.pdf",
                                title,
                                "",
                                false,
                                false
                        )
                );

                if (is != null) is.close();
                if (os != null) os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}