package com.example.grzeiek.pracainzynierska;


import java.io.Serializable;

public class Reminder implements Serializable{
    private int id;
    private String medName;
    private String medDose;
    private String medDoseUnit;
    private String reminderTime;
    private String reminderDays;
    private String reminderAlarmId;

    public Reminder(){}

    //constructor with all data
    public Reminder( int id, String medName, String reminderTime, String medDose, String medDoseUnit, String reminderDays, String reminderAlarmIds ){
        this.id = id;
        this.medName = medName;
        this.medDose = medDose;
        this.medDoseUnit = medDoseUnit;
        this.reminderTime = reminderTime;
        this.reminderDays = reminderDays;
        this.reminderAlarmId = reminderAlarmIds;
    }

    //constructor without reminder alarm Ids
    public Reminder( int id, String medName, String reminderTime, String medDose, String medDoseUnit, String reminderDays ){
        this.id = id;
        this.medName = medName;
        this.medDose = medDose;
        this.medDoseUnit = medDoseUnit;
        this.reminderTime = reminderTime;
        this.reminderDays = reminderDays;
    }

    // constructor without record id, and reminder alarm ids
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

    public String getReminderAlarmId() { return reminderAlarmId; }

    public void setReminderAlarmId( String reminderAlarmId ) {
        this.reminderAlarmId = reminderAlarmId;
    }
}
