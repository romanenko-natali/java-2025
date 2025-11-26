package ua.university.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.university.exception.AlreadyExistsException;
import ua.university.exception.DataSerializationException;
import ua.university.exception.InvalidDataException;
import ua.university.model.Student;
import ua.university.repository.StudentRepository;
import ua.university.serializer.JsonDataSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST API Servlet for Student CRUD operations
 * Demonstrates servlet lifecycle: init(), service(), destroy()
 *
 * Endpoints:
 * - GET    /students         -> get all students
 * - GET    /students/{id}    -> get student by ID
 * - POST   /students         -> create new student
 * - PUT    /students/{id}    -> update student
 * - DELETE /students/{id}    -> delete student
 */
@WebServlet(name = "StudentServlet", urlPatterns = {"/students", "/students/*"})
public class StudentServlet extends BaseServlet {

    private JsonDataSerializer<Student> serializer;
    private StudentRepository studentRepository;

    /**
     * init() - Called ONCE when servlet is first loaded
     */
    @Override
    public void init() throws ServletException {
        logger.info("=== StudentServlet init() - Servlet lifecycle started ===");

        serializer = new JsonDataSerializer<>();

        // Get repository from ServletContext (set by AppContextListener)
        studentRepository = (StudentRepository) getServletContext()
                .getAttribute("studentRepository");

        if (studentRepository == null) {
            logger.error("StudentRepository not found in ServletContext");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("StudentServlet initialized with {} students", studentRepository.size());
    }

    /**
     * GET /students - Get all students
     * GET /students/{id} - Get student by ID
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAll(resp);
            } else {
                String studentId = pathInfo.substring(1);
                handleGetById(studentId, resp);
            }
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doGet", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * POST /students - Create new student
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            String requestBody = getRequestBody(req);
            Student student = serializer.fromString(requestBody, Student.class);

            studentRepository.add(student);
            logger.info("Student created: {}", student.getStudentId());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(serializer.toString(student));

        } catch (AlreadyExistsException e) {
            logger.warn("Student already exists: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (InvalidDataException e) {
            logger.warn("Invalid student data: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPost", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    /**
     * PUT /students/{id} - Update existing student
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Student ID is required");
            return;
        }

        String studentId = pathInfo.substring(1);

        try {
            String requestBody = getRequestBody(req);
            Student updatedStudent = serializer.fromString(requestBody, Student.class);

            boolean updated = studentRepository.update(updatedStudent);

            if (!updated) {
                logger.warn("Student not found: {}", studentId);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Student not found: " + studentId);
                return;
            }

            logger.info("Student updated: {}", studentId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(updatedStudent));

        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPut", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    /**
     * DELETE /students/{id} - Delete student
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Student ID is required");
            return;
        }

        String studentId = pathInfo.substring(1);
        boolean removed = studentRepository.removeByIdentity(studentId);

        if (!removed) {
            logger.warn("Student not found for deletion: {}", studentId);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Student not found: " + studentId);
            return;
        }

        logger.info("Student deleted: {}", studentId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Handle GET all students
     */
    private void handleGetAll(HttpServletResponse resp) throws IOException, DataSerializationException {
        List<Student> students = studentRepository.getAll();
        logger.info("Retrieved {} students", students.size());

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(serializer.listToString(students));
    }

    /**
     * Handle GET student by ID
     */
    private void handleGetById(String studentId, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        Optional<Student> student = studentRepository.findByIdentity(studentId);

        if (student.isPresent()) {
            logger.info("Found student: {}", studentId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(student.get()));
        } else {
            logger.warn("Student not found: {}", studentId);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Student not found: " + studentId);
        }
    }


    /**
     * destroy() - Called ONCE when servlet is being removed
     */
    @Override
    public void destroy() {
        logger.info("=== StudentServlet destroy() ===");
        logger.info("Total requests processed: {}", requestCount);
        logger.info("Final student count: {}",
                studentRepository != null ? studentRepository.size() : 0);
    }
}
