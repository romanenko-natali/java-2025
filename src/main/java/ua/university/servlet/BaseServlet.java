package ua.university.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Base servlet with common functionality for all API servlets
 */
public abstract class BaseServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    protected int requestCount = 0;

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

    protected void sendError(HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.setContentType(CONTENT_TYPE_JSON);

        String json = String.format(
                "{\"status\":%d,\"message\":\"%s\",\"timestamp\":%d}",
                status, message, System.currentTimeMillis()
        );
        resp.getWriter().write(json);
    }

    protected String getRequestBody(HttpServletRequest req) throws IOException {
        return req.getReader().lines().collect(Collectors.joining());
    }

    protected int getRequestCount() {
        return requestCount;
    }
}