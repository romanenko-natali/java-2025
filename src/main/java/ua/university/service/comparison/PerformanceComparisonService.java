package ua.university.service.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Student;
import ua.university.model.Subject;
import ua.university.repository.StudentRepository;
import ua.university.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service for comparing different parallel processing approaches
 */
public class PerformanceComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceComparisonService.class);

    /**
     * Compare filtering students by group using different approaches
     */
    public ComparisonResult compareStudentFiltering(
            StudentRepository repository, String groupNamePart) {

        logger.info("Comparing student filtering approaches for group: '{}'", groupNamePart);

        long startSequential = System.currentTimeMillis();
        List<Student> sequentialResult = repository.getAll().stream()
                .filter(s -> s.getGroup() != null &&
                        s.getGroup().getFullName().contains(groupNamePart))
                .toList();
        long sequentialTime = System.currentTimeMillis() - startSequential;

        long startParallel = System.currentTimeMillis();
        List<Student> parallelResult = repository.getAll().parallelStream()
                .filter(s -> s.getGroup() != null &&
                        s.getGroup().getFullName().contains(groupNamePart))
                .toList();
        long parallelTime = System.currentTimeMillis() - startParallel;

        long startExecutor = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Student> allStudents = repository.getAll();
            int chunkSize = Math.max(1, allStudents.size() / 4);

            List<CompletableFuture<List<Student>>> futures = new ArrayList<>();

            for (int i = 0; i < allStudents.size(); i += chunkSize) {
                int start = i;
                int end = Math.min(i + chunkSize, allStudents.size());
                List<Student> chunk = allStudents.subList(start, end);

                CompletableFuture<List<Student>> future = CompletableFuture.supplyAsync(() ->
                        chunk.stream()
                                .filter(s -> s.getGroup() != null &&
                                        s.getGroup().getFullName().contains(groupNamePart))
                                .toList(), executor);
                futures.add(future);
            }

            List<Student> executorResult = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .toList();

        } finally {
            executor.shutdown();
        }
        long executorTime = System.currentTimeMillis() - startExecutor;

        ComparisonResult result = new ComparisonResult(
                "Student Filtering by Group",
                sequentialResult.size(),
                sequentialTime,
                parallelTime,
                executorTime
        );

        logger.info("Comparison completed: {}", result);
        return result;
    }

    /**
     * Compare counting subjects by difficulty using different approaches
     */
    public ComparisonResult compareSubjectCounting(SubjectRepository repository) {

        logger.info("Comparing subject counting approaches");

        long startSequential = System.currentTimeMillis();
        Map<String, Long> sequentialResult = repository.getAll().stream()
                .collect(Collectors.groupingBy(
                        Subject::getDifficultyLevel,
                        Collectors.counting()
                ));
        long sequentialTime = System.currentTimeMillis() - startSequential;

        long startParallel = System.currentTimeMillis();
        Map<String, Long> parallelResult = repository.getAll().parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        Subject::getDifficultyLevel,
                        Collectors.counting()
                ));
        long parallelTime = System.currentTimeMillis() - startParallel;

        long startExecutor = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            List<Subject> allSubjects = repository.getAll();

            Callable<Long> countEasy = () -> allSubjects.stream()
                    .filter(s -> "Easy".equals(s.getDifficultyLevel()))
                    .count();

            Callable<Long> countMedium = () -> allSubjects.stream()
                    .filter(s -> "Medium".equals(s.getDifficultyLevel()))
                    .count();

            Callable<Long> countHard = () -> allSubjects.stream()
                    .filter(s -> "Hard".equals(s.getDifficultyLevel()))
                    .count();

            Future<Long> easyFuture = executor.submit(countEasy);
            Future<Long> mediumFuture = executor.submit(countMedium);
            Future<Long> hardFuture = executor.submit(countHard);

            Map<String, Long> executorResult = new ConcurrentHashMap<>();
            executorResult.put("Easy", easyFuture.get());
            executorResult.put("Medium", mediumFuture.get());
            executorResult.put("Hard", hardFuture.get());

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error in executor comparison: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        long executorTime = System.currentTimeMillis() - startExecutor;

        ComparisonResult result = new ComparisonResult(
                "Subject Counting by Difficulty",
                sequentialResult.size(),
                sequentialTime,
                parallelTime,
                executorTime
        );

        logger.info("Comparison completed: {}", result);
        return result;
    }

    /**
     * Compare total credits calculation
     */
    public ComparisonResult compareTotalCreditsCalculation(SubjectRepository repository) {

        logger.info("Comparing total credits calculation approaches");

        long startSequential = System.currentTimeMillis();
        int sequentialResult = repository.getAll().stream()
                .mapToInt(Subject::credits)
                .sum();
        long sequentialTime = System.currentTimeMillis() - startSequential;

        long startParallel = System.currentTimeMillis();
        int parallelResult = repository.getAll().parallelStream()
                .mapToInt(Subject::credits)
                .sum();
        long parallelTime = System.currentTimeMillis() - startParallel;


        long startExecutor = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Subject> allSubjects = repository.getAll();
            int chunkSize = Math.max(1, allSubjects.size() / 4);

            List<Future<Integer>> futures = new java.util.ArrayList<>();

            for (int i = 0; i < allSubjects.size(); i += chunkSize) {
                int start = i;
                int end = Math.min(i + chunkSize, allSubjects.size());
                List<Subject> chunk = allSubjects.subList(start, end);

                Callable<Integer> task = () -> chunk.stream()
                        .mapToInt(Subject::credits)
                        .sum();

                futures.add(executor.submit(task));
            }

            int executorResult = 0;
            for (Future<Integer> future : futures) {
                executorResult += future.get();
            }

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error in executor comparison: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        long executorTime = System.currentTimeMillis() - startExecutor;

        ComparisonResult result = new ComparisonResult(
                "Total Credits Calculation",
                sequentialResult,
                sequentialTime,
                parallelTime,
                executorTime
        );

        logger.info("Comparison completed: {}", result);
        return result;
    }
}