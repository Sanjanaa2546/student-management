package com.sanjanaa.studentmanagement;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StudentService {

    private final List<Student> students = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Student addStudent(String name, String department) {
        Student student = new Student(idCounter.getAndIncrement(), name, department);
        students.add(student);
        return student;
    }

    public List<Student> getAllStudents() {
        return students;
    }
}
