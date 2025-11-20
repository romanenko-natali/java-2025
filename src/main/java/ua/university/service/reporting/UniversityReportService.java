package ua.university.service.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.*;
import ua.university.repository.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service for generating university reports using parallel processing
 */
public class UniversityReportService {

    private static final Logger logger = LoggerFactory.getLogger(UniversityReportService.class);

    private final ExecutorService executor;

    public UniversityReportService(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        logger.info("UniversityReportService initialized with {} threads", threadPoolSize);
    }

    public UniversityReportService() {
        this(4);
    }

    /**
     * Generate group statistics report (students count per group)
     */
    public Callable<Map<String, Integer>> createGroupStatisticsTask(StudentRepository studentRepository) {
        return () -> {
            logger.debug("Generating group statistics in thread: {}", Thread.currentThread().getName());

            return studentRepository.getAll().stream()
                    .filter(s -> s.getGroup() != null)
                    .collect(Collectors.groupingBy(
                            s -> s.getGroup().getFullName(),
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                    ));
        };
    }

    /**
     * Generate department workload report (teachers count per department)
     */
    public Callable<Map<String, Long>> createDepartmentWorkloadTask(TeacherRepository teacherRepository) {
        return () -> {
            logger.debug("Generating department workload in thread: {}", Thread.currentThread().getName());

            return teacherRepository.countByDepartment();
        };
    }

    /**
     * Find students graduating this year
     */
    public Callable<List<Student>> createGraduatingStudentsTask(StudentRepository studentRepository) {
        return () -> {
            logger.debug("Finding graduating students in thread: {}", Thread.currentThread().getName());

            return studentRepository.getAll().stream()
                    .filter(s -> s.getGroup() != null && s.getGroup().getCurrentYear() == 4)
                    .toList();
        };
    }

    /**
     * Calculate total credits by difficulty level
     */
    public Callable<Map<String, Integer>> createCreditsByDifficultyTask(SubjectRepository subjectRepository) {
        return () -> {
            logger.debug("Calculating credits by difficulty in thread: {}", Thread.currentThread().getName());

            return subjectRepository.getAll().stream()
                    .collect(Collectors.groupingBy(
                            Subject::getDifficultyLevel,
                            Collectors.summingInt(Subject::credits)
                    ));
        };
    }

    /**
     * Find active groups (not graduated)
     */
    public Callable<List<Group>> createActiveGroupsTask(GroupRepository groupRepository) {
        return () -> {
            logger.debug("Finding active groups in thread: {}", Thread.currentThread().getName());

            return groupRepository.findActive();
        };
    }

    /**
     * Generate full university report using CompletableFuture
     */
    public CompletableFuture<UniversityReport> generateReportAsync(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository) {

        logger.info("Starting parallel report generation...");
        long startTime = System.currentTimeMillis();

        CompletableFuture<Map<String, Integer>> groupStatsFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return createGroupStatisticsTask(studentRepository).call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

        CompletableFuture<Map<String, Long>> departmentWorkloadFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return createDepartmentWorkloadTask(teacherRepository).call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

        CompletableFuture<List<Student>> graduatingStudentsFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return createGraduatingStudentsTask(studentRepository).call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

        CompletableFuture<Map<String, Integer>> creditsByDifficultyFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return createCreditsByDifficultyTask(subjectRepository).call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

        CompletableFuture<List<Group>> activeGroupsFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return createActiveGroupsTask(groupRepository).call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

        return CompletableFuture.allOf(
                groupStatsFuture,
                departmentWorkloadFuture,
                graduatingStudentsFuture,
                creditsByDifficultyFuture,
                activeGroupsFuture
        ).thenApply(v -> {
            long duration = System.currentTimeMillis() - startTime;

            UniversityReport report = new UniversityReport(
                    groupStatsFuture.join(),
                    departmentWorkloadFuture.join(),
                    graduatingStudentsFuture.join(),
                    creditsByDifficultyFuture.join(),
                    activeGroupsFuture.join(),
                    duration
            );

            logger.info("Report generation completed in {} ms", duration);
            return report;
        });
    }

    /**
     * Generate report using ExecutorService with Future
     */
    public UniversityReport generateReportWithExecutor(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository) throws InterruptedException, ExecutionException {

        logger.info("Starting report generation with ExecutorService...");
        long startTime = System.currentTimeMillis();

        Future<Map<String, Integer>> groupStatsFuture =
                executor.submit(createGroupStatisticsTask(studentRepository));

        Future<Map<String, Long>> departmentWorkloadFuture =
                executor.submit(createDepartmentWorkloadTask(teacherRepository));

        Future<List<Student>> graduatingStudentsFuture =
                executor.submit(createGraduatingStudentsTask(studentRepository));

        Future<Map<String, Integer>> creditsByDifficultyFuture =
                executor.submit(createCreditsByDifficultyTask(subjectRepository));

        Future<List<Group>> activeGroupsFuture =
                executor.submit(createActiveGroupsTask(groupRepository));

        long duration = System.currentTimeMillis() - startTime;

        ua.university.service.reporting.UniversityReport report = new ua.university.service.reporting.UniversityReport(
                groupStatsFuture.get(),
                departmentWorkloadFuture.get(),
                graduatingStudentsFuture.get(),
                creditsByDifficultyFuture.get(),
                activeGroupsFuture.get(),
                duration
        );

        logger.info("Report generation completed in {} ms", duration);
        return report;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("UniversityReportService shut down");
    }
}