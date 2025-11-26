package ua.university.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.university.model.Student;
import ua.university.repository.StudentRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Test servlet to verify application initialization
 * URL: /test
 */
@WebServlet(name = "TestServlet", urlPatterns = {"/test"})
public class TestServlet extends BaseServlet {

    private StudentRepository studentRepository;

    @Override
    public void init() throws ServletException {
        logger.info("=== TestServlet init() ===");

        studentRepository = (StudentRepository) getServletContext()
                .getAttribute("studentRepository");

        if (studentRepository == null) {
            logger.error("StudentRepository not found in ServletContext!");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("TestServlet initialized. Students in repository: {}",
                studentRepository.size());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("=== STUDENT MANAGEMENT API - TEST PAGE ===\n");

        if (studentRepository == null) {
            out.println("ERROR: StudentRepository is NULL");
            return;
        }

        out.println("StudentRepository loaded successfully");
        out.println("Total students in repository: " + studentRepository.size());
        out.println();

        List<Student> students = studentRepository.getAll();

        if (students.isEmpty()) {
            out.println("No students found in repository");
            out.println("\nPossible reasons:");
            out.println("1. JSON files are empty");
            out.println("2. JSON files not found");
            out.println("3. Data loading failed");
        } else {
            out.println("--- FIRST STUDENT ---");
            Student firstStudent = students.get(0);
            out.println("Student ID:   " + firstStudent.getStudentId());
            out.println("First Name:   " + firstStudent.getFirstName());
            out.println("Last Name:    " + firstStudent.getLastName());
            out.println("Email:        " + firstStudent.getEmail());
            out.println("Group:        " + firstStudent.getGroup().getFullName());
            out.println();

            out.println("--- ALL STUDENTS ---");
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                out.printf("%d. %s %s (ID: %s)\n",
                        i + 1,
                        s.getFirstName(),
                        s.getLastName(),
                        s.getStudentId());
            }
        }

        out.println();
        out.println("=== TEST COMPLETE ===");

        logger.info("Test page accessed. Returned {} students", students.size());
    }
}