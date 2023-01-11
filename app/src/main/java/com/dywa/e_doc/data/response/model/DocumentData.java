package com.dywa.e_doc.data.response.model;

public class DocumentData {

    private String patient_document_id;
    private String patient_id;
    private String document_code;
    private Long created_time;
    private Long modified_time;
    private String title;

    public String getPatient_document_id() {
        return patient_document_id;
    }

    public void setPatient_document_id(String patient_document_id) {
        this.patient_document_id = patient_document_id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getDocument_code() {
        return document_code;
    }

    public void setDocument_code(String document_code) {
        this.document_code = document_code;
    }

    public Long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Long created_time) {
        this.created_time = created_time;
    }

    public Long getModified_time() {
        return modified_time;
    }

    public void setModified_time(Long modified_time) {
        this.modified_time = modified_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
