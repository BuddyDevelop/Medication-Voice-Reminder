package com.medreminder.app.Models;

public class User {
    private String name, email, pesel;

    public User(){}

    public User( String name, String email, String pesel ) {
        this.name = name;
        this.email = email;
        this.pesel = pesel;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel( String pesel ) {
        this.pesel = pesel;
    }
}
