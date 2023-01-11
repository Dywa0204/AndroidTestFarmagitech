package com.dywa.e_doc.data.service;

import android.provider.ContactsContract;

import com.dywa.e_doc.data.request.DetailPatientRequest;
import com.dywa.e_doc.data.request.DocumentDetailRequest;
import com.dywa.e_doc.data.request.DocumentListRequest;
import com.dywa.e_doc.data.request.LoginRequest;
import com.dywa.e_doc.data.request.PatientListRequest;
import com.dywa.e_doc.data.response.DetailPatientResponse;
import com.dywa.e_doc.data.response.DocumentDetailResponse;
import com.dywa.e_doc.data.response.DocumentListResponse;
import com.dywa.e_doc.data.response.LoginResponse;
import com.dywa.e_doc.data.response.PatientListResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface ApiEndpoint {
    @POST("auth/generate_token")
    Call<LoginResponse> getUserToken(@Body LoginRequest loginRequest);

    @POST("patient/list/{page}")
    Call<PatientListResponse> getPatientList(@Path("page") int page, @Body PatientListRequest patientListRequest);

    @POST("patient/get")
    Call<DetailPatientResponse> getPatientDetail(@Body DetailPatientRequest detailPatientRequest);

    @POST("patient_document/list_doc")
    Call<DocumentListResponse> getDocumentList(@Body DocumentListRequest documentListRequest);

    @POST("patient_document/serve_doc")
    Call<DocumentDetailResponse> getDocumentView(@Body DocumentDetailRequest documentDetailRequest);


}
