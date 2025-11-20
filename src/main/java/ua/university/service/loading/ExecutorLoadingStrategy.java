package ua.university.service.loading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.DataSerializationException;
import ua.university.model.*;
import ua.university.repository.*;
import ua.university.service.LoadResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorLoadingStrategy implements LoadingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorLoadingStrategy.class);

    private final int threadPoolSize;

    public ExecutorLoadingStrategy(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public ExecutorLoadingStrategy() {
        this(4);
    }

    @Override
    public LoadResult load(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository,
            DataLoader dataLoader) {

        logger.info("Starting loading with ExecutorService (pool size: {})...", threadPoolSize);
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        try {
            CompletableFuture<Integer> studentsFuture = CompletableFuture
                    .supplyAsync(() -> loadEntity(dataLoader, Student.class, studentRepository), executor);

            CompletableFuture<Integer> teachersFuture = CompletableFuture
                    .supplyAsync(() -> loadEntity(dataLoader, Teacher.class, teacherRepository), executor);

            CompletableFuture<Integer> groupsFuture = CompletableFuture
                    .supplyAsync(() -> loadEntity(dataLoader, Group.class, groupRepository), executor);

            CompletableFuture<Integer> subjectsFuture = CompletableFuture
                    .supplyAsync(() -> loadEntity(dataLoader, Subject.class, subjectRepository), executor);

            CompletableFuture.allOf(studentsFuture, teachersFuture, groupsFuture, subjectsFuture).join();

            long duration = System.currentTimeMillis() - startTime;
            logger.info("ExecutorService loading completed in {} ms", duration);

            return new LoadResult(
                    studentsFuture.join(),
                    teachersFuture.join(),
                    groupsFuture.join(),
                    subjectsFuture.join(),
                    duration
            );
        } finally {
            shutdownExecutor(executor);
        }
    }

    private <T> int loadEntity(DataLoader dataLoader, Class<T> clazz, GenericRepository<T> repository) {
        try {
            return dataLoader.loadEntity(clazz, repository);
        } catch (DataSerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}