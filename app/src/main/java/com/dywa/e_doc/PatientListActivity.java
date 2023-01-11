package com.dywa.e_doc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dywa.e_doc.data.adapter.PatientListAdapter;
import com.dywa.e_doc.data.request.PatientListRequest;
import com.dywa.e_doc.data.response.PatientListResponse;
import com.dywa.e_doc.data.response.model.PatientData;
import com.dywa.e_doc.data.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientListActivity extends AppCompatActivity {

    private final String TAG = "SDKSDK";
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String TOKEN_KEY = "token_key";
    SharedPreferences sharedpreferences;
    String token;

    ImageButton logout;
    EditText search;

    SweetAlertDialog cDialog;
    SweetAlertDialog pDialog;

    private RecyclerView recyclerView;
    PatientListRequest patientListRequest;

    private PatientListAdapter patientListAdapter;
    private List<PatientData> results = new ArrayList<>();
    boolean isLoading = false;
    PatientListResponse.Response patienListResponse;
    int page = 1;
    int lastItem = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        logout = findViewById(R.id.logout);
        search = findViewById(R.id.search);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page = 1; lastItem = 10;
                    patientListAdapter.clearData();
                    patientListRequest = new PatientListRequest(v.getText().toString(), "", "");
                    for (int i = 1; i <= (patienListResponse.getTotal()/10) + 1; i++)
                        getPatientList(patientListRequest, token, i);
                    return true;
                }
                return false;
            }
        });

        setupView();
        setupRecyclerView();

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_key", null);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlert().confirm(PatientListActivity.this, "Logout", "Yakin ingin logout?", "Ya", "Tidak")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                            editor.apply();

                            Intent i = new Intent(PatientListActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
            }
        });

        patientListRequest = new PatientListRequest("", "", "");
        getPatientList(patientListRequest, token, page);
        initScrollListener();

    }

    private void setupView() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        patientListAdapter = new PatientListAdapter(results, new PatientListAdapter.OnAdapterListener() {
            @Override
            public void onClick(PatientData patientListData) {
                //Toast.makeText(PatientListActivity.this, patientListData.getPatient_name(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PatientListActivity.this, DocumentListActivity.class);
                i.putExtra("intent_id", patientListData.getPatient_id());
                startActivity(i);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(patientListAdapter);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (page <= patienListResponse.getTotal() / 10) {
                        Log.d(TAG, ""+ linearLayoutManager.findLastCompletelyVisibleItemPosition() + ", " + lastItem);
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == (lastItem - page)) {
                            pDialog = new SweetAlert().loading(PatientListActivity.this, "Please Wait ...");
                            lastItem += (patienListResponse.getPatientListData().size() - 1);
                            page++;
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        patienListResponse.getPatientListData().add(null);
        patientListAdapter.notifyItemInserted(patienListResponse.getPatientListData().size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                patienListResponse.getPatientListData().remove(patienListResponse.getPatientListData().size() - 1);
                int scrollPosition = patienListResponse.getPatientListData().size();
                patientListAdapter.notifyItemRemoved(lastItem);

                getPatientList(patientListRequest, token, page);

                patientListAdapter.notifyDataSetChanged();
                pDialog.hide();
                isLoading = false;
            }
        }, 800);


    }

    private void getPatientList(PatientListRequest patientListRequest, String token, int page) {
        ApiService.endpoint(token).getPatientList(page, patientListRequest)
                .enqueue(new Callback<PatientListResponse>() {
                    @Override
                    public void onResponse(Call<PatientListResponse> call, Response<PatientListResponse> response) {
                        if(response.isSuccessful()) {
                            patienListResponse = response.body().getResponse();
                            Log.d(TAG, patienListResponse.toString());
                            Log.d(TAG, "Total : " + patienListResponse.getTotal() + ", Page : " + page);
                            patientListAdapter.setData(patienListResponse.getPatientListData());
                        } else {
                            Log.d(TAG, "Error");
                            new SweetAlert().error(PatientListActivity.this, "Oops...", "Terjadi Kesalahan ...");
                        }
                    }

                    @Override
                    public void onFailure(Call<PatientListResponse> call, Throwable t) {
                        Log.d( TAG, t.toString());
                        new SweetAlert().error(PatientListActivity.this, "Oops...", "Terjadi Kesalahan ...");
                    }
                });
    }
}