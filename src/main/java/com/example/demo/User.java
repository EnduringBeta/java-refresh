package com.example.demo;

public class User {
    private String name;
    private int age;
    private String profession;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    // Default constructor needed for JSON deserialization
    public User() {}

    public User(String name, int age, String profession) {
        this.name = name;
        this.age = age;
        this.profession = profession;
    }

    @Override
    public String toString() {
        return name + " (" + age + ", " + profession + ")";
    }

    
}
