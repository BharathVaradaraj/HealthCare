package com.infosysproject.healthcare;

public class PatientData {

    private String name, patient_id, time;

    public PatientData(){
    }

    public PatientData(String name, String patient_id, String time){
        this.name = name;
        this.patient_id = patient_id;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getTime() {
        return time;
    }
}
