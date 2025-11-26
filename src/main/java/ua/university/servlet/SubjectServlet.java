package ua.university.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.university.exception.AlreadyExistsException;
import ua.university.exception.DataSerializationException;
import ua.university.exception.InvalidDataException;
import ua.university.model.Subject;
import ua.university.repository.SubjectRepository;
import ua.university.serializer.JsonDataSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST API Servlet for Subject CRUD operations
 *
 * Endpoints:
 * - GET    /subjects                          -> get all subjects
 * - GET    /subjects?minCredits=2&maxCredits=4 -> filter by credits range
 * - GET    /subjects/{name}                   -> get subject by name
 * - POST   /subjects                          -> create new subject
 * - PUT    /subjects/{name}                   -> update subject
 * - DELETE /subjects/{name}                   -> delete subject
 */
@WebServlet(name = "SubjectServlet", urlPatterns = {"/subjects", "/subjects/*"})
public class SubjectServlet extends BaseServlet {

    private JsonDataSerializer<Subject> serializer;
    private SubjectRepository subjectRepository;

    @Override
    public void init() throws ServletException {
        logger.info("=== SubjectServlet init() ===");

        serializer = new JsonDataSerializer<>();

        subjectRepository = (SubjectRepository) getServletContext()
                .getAttribute("subjectRepository");

        if (subjectRepository == null) {
            logger.error("SubjectRepository not found in ServletContext");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("SubjectServlet initialized with {} subjects", subjectRepository.size());
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
                String name = decodePathParam(pathInfo.substring(1));
                handleGetByName(name, resp);
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
            Subject subject = serializer.fromString(requestBody, Subject.class);

            subjectRepository.add(subject);
            logger.info("Subject created: {}", subject.name());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(serializer.toString(subject));

        } catch (AlreadyExistsException e) {
            logger.warn("Subject already exists: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (InvalidDataException e) {
            logger.warn("Invalid subject data: {}", e.getMessage());
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
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Subject name is required");
            return;
        }

        String name = decodePathParam(pathInfo.substring(1));

        try {
            String requestBody = getRequestBody(req);
            Subject updatedSubject = serializer.fromString(requestBody, Subject.class);

            boolean updated = subjectRepository.update(updatedSubject);

            if (!updated) {
                logger.warn("Subject not found: {}", name);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Subject not found: " + name);
                return;
            }

            logger.info("Subject updated: {}", name);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(updatedSubject));

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
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Subject name is required");
            return;
        }

        String name = decodePathParam(pathInfo.substring(1));
        boolean removed = subjectRepository.removeByIdentity(name);

        if (!removed) {
            logger.warn("Subject not found for deletion: {}", name);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Subject not found: " + name);
            return;
        }

        logger.info("Subject deleted: {}", name);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void handleGetWithFilters(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, DataSerializationException {

        String minCreditsParam = req.getParameter("minCredits");
        String maxCreditsParam = req.getParameter("maxCredits");

        List<Subject> subjects;

        if (minCreditsParam != null || maxCreditsParam != null) {
            try {
                int minCredits = minCreditsParam != null ? Integer.parseInt(minCreditsParam) : 1;
                int maxCredits = maxCreditsParam != null ? Integer.parseInt(maxCreditsParam) : 5;

                subjects = subjectRepository.findByCreditsRange(minCredits, maxCredits);
                logger.info("Filter by credits range [{}-{}]: {} subjects",
                        minCredits, maxCredits, subjects.size());
            } catch (NumberFormatException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid credits value: " + e.getMessage());
                return;
            }
        } else {
            subjects = subjectRepository.getAll();
            logger.info("Retrieved all {} subjects", subjects.size());
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(serializer.listToString(subjects));
    }

    private void handleGetByName(String name, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        Optional<Subject> subject = subjectRepository.findByIdentity(name);

        if (subject.isPresent()) {
            logger.info("Found subject: {}", name);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(subject.get()));
        } else {
            logger.warn("Subject not found: {}", name);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Subject not found: " + name);
        }
    }

    /**
     * Decode URL-encoded path parameter (e.g., "Data%20Structures" -> "Data Structures")
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
        logger.info("=== SubjectServlet destroy() ===");
        logger.info("Total requests processed: {}", getRequestCount());
        logger.info("Final subject count: {}",
                subjectRepository != null ? subjectRepository.size() : 0);
    }
}