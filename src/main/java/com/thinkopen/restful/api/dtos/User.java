package com.thinkopen.restful.api.dtos;

public class User {
    private int id = -1, age = -1;
    private String email = null, name = null;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("{\"id\" : %d, \"name\" : \"%s\", \"email\" : \"%s\", \"age\" : %d}", id, name, email, age);
    }
}
