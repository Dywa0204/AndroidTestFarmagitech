package com.dywa.e_doc.data.response;

import com.dywa.e_doc.data.response.model.PatientData;

import java.util.List;

public class PatientListResponse {
    private Response response;

    public Response getResponse () {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {
        private List<PatientData> data;
        private int total;

        public List<PatientData> getPatientListData() {
            return data;
        }

        public void setPatientListData(List<PatientData> patientListData) {
            this.data = patientListData;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "patientListData=" + data.toString() +
                    '}';
        }
    }
}
