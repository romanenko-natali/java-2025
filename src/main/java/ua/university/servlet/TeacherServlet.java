package ua.university.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.university.exception.AlreadyExistsException;
import ua.university.exception.DataSerializationException;
import ua.university.exception.InvalidDataException;
import ua.university.model.Teacher;
import ua.university.repository.TeacherRepository;
import ua.university.serializer.JsonDataSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST API Servlet for Teacher CRUD operations
 *
 * Endpoints:
 * - GET    /teachers              -> get all teachers
 * - GET    /teachers?department=X -> get teachers by department
 * - GET    /teachers/{email}      -> get teacher by email
 * - POST   /teachers              -> create new teacher
 * - PUT    /teachers/{email}      -> update teacher
 * - DELETE /teachers/{email}      -> delete teacher
 */
@WebServlet(name = "TeacherServlet", urlPatterns = {"/teachers", "/teachers/*"})
public class TeacherServlet extends BaseServlet {

    private JsonDataSerializer<Teacher> serializer;
    private TeacherRepository teacherRepository;

    @Override
    public void init() throws ServletException {
        logger.info("=== TeacherServlet init() ===");

        serializer = new JsonDataSerializer<>();

        teacherRepository = (TeacherRepository) getServletContext()
                .getAttribute("teacherRepository");

        if (teacherRepository == null) {
            logger.error("TeacherRepository not found in ServletContext");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("TeacherServlet initialized with {} teachers", teacherRepository.size());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        requestCount++;
        long startTime = System.currentTimeMillis();

        logger.info("Request #{}: {} {}", requestCount, req.getMethod(), req.getRequestURI());

        try {
            super.service(req, resp);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Request #{} completed in {}ms. Status: {}",
                    requestCount, duration, resp.getStatus());
        }
    }

    /**
     * GET /teachers              -> get all teachers
     * GET /teachers?department=X -> get teachers by department
     * GET /teachers/{email}      -> get teacher by email
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String department = req.getParameter("department");
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                if (department != null && !department.isBlank()) {
                    // GET /teachers?department=X
                    handleGetByDepartment(department, resp);
                } else {
                    // GET /teachers
                    handleGetAll(resp);
                }
            } else {
                // GET /teachers/{email}
                String email = pathInfo.substring(1);
                handleGetByEmail(email, resp);
            }
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doGet", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * POST /teachers - Create new teacher
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            String requestBody = getRequestBody(req);
            Teacher teacher = serializer.fromString(requestBody, Teacher.class);

            teacherRepository.add(teacher);
            logger.info("Teacher created: {}", teacher.getEmail());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(serializer.toString(teacher));

        } catch (AlreadyExistsException e) {
            logger.warn("Teacher already exists: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (InvalidDataException e) {
            logger.warn("Invalid teacher data: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPost", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    /**
     * PUT /teachers/{email} - Update existing teacher
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Teacher email is required");
            return;
        }

        String email = pathInfo.substring(1);

        try {
            String requestBody = getRequestBody(req);
            Teacher updatedTeacher = serializer.fromString(requestBody, Teacher.class);

            boolean updated = teacherRepository.update(updatedTeacher);

            if (!updated) {
                logger.warn("Teacher not found: {}", email);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Teacher not found: " + email);
                return;
            }

            logger.info("Teacher updated: {}", email);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(updatedTeacher));

        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPut", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    /**
     * DELETE /teachers/{email} - Delete teacher
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Teacher email is required");
            return;
        }

        String email = pathInfo.substring(1);
        boolean removed = teacherRepository.removeByIdentity(email);

        if (!removed) {
            logger.warn("Teacher not found for deletion: {}", email);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Teacher not found: " + email);
            return;
        }

        logger.info("Teacher deleted: {}", email);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void handleGetAll(HttpServletResponse resp) throws IOException, DataSerializationException {
        List<Teacher> teachers = teacherRepository.getAll();
        logger.info("Retrieved {} teachers", teachers.size());

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(serializer.listToString(teachers));
    }

    private void handleGetByDepartment(String department, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        List<Teacher> teachers = teacherRepository.findByDepartment(department);
        logger.info("Retrieved {} teachers from department '{}'", teachers.size(), department);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(serializer.listToString(teachers));
    }

    private void handleGetByEmail(String email, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        Optional<Teacher> teacher = teacherRepository.findByIdentity(email);

        if (teacher.isPresent()) {
            logger.info("Found teacher: {}", email);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(teacher.get()));
        } else {
            logger.warn("Teacher not found: {}", email);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Teacher not found: " + email);
        }
    }


    @Override
    public void destroy() {
        logger.info("=== TeacherServlet destroy() ===");
        logger.info("Total requests processed: {}", requestCount);
        logger.info("Final teacher count: {}",
                teacherRepository != null ? teacherRepository.size() : 0);
    }
}