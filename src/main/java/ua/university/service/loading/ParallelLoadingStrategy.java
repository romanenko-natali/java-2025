package ua.university.service.loading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.DataSerializationException;
import ua.university.model.*;
import ua.university.repository.*;
import ua.university.service.LoadResult;

import java.util.concurrent.CompletableFuture;

public class ParallelLoadingStrategy implements LoadingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ParallelLoadingStrategy.class);

    @Override
    public LoadResult load(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository,
            DataLoader dataLoader) {

        logger.info("Starting parallel loading with CompletableFuture...");
        long startTime = System.currentTimeMillis();

        CompletableFuture<Integer> studentsFuture = CompletableFuture
                .supplyAsync(() -> loadEntity(dataLoader, Student.class, studentRepository))
                .exceptionally(ex -> handleError("students", ex));

        CompletableFuture<Integer> teachersFuture = CompletableFuture
                .supplyAsync(() -> loadEntity(dataLoader, Teacher.class, teacherRepository))
                .exceptionally(ex -> handleError("teachers", ex));

        CompletableFuture<Integer> groupsFuture = CompletableFuture
                .supplyAsync(() -> loadEntity(dataLoader, Group.class, groupRepository))
                .exceptionally(ex -> handleError("groups", ex));

        CompletableFuture<Integer> subjectsFuture = CompletableFuture
                .supplyAsync(() -> loadEntity(dataLoader, Subject.class, subjectRepository))
                .exceptionally(ex -> handleError("subjects", ex));

        CompletableFuture.allOf(studentsFuture, teachersFuture, groupsFuture, subjectsFuture).join();

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Parallel loading completed in {} ms", duration);

        return new LoadResult(
                studentsFuture.join(),
                teachersFuture.join(),
                groupsFuture.join(),
                subjectsFuture.join(),
                duration
        );
    }

    private <T> int loadEntity(DataLoader dataLoader, Class<T> clazz, GenericRepository<T> repository) {
        try {
            return dataLoader.loadEntity(clazz, repository);
        } catch (DataSerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private int handleError(String entityType, Throwable ex) {
        logger.error("Failed to load {}: {}", entityType, ex.getMessage());
        return 0;
    }
}