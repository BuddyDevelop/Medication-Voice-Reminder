package com.medreminder.app.Models;

public class Medication {
    private String name;
    private String dose;
    private String doseUnit;
    private String start;
    private String ends;
    private String doctor;

    public Medication() {
    }

    public Medication( String name, String dose, String doseUnit, String start, String ends, String doctor ) {
        this.name = name;
        this.dose = dose;
        this.doseUnit = doseUnit;
        this.start = start;
        this.ends = ends;
        this.doctor = doctor;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDose() {
        return dose;
    }

    public void setDose( String dose ) {
        this.dose = dose;
    }

    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit( String doseUnit ) {
        this.doseUnit = doseUnit;
    }

    public String getStart() {
        return start;
    }

    public void setStart( String start ) {
        this.start = start;
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds( String ends ) {
        this.ends = ends;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor( String doctor ) {
        this.doctor = doctor;
    }
}
