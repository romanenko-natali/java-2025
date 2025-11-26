package ua.university.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.config.AppConfig;
import ua.university.model.*;
import ua.university.persistence.PersistenceManager;
import ua.university.repository.*;
import ua.university.service.LoadResult;
import ua.university.service.loading.DataLoader;
import ua.university.service.loading.ExecutorLoadingStrategy;

/**
 * Application Context Listener - manages application lifecycle
 *
 * LIFECYCLE DEMONSTRATION:
 * 1. contextInitialized() - called ON SERVER STARTUP
 *    - Loads data from JSON files
 *    - Creates repositories (ONCE)
 *    - Stores them in ServletContext as Singleton
 *
 * 2. contextDestroyed() - called ON SERVER SHUTDOWN
 *    - Logs final statistics
 *    - Resource cleanup
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    private long startTime;

    /**
     * contextInitialized() - APPLICATION STARTUP
     * Called ONCE when server starts
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        startTime = System.currentTimeMillis();

        logger.info("=================================================================");
        logger.info("===  APPLICATION CONTEXT INITIALIZED - STARTING UP           ===");
        logger.info("=================================================================");

        ServletContext context = sce.getServletContext();

        try {
            logger.info("Step 1: Creating AppConfig and PersistenceManager...");
            AppConfig config = new AppConfig();
            PersistenceManager persistenceManager = new PersistenceManager(config);
            logger.info("✓ Configuration initialized");

            logger.info("Step 2: Creating repositories...");
            StudentRepository studentRepository = new StudentRepository();
            TeacherRepository teacherRepository = new TeacherRepository();
            GroupRepository groupRepository = new GroupRepository();
            SubjectRepository subjectRepository = new SubjectRepository();
            CourseRepository courseRepository = new CourseRepository();
            logger.info("✓ All repositories created");

            logger.info("Step 3: Loading data from JSON files using ExecutorLoadingStrategy...");
            DataLoader dataLoader = new DataLoader(persistenceManager);

            LoadResult loadResult = dataLoader.load(
                    studentRepository,
                    teacherRepository,
                    groupRepository,
                    subjectRepository,
                    new ExecutorLoadingStrategy(4)
            );

            logger.info("✓ Data loading completed");
            logLoadResults(loadResult, studentRepository, teacherRepository,
                    groupRepository, subjectRepository, courseRepository);

            // Store repositories in ServletContext (as Singleton)
            logger.info("Step 4: Storing repositories in ServletContext...");
            context.setAttribute("studentRepository", studentRepository);
            context.setAttribute("teacherRepository", teacherRepository);
            context.setAttribute("groupRepository", groupRepository);
            context.setAttribute("subjectRepository", subjectRepository);
            context.setAttribute("courseRepository", courseRepository);
            context.setAttribute("persistenceManager", persistenceManager);
            logger.info("✓ Repositories stored in ServletContext");

            long initTime = System.currentTimeMillis() - startTime;
            logger.info("=================================================================");
            logger.info("===  APPLICATION STARTUP SUCCESSFUL in {}ms                  ===", initTime);
            logger.info("===  All servlets will now share these repository instances  ===");
            logger.info("=================================================================");

        } catch (Exception e) {
            logger.error("=================================================================");
            logger.error("===  CRITICAL ERROR DURING APPLICATION STARTUP               ===");
            logger.error("=================================================================");
            logger.error("Failed to initialize application context", e);
            throw new RuntimeException("Application initialization failed", e);
        }
    }

    /**
     * contextDestroyed() - APPLICATION SHUTDOWN
     * Called ONCE when server stops
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=================================================================");
        logger.info("===  APPLICATION CONTEXT DESTROYED - SHUTTING DOWN           ===");
        logger.info("=================================================================");

        ServletContext context = sce.getServletContext();

        try {
            // Get repositories for final statistics and saving
            StudentRepository studentRepository =
                    (StudentRepository) context.getAttribute("studentRepository");
            TeacherRepository teacherRepository =
                    (TeacherRepository) context.getAttribute("teacherRepository");
            GroupRepository groupRepository =
                    (GroupRepository) context.getAttribute("groupRepository");
            SubjectRepository subjectRepository =
                    (SubjectRepository) context.getAttribute("subjectRepository");
            CourseRepository courseRepository =
                    (CourseRepository) context.getAttribute("courseRepository");
            PersistenceManager persistenceManager =
                    (PersistenceManager) context.getAttribute("persistenceManager");

            // Log final statistics
            logger.info("Final statistics:");
            if (studentRepository != null) {
                logger.info("  - Students: {}", studentRepository.size());
            }
            if (teacherRepository != null) {
                logger.info("  - Teachers: {}", teacherRepository.size());
            }
            if (groupRepository != null) {
                logger.info("  - Groups: {}", groupRepository.size());
            }
            if (subjectRepository != null) {
                logger.info("  - Subjects: {}", subjectRepository.size());
            }
            if (courseRepository != null) {
                logger.info("  - Courses: {}", courseRepository.size());
            }

            // Save all data to JSON files before shutdown
            if (persistenceManager != null) {
                logger.info("Saving all data to JSON files...");

                if (studentRepository != null) {
                    persistenceManager.save(
                            studentRepository.getAll(),
                            "students",
                            Student.class,
                            "JSON"
                    );
                    logger.info("✓ Students saved");
                }

                if (teacherRepository != null) {
                    persistenceManager.save(
                            teacherRepository.getAll(),
                            "teachers",
                            Teacher.class,
                            "JSON"
                    );
                    logger.info("✓ Teachers saved");
                }

                if (groupRepository != null) {
                    persistenceManager.save(
                            groupRepository.getAll(),
                            "groups",
                            Group.class,
                            "JSON"
                    );
                    logger.info("✓ Groups saved");
                }

                if (subjectRepository != null) {
                    persistenceManager.save(
                            subjectRepository.getAll(),
                            "subjects",
                            Subject.class,
                            "JSON"
                    );
                    logger.info("✓ Subjects saved");
                }

                if (courseRepository != null) {
                    persistenceManager.save(
                            courseRepository.getAll(),
                            "courses",
                            Course.class,
                            "JSON"
                    );
                    logger.info("✓ Courses saved");
                }

                logger.info("All data successfully saved to JSON files");
            }

            long totalUptime = System.currentTimeMillis() - startTime;
            logger.info("Total application uptime: {}ms ({} seconds)",
                    totalUptime, totalUptime / 1000);

            // Clean up ServletContext
            logger.info("Cleaning up ServletContext attributes...");
            context.removeAttribute("studentRepository");
            context.removeAttribute("teacherRepository");
            context.removeAttribute("groupRepository");
            context.removeAttribute("subjectRepository");
            context.removeAttribute("courseRepository");
            context.removeAttribute("persistenceManager");

            logger.info("=================================================================");
            logger.info("===  APPLICATION SHUTDOWN COMPLETE                           ===");
            logger.info("=================================================================");

        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
    }

    /**
     * Log detailed loading results
     */
    private void logLoadResults(LoadResult loadResult,
                                StudentRepository studentRepo,
                                TeacherRepository teacherRepo,
                                GroupRepository groupRepo,
                                SubjectRepository subjectRepo,
                                CourseRepository courseRepo) {

        logger.info("─────────────────────────────────────────────────────────────────");
        logger.info("DATA LOADING RESULTS:");
        logger.info("─────────────────────────────────────────────────────────────────");
        logger.info("  Students loaded:  {}", studentRepo.size());
        logger.info("  Teachers loaded:  {}", teacherRepo.size());
        logger.info("  Groups loaded:    {}", groupRepo.size());
        logger.info("  Subjects loaded:  {}", subjectRepo.size());
        logger.info("  Courses loaded:   {}", courseRepo.size());
        logger.info("─────────────────────────────────────────────────────────────────");
        logger.info("  Total entities:   {}",
                studentRepo.size() + teacherRepo.size() + groupRepo.size() +
                        subjectRepo.size() + courseRepo.size());
        logger.info("─────────────────────────────────────────────────────────────────");
    }
}

//    curl http://localhost:8080/api/students


//        curl http://localhost:8080/api/students/STU001

//        curl -X POST http://localhost:8080/api/students \
//        -H "Content-Type: application/json" \
//        -d '{"firstName":"Test","lastName":"User","studentId":"STU999","email":"test@test.com","group":{"number":1,"specialty":"Computer Science","startYear":2023}}'

//        curl -X PUT http://localhost:8080/api/students/STU001 \
//        -H "Content-Type: application/json" \
//        -d '{"firstName":"Updated","lastName":"Name","studentId":"STU001","email":"updated@test.com","group":{"number":1,"specialty":"Computer Science","startYear":2023}}'


//        curl -X DELETE http://localhost:8080/api/students/STU001