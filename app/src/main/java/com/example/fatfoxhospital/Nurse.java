package com.example.fatfoxhospital;

public class Nurse {
    private long id;
    private String name;
    private String surname;
    private String user;
    private String password;

    public Nurse(long id, String name, String surname, String user, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.user = user;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
