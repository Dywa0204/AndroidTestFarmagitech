package com.dywa.e_doc.data.response.model;

public class PatientData {

    private String patient_id;
    private String patient_name;
    private String gender;
    private Long date_birth;
    private String address;

    public String getPatient_id() {
        return patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public String getGender() {
        return gender;
    }

    public Long getDate_birth() {
        return date_birth;
    }

    public String getAddress() {
        return address;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDate_birth(Long date_birth) {
        this.date_birth = date_birth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PatientListData{" +
                "patient_id='" + patient_id + '\'' +
                ", patient_name='" + patient_name + '\'' +
                ", gender='" + gender + '\'' +
                ", date_birth=" + date_birth +
                ", address='" + address + '\'' +
                '}';
    }
}
