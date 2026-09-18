package com.sanjanaa.studentmanagement;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Operation 1: Add a student record
    @PostMapping
    public Student addStudent(@RequestParam String name, @RequestParam String department) {
        return studentService.addStudent(name, department);
    }

    // Operation 2: View all student records
    @GetMapping
    public List<Student> viewStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/health")
    public String health() {
        return "Student Management Service is running";
    }
}
