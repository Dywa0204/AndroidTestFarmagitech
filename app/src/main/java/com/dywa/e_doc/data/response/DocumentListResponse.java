package com.dywa.e_doc.data.response;

import com.dywa.e_doc.data.response.model.DocumentData;
import com.dywa.e_doc.data.response.model.PatientData;

import java.util.List;

public class DocumentListResponse {

    private DocumentListResponse.Response response;

    public DocumentListResponse.Response getResponse () {
        return response;
    }

    public void setResponse(DocumentListResponse.Response response) {
        this.response = response;
    }

    public static class Response {
        private List<DocumentData> data;
        private int total;

        public List<DocumentData> getData() {
            return data;
        }

        public void setData(List<DocumentData> documentDataList) {
            this.data = documentDataList;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
