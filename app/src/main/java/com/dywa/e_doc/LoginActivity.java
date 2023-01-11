package com.dywa.e_doc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dywa.e_doc.data.request.LoginRequest;
import com.dywa.e_doc.data.response.LoginResponse;
import com.dywa.e_doc.data.service.ApiService;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "SDKSDK";
    LoginRequest loginRequest;

    EditText username;
    EditText password;
    Button sign_in;

    SweetAlertDialog pDialog;

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String TOKEN_KEY = "token_key";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        sign_in = findViewById(R.id.sign_in);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    new SweetAlert().error(LoginActivity.this, "Oops...", "Username dan Password tidak boleh kosong!");
                } else {
                    pDialog = new SweetAlert().loading(LoginActivity.this, "Please Wait ...");
                    loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());
                    getDataFromApi(loginRequest);
                }
            }
        });
    }

    private void getDataFromApi(LoginRequest request) {
        ApiService.endpoint("").getUserToken(request)
            .enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if(response.isSuccessful()) {
                        LoginResponse.Response loginResponse = response.body().getResponse();
                        pDialog.hide();

                        if(loginResponse.getToken().isEmpty()) {
                            new SweetAlert().error(LoginActivity.this, "Oops...", "Username atau Password Salah");
                        } else {
                            new SweetAlert().success(LoginActivity.this, "Berhasil ...");
                            Log.d(TAG, "Generated Token : " + loginResponse.getToken());

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(TOKEN_KEY, loginResponse.getToken());
                            editor.apply();

                            Intent i = new Intent(LoginActivity.this, PatientListActivity.class);
                            startActivity(i);
                            MainActivity.mf.finish();
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    new SweetAlert().error(LoginActivity.this, "Oops...", "Terjadi Kesalahan ...");
                    pDialog.hide();
                    Log.d( TAG, t.toString());
                }
            });
    }
}