package com.sanjanaa.studentmanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentServiceTest {

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService();
    }

    @Test
    void addStudent_shouldStoreStudentAndAssignId() {
        Student student = studentService.addStudent("Sanjanaa", "CSE");

        assertEquals("Sanjanaa", student.getName());
        assertEquals("CSE", student.getDepartment());
        assertTrue(student.getId() > 0);
    }

    @Test
    void getAllStudents_shouldReturnAddedStudents() {
        studentService.addStudent("Arjun", "IT");
        studentService.addStudent("Meera", "ECE");

        List<Student> students = studentService.getAllStudents();

        assertEquals(2, students.size());
    }
}
