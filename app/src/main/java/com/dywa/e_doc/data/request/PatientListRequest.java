package com.dywa.e_doc.data.request;

public class PatientListRequest {

    private String key;
    private String patient_id;
    private String patient_name;

    public PatientListRequest(String key, String patient_id, String patient_name) {
        this.key = key;
        this.patient_id = patient_id;
        this.patient_name = patient_name;
    }
}
