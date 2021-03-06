package com.codecool.lms.model;

import java.util.List;

public class Day {
    private final String date;
    private List<Student> students;
    private int id;

    public Day(int id, List<Student> students, String date) {
        this.id = id;
        this.date = date;
        this.students = students;
    }

    public String getDate() {
        return date;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public int getId() {
        return id;
    }
}
