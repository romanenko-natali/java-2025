package ua.university.service.loading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.DataSerializationException;
import ua.university.model.*;
import ua.university.repository.*;
import ua.university.service.LoadResult;

public class SequentialLoadingStrategy implements LoadingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SequentialLoadingStrategy.class);

    @Override
    public LoadResult load(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository,
            DataLoader dataLoader) {

        logger.info("Starting sequential loading...");
        long startTime = System.currentTimeMillis();

        try {
            int students = dataLoader.loadEntity(Student.class, studentRepository);
            int teachers = dataLoader.loadEntity(Teacher.class, teacherRepository);
            int groups = dataLoader.loadEntity(Group.class, groupRepository);
            int subjects = dataLoader.loadEntity(Subject.class, subjectRepository);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Sequential loading completed in {} ms", duration);

            return new LoadResult(students, teachers, groups, subjects, duration);
        } catch (DataSerializationException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Sequential loading failed after {} ms: {}", duration, e.getMessage());
            throw new RuntimeException("Failed to load data", e);
        }
    }
}