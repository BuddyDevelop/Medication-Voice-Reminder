package com.medreminder.app.Models;

import java.util.List;

public class Prescription {
    private String doctorName;
    private String doctor;
    private String created;
    private String realizeTo;
    private String prescriptionId;
    private List<PrescriptionMedications> prescriptionMedications;

    public Prescription() {
    }

    public Prescription( String doctorName, String doctor, String created, String realizeTo, List<PrescriptionMedications> prescriptionMedications ) {
        this.doctorName = doctorName;
        this.doctor = doctor;
        this.created = created;
        this.realizeTo = realizeTo;
        this.prescriptionMedications = prescriptionMedications;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName( String doctorName ) {
        this.doctorName = doctorName;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor( String doctor ) {
        this.doctor = doctor;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated( String created ) {
        this.created = created;
    }

    public String getRealizeTo() {
        return realizeTo;
    }

    public void setRealizeTo( String realizeTo ) {
        this.realizeTo = realizeTo;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId( String prescriptionId ) {
        this.prescriptionId = prescriptionId;
    }

    public List<PrescriptionMedications> getPrescriptionMedications() {
        return prescriptionMedications;
    }

    public void setPrescriptionMedications( List<PrescriptionMedications> prescriptionMedications ) {
        this.prescriptionMedications = prescriptionMedications;
    }
}
