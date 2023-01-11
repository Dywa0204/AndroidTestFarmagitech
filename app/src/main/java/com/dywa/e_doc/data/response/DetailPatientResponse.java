package com.dywa.e_doc.data.response;

import com.dywa.e_doc.data.response.model.PatientData;

public class DetailPatientResponse {
    private DetailPatientResponse.Response response;

    public DetailPatientResponse.Response getResponse () {
        return response;
    }

    public void setResponse(DetailPatientResponse.Response response) {
        this.response = response;
    }

    public class Response {
        private PatientData data;

        public PatientData getData() {
            return data;
        }

        public void setData(PatientData data) {
            this.data = data;
        }

    }
}
