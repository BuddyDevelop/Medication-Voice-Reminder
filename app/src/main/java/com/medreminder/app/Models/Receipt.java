package com.medreminder.app.Models;

import java.util.List;

public class Receipt {
    private String doctorName;
    private String doctor;
    private String created;
    private String realizeTo;
    private String receiptId;
    private List<ReceiptMedication> receiptMedications;

    public Receipt() {
    }

    public Receipt( String doctorName, String doctor, String created, String realizeTo, List<ReceiptMedication> receiptMed ) {
        this.doctorName = doctorName;
        this.doctor = doctor;
        this.created = created;
        this.realizeTo = realizeTo;
        this.receiptMedications = receiptMed;
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

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId( String receiptId ) {
        this.receiptId = receiptId;
    }

    public List<ReceiptMedication> getReceiptMedications() {
        return receiptMedications;
    }

    public void setReceiptMedications( List<ReceiptMedication> receiptMedications ) {
        this.receiptMedications = receiptMedications;
    }
}
