package ua.university;

import ua.university.config.AppConfig;
import ua.university.exception.DataSerializationException;
import ua.university.model.Student;
import ua.university.model.Teacher;
import ua.university.persistence.PersistenceManager;
import ua.university.repository.*;
import ua.university.service.LoadResult;
import ua.university.service.comparison.ComparisonResult;
import ua.university.service.comparison.PerformanceComparisonService;
import ua.university.service.loading.*;
import ua.university.service.reporting.UniversityReport;
import ua.university.service.reporting.UniversityReportService;

import java.util.List;

/**
 * Main class demonstrating multithreading and concurrency features
 */
public class Main {

    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        PersistenceManager persistenceManager = new PersistenceManager(config);

        StudentRepository studentRepository = new StudentRepository();
        TeacherRepository teacherRepository = new TeacherRepository();
        GroupRepository groupRepository = new GroupRepository();
        SubjectRepository subjectRepository = new SubjectRepository();

        DataLoader dataLoader = new DataLoader(persistenceManager);
        LoadResult executorResult = dataLoader.load(
                studentRepository,
                teacherRepository,
                groupRepository,
                subjectRepository,
                new ExecutorLoadingStrategy(4)
        );
        System.out.println(executorResult);

        List<Student> students = studentRepository.getAll();
        students.get(0).setFirstName("New");
        try {
            persistenceManager.save(students, "students", Student.class, "JSON");
        } catch (DataSerializationException e) {
            throw new RuntimeException(e);
        }


//        demonstrateParallelLoading(
//                persistenceManager,
//                studentRepository,
//                teacherRepository,
//                groupRepository,
//                subjectRepository
//        );

//        demonstrateReportGeneration(
//                studentRepository,
//                teacherRepository,
//                groupRepository,
//                subjectRepository
//        );

//        demonstratePerformanceComparison(
//                studentRepository,
//                subjectRepository
//        );

    }

    /**
     * Demonstrate parallel data loading with different strategies
     */
    private static void demonstrateParallelLoading(
            PersistenceManager persistenceManager,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository) {


        DataLoader dataLoader = new DataLoader(persistenceManager);

        LoadResult sequentialResult = dataLoader.load(
                studentRepository,
                teacherRepository,
                groupRepository,
                subjectRepository,
                new SequentialLoadingStrategy()
        );
        System.out.println(sequentialResult);

        clearRepositories(studentRepository, teacherRepository, groupRepository, subjectRepository);

        LoadResult parallelResult = dataLoader.load(
                studentRepository,
                teacherRepository,
                groupRepository,
                subjectRepository,
                new ParallelLoadingStrategy()
        );
        System.out.println(parallelResult);

        clearRepositories(studentRepository, teacherRepository, groupRepository, subjectRepository);

        LoadResult executorResult = dataLoader.load(
                studentRepository,
                teacherRepository,
                groupRepository,
                subjectRepository,
                new ExecutorLoadingStrategy(4)
        );
        System.out.println(executorResult);

        System.out.println("\n=== Loading Time Comparison ===");
        System.out.printf("Sequential:      %d ms%n", sequentialResult.durationMs());
        System.out.printf("Parallel:        %d ms%n", parallelResult.durationMs());
        System.out.printf("ExecutorService: %d ms%n", executorResult.durationMs());
    }

    /**
     * Demonstrate report generation using parallel processing
     */
    private static void demonstrateReportGeneration(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository) {


        UniversityReportService reportService = new UniversityReportService(4);

        try {
            UniversityReport report = reportService.generateReportAsync(
                    studentRepository,
                    teacherRepository,
                    groupRepository,
                    subjectRepository
            ).join();

            System.out.println(report);

            UniversityReport report2 = reportService.generateReportWithExecutor(
                    studentRepository,
                    teacherRepository,
                    groupRepository,
                    subjectRepository
            );

            System.out.printf("ExecutorService report generation: %d ms%n", report2.generationTimeMs());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reportService.shutdown();
        }
    }

    /**
     * Demonstrate performance comparison between different approaches
     */
    private static void demonstratePerformanceComparison(
            StudentRepository studentRepository,
            SubjectRepository subjectRepository) {


        PerformanceComparisonService comparisonService = new PerformanceComparisonService();

        ComparisonResult filterResult = comparisonService.compareStudentFiltering(
                studentRepository, "КО"
        );
        System.out.println(filterResult);

        ComparisonResult countResult = comparisonService.compareSubjectCounting(subjectRepository);
        System.out.println(countResult);

        ComparisonResult creditsResult = comparisonService.compareTotalCreditsCalculation(subjectRepository);
        System.out.println(creditsResult);
    }

    /**
     * Clear all repositories for clean test runs
     */
    private static void clearRepositories(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository) {

        studentRepository.clear();
        teacherRepository.clear();
        groupRepository.clear();
        subjectRepository.clear();
    }
}