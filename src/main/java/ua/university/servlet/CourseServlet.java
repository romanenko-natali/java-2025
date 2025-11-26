package ua.university.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.university.exception.AlreadyExistsException;
import ua.university.exception.DataSerializationException;
import ua.university.exception.InvalidDataException;
import ua.university.model.Course;
import ua.university.repository.CourseRepository;
import ua.university.serializer.JsonDataSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST API Servlet for Course CRUD operations
 *
 * Endpoints:
 * - GET    /courses                -> get all courses
 * - GET    /courses?subject=X      -> filter by subject name
 * - GET    /courses?teacher=X      -> filter by teacher email
 * - GET    /courses?group=X        -> filter by group full name
 * - GET    /courses/{identity}     -> get course by identity
 * - POST   /courses                -> create new course
 * - PUT    /courses/{identity}     -> update course
 * - DELETE /courses/{identity}     -> delete course
 */
@WebServlet(name = "CourseServlet", urlPatterns = {"/courses", "/courses/*"})
public class CourseServlet extends BaseServlet {

    private JsonDataSerializer<Course> serializer;
    private CourseRepository courseRepository;

    @Override
    public void init() throws ServletException {
        logger.info("=== CourseServlet init() ===");

        serializer = new JsonDataSerializer<>();

        courseRepository = (CourseRepository) getServletContext()
                .getAttribute("courseRepository");

        if (courseRepository == null) {
            logger.error("CourseRepository not found in ServletContext");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("CourseServlet initialized with {} courses", courseRepository.size());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetWithFilters(req, resp);
            } else {
                String identity = decodePathParam(pathInfo.substring(1));
                handleGetByIdentity(identity, resp);
            }
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doGet", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            String requestBody = getRequestBody(req);
            Course course = serializer.fromString(requestBody, Course.class);

            courseRepository.add(course);
            logger.info("Course created: {}", course.getIdentity());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(serializer.toString(course));

        } catch (AlreadyExistsException e) {
            logger.warn("Course already exists: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (InvalidDataException e) {
            logger.warn("Invalid course data: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPost", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Course identity is required");
            return;
        }

        String identity = decodePathParam(pathInfo.substring(1));

        try {
            String requestBody = getRequestBody(req);
            Course updatedCourse = serializer.fromString(requestBody, Course.class);

            boolean updated = courseRepository.update(updatedCourse);

            if (!updated) {
                logger.warn("Course not found: {}", identity);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Course not found: " + identity);
                return;
            }

            logger.info("Course updated: {}", identity);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(updatedCourse));

        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPut", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Course identity is required");
            return;
        }

        String identity = decodePathParam(pathInfo.substring(1));
        boolean removed = courseRepository.removeByIdentity(identity);

        if (!removed) {
            logger.warn("Course not found for deletion: {}", identity);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Course not found: " + identity);
            return;
        }

        logger.info("Course deleted: {}", identity);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void handleGetWithFilters(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, DataSerializationException {

        String subject = req.getParameter("subject");
        String teacher = req.getParameter("teacher");
        String group = req.getParameter("group");

        List<Course> courses;

        if (subject != null && !subject.isBlank()) {
            courses = courseRepository.findBySubjectName(subject);
            logger.info("Filter by subject '{}': {} courses", subject, courses.size());
        } else if (teacher != null && !teacher.isBlank()) {
            courses = courseRepository.findByTeacher(teacher);
            logger.info("Filter by teacher '{}': {} courses", teacher, courses.size());
        } else if (group != null && !group.isBlank()) {
            courses = courseRepository.findByGroup(group);
            logger.info("Filter by group '{}': {} courses", group, courses.size());
        } else {
            courses = courseRepository.getAll();
            logger.info("Retrieved all {} courses", courses.size());
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(serializer.listToString(courses));
    }

    private void handleGetByIdentity(String identity, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        Optional<Course> course = courseRepository.findByIdentity(identity);

        if (course.isPresent()) {
            logger.info("Found course: {}", identity);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(course.get()));
        } else {
            logger.warn("Course not found: {}", identity);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Course not found: " + identity);
        }
    }

    /**
     * Decode URL-encoded path parameter
     */
    private String decodePathParam(String param) {
        try {
            return java.net.URLDecoder.decode(param, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn("Failed to decode path param: {}", param);
            return param;
        }
    }

    @Override
    public void destroy() {
        logger.info("=== CourseServlet destroy() ===");
        logger.info("Total requests processed: {}", getRequestCount());
        logger.info("Final course count: {}",
                courseRepository != null ? courseRepository.size() : 0);
    }
}