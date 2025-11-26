package ua.university.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.university.exception.AlreadyExistsException;
import ua.university.exception.DataSerializationException;
import ua.university.exception.InvalidDataException;
import ua.university.model.Group;
import ua.university.repository.GroupRepository;
import ua.university.serializer.JsonDataSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST API Servlet for Group CRUD operations
 *
 * Endpoints:
 * - GET    /groups                  -> get all groups
 * - GET    /groups?specialty=X      -> filter by specialty (partial match)
 * - GET    /groups?number=X         -> filter by group number
 * - GET    /groups?active=true      -> get active groups only
 * - GET    /groups?graduated=true   -> get graduated groups only
 * - GET    /groups/{fullName}       -> get group by full name (e.g., CS01-23)
 * - POST   /groups                  -> create new group
 * - PUT    /groups/{fullName}       -> update group
 * - DELETE /groups/{fullName}       -> delete group
 */
@WebServlet(name = "GroupServlet", urlPatterns = {"/groups", "/groups/*"})
public class GroupServlet extends BaseServlet {

    private JsonDataSerializer<Group> serializer;
    private GroupRepository groupRepository;

    @Override
    public void init() throws ServletException {
        logger.info("=== GroupServlet init() ===");

        serializer = new JsonDataSerializer<>();

        groupRepository = (GroupRepository) getServletContext()
                .getAttribute("groupRepository");

        if (groupRepository == null) {
            logger.error("GroupRepository not found in ServletContext");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("GroupServlet initialized with {} groups", groupRepository.size());
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
                String fullName = pathInfo.substring(1);
                handleGetByFullName(fullName, resp);
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
            Group group = serializer.fromString(requestBody, Group.class);

            groupRepository.add(group);
            logger.info("Group created: {}", group.getFullName());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(serializer.toString(group));

        } catch (AlreadyExistsException e) {
            logger.warn("Group already exists: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (InvalidDataException e) {
            logger.warn("Invalid group data: {}", e.getMessage());
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
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Group full name is required");
            return;
        }

        String fullName = pathInfo.substring(1);

        try {
            String requestBody = getRequestBody(req);
            Group updatedGroup = serializer.fromString(requestBody, Group.class);

            boolean updated = groupRepository.update(updatedGroup);

            if (!updated) {
                logger.warn("Group not found: {}", fullName);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Group not found: " + fullName);
                return;
            }

            logger.info("Group updated: {}", fullName);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(updatedGroup));

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
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Group full name is required");
            return;
        }

        String fullName = pathInfo.substring(1);
        boolean removed = groupRepository.removeByIdentity(fullName);

        if (!removed) {
            logger.warn("Group not found for deletion: {}", fullName);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Group not found: " + fullName);
            return;
        }

        logger.info("Group deleted: {}", fullName);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Handle GET with optional filters
     */
    private void handleGetWithFilters(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, DataSerializationException {

        String specialty = req.getParameter("specialty");
        String numberParam = req.getParameter("number");
        String activeParam = req.getParameter("active");
        String graduatedParam = req.getParameter("graduated");

        List<Group> groups;

        if (specialty != null && !specialty.isBlank()) {
            groups = groupRepository.findBySpecialty(specialty);
            logger.info("Filter by specialty '{}': {} groups", specialty, groups.size());
        } else if (numberParam != null && !numberParam.isBlank()) {
            try {
                int number = Integer.parseInt(numberParam);
                groups = groupRepository.findByNumber(number);
                logger.info("Filter by number {}: {} groups", number, groups.size());
            } catch (NumberFormatException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid number: " + numberParam);
                return;
            }
        } else if ("true".equalsIgnoreCase(activeParam)) {
            groups = groupRepository.findActive();
            logger.info("Filter active groups: {} groups", groups.size());
        } else if ("true".equalsIgnoreCase(graduatedParam)) {
            groups = groupRepository.findGraduated();
            logger.info("Filter graduated groups: {} groups", groups.size());
        } else {
            groups = groupRepository.getAll();
            logger.info("Retrieved all {} groups", groups.size());
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(serializer.listToString(groups));
    }

    private void handleGetByFullName(String fullName, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        Optional<Group> group = groupRepository.findByIdentity(fullName);

        if (group.isPresent()) {
            logger.info("Found group: {}", fullName);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(group.get()));
        } else {
            logger.warn("Group not found: {}", fullName);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Group not found: " + fullName);
        }
    }

    @Override
    public void destroy() {
        logger.info("=== GroupServlet destroy() ===");
        logger.info("Total requests processed: {}", getRequestCount());
        logger.info("Final group count: {}",
                groupRepository != null ? groupRepository.size() : 0);
    }
}