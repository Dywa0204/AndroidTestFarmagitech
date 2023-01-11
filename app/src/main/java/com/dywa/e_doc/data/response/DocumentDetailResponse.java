package com.dywa.e_doc.data.response;

import com.dywa.e_doc.data.response.model.PatientData;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class DocumentDetailResponse {

    private ResponseBody response;

    public ResponseBody getResponse () {
        return response;
    }

    public void setResponse(ResponseBody response) {
        this.response = response;
    }

}
