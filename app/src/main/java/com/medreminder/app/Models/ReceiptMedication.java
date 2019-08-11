package com.medreminder.app.Models;

public class ReceiptMedication {
    private String medication;
    private String payment;

    public ReceiptMedication() {
    }

    public ReceiptMedication( String medication, String payment ) {
        this.medication = medication;
        this.payment = payment;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication( String medication ) {
        this.medication = medication;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment( String payment ) {
        this.payment = payment;
    }
}
