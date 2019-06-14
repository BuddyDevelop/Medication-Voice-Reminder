package com.example.grzeiek.pracainzynierska;


import java.io.Serializable;

public class Reminder implements Serializable{
    private int id;
    private String medName;
    private String medDose;
    private String medDoseUnit;
    private String reminderTime;
    private String reminderDays;

    public Reminder(){}

    public Reminder( int id, String medName, String reminderTime, String medDose, String medDoseUnit, String reminderDays ){
        this.id = id;
        this.medName = medName;
        this.medDose = medDose;
        this.medDoseUnit = medDoseUnit;
        this.reminderTime = reminderTime;
        this.reminderDays = reminderDays;
    }

    public Reminder(  String medName, String reminderTime, String medDose, String medDoseUnit, String reminderDays ){
        this.medName = medName;
        this.medDose = medDose;
        this.medDoseUnit = medDoseUnit;
        this.reminderTime = reminderTime;
        this.reminderDays = reminderDays;
    }

    public int getId() {
        return id;
    }


    public String getMedName() {
        return medName;
    }

    public String getMedDose() {
        return medDose;
    }

    public String getMedDoseUnit() { return medDoseUnit; }

    public String getReminderTime() { return reminderTime; }

    public String getReminderDays() {
        return reminderDays;
    }
}
