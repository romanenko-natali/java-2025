package ua.university;

import ua.university.config.AppConfig;
import ua.university.model.*;
import ua.university.persistence.PersistenceManager;
import ua.university.exception.DataSerializationException;
import ua.university.repository.GenericRepository;
import ua.university.serializer.DataSerializer;
import ua.university.serializer.JsonDataSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Main demonstration class for repository persistence
 */
public class Main {

    public static void main(String[] args) throws DataSerializationException {

        AppConfig config = new AppConfig();
        PersistenceManager manager = new PersistenceManager(config);

//        demonstrateSubjectSerialization(config, manager);

        System.out.println("\n" + "=".repeat(70) + "\n");

//        demonstrateTeacherSerialization(config, manager);
        Subject subject = new Subject("Mathematics", 5);
        Teacher t = new Teacher("John", "Doe", "john.doe@university.edu",
                "Mathematics", "Professor");
        Group group = new Group(1, "Applied Math", 2025);
        Student student = new Student("Oleh", "Yakovenko", "STU001", group);
        StudentGrade sg = new StudentGrade(student, subject, t, 7, ExamType.EXAM, "2-2025", LocalDate.now());
        DataSerializer<StudentGrade> ser = new JsonDataSerializer<>();
        ser.serialize(List.of(sg), "data.json");
    }

    private static void demonstrateSubjectSerialization(AppConfig config, PersistenceManager manager)
            throws DataSerializationException {

        System.out.println("### SUBJECT SERIALIZATION DEMO ###\n");

        List<Subject> subjectRepo = new ArrayList<>();

        System.out.println("--- Creating sample subjects ---");
        subjectRepo.add(new Subject("Mathematics", 5));
        subjectRepo.add(new Subject("Physics", 4));
        subjectRepo.add(new Subject("Programming", 5));
        subjectRepo.add(new Subject("English", 3));
        subjectRepo.add(new Subject("Chemistry", 4));
        subjectRepo.add(new Subject("History", 2));

        System.out.println("Created " + subjectRepo.size() + " subjects\n");
        displaySubjects(subjectRepo, "Original subjects");

        System.out.println("\n--- Saving to JSON ---");
        manager.save(subjectRepo, "subjects", Subject.class, "JSON");
        System.out.println("Saved to: " + config.getJsonFilePath("subjects"));

        System.out.println("\n--- Saving to YAML ---");
        manager.save(subjectRepo, "subjects", Subject.class, "YAML");
        System.out.println("Saved to: " + config.getYamlFilePath("subjects"));

        System.out.println("\n--- Loading from JSON ---");
        GenericRepository<Subject> loadedFromJson = new GenericRepository<>(
                subject -> subject.name(),
                "Subject"
        );
        manager.load("subjects", Subject.class, "JSON");
        displaySubjects(loadedFromJson.getAll(), "Loaded from JSON");

        System.out.println("\n--- Loading from YAML ---");
        GenericRepository<Subject> loadedFromYaml = new GenericRepository<>(
                subject -> subject.name(),
                "Subject"
        );
        manager.load("subjects", Subject.class, "YAML");
        displaySubjects(loadedFromYaml.getAll(), "Loaded from YAML");
    }

    private static void demonstrateTeacherSerialization(AppConfig config, PersistenceManager manager)
            throws DataSerializationException {

        System.out.println("### TEACHER SERIALIZATION DEMO ###\n");

        List<Teacher> teacherRepo = new ArrayList<>();

        System.out.println("--- Creating sample teachers ---");
        teacherRepo.add(new Teacher("John", "Doe", "john.doe@university.edu",
                "Mathematics", "Professor"));
        teacherRepo.add(new Teacher("Jane", "Smith", "jane.smith@university.edu",
                "Physics", "Associate Professor"));
        teacherRepo.add(new Teacher("Bob", "Johnson", "bob.johnson@university.edu",
                "Computer Science", "Senior Lecturer"));
        teacherRepo.add(new Teacher("Alice", "Williams", "alice.williams@university.edu",
                "Languages", "Lecturer"));
        teacherRepo.add(new Teacher("Charlie", "Brown", "charlie.brown@university.edu",
                "Chemistry", "Professor"));

        System.out.println("Created " + teacherRepo.size() + " teachers\n");
        displayTeachers(teacherRepo, "Original teachers");

        System.out.println("\n--- Saving to JSON ---");
        manager.save(teacherRepo, "teachers", Teacher.class, "JSON");
        System.out.println("Saved to: " + config.getJsonFilePath("teachers"));

        System.out.println("\n--- Saving to YAML ---");
        manager.save(teacherRepo, "teachers", Teacher.class, "YAML");
        System.out.println("Saved to: " + config.getYamlFilePath("teachers"));

        System.out.println("\n--- Loading from JSON ---");
        GenericRepository<Teacher> loadedFromJson = new GenericRepository<>(
                teacher -> teacher.getEmail(),
                "Teacher"
        );
        manager.load("teachers", Teacher.class, "JSON");
        displayTeachers(loadedFromJson.getAll(), "Loaded from JSON");

        System.out.println("\n--- Loading from YAML ---");
        GenericRepository<Teacher> loadedFromYaml = new GenericRepository<>(
                teacher -> teacher.getEmail(),
                "Teacher"
        );
        manager.load("teachers", Teacher.class, "YAML");
        displayTeachers(loadedFromYaml.getAll(), "Loaded from YAML");

        System.out.println("\n--- Sorting teachers by department ---");
        List<Teacher> sortedTeachers = loadedFromJson.getAll();
        sortedTeachers.sort(Teacher.TEACHER_COMPARATOR_BY_DEPARTMENT);
        displayTeachers(sortedTeachers, "Teachers sorted by department");
    }

    private static void displaySubjects(List<Subject> subjects, String title) {
        System.out.println("\n" + title + " (" + subjects.size() + " subjects):");
        System.out.println("-".repeat(60));
        for (Subject subject : subjects) {
            System.out.printf("%-20s | Credits: %d | Difficulty: %s%n",
                    subject.name(),
                    subject.credits(),
                    subject.getDifficultyLevel());
        }
    }

    private static void displayTeachers(List<Teacher> teachers, String title) {
        System.out.println("\n" + title + " (" + teachers.size() + " teachers):");
        System.out.println("-".repeat(80));
        for (Teacher teacher : teachers) {
            System.out.printf("%-15s %-15s | %-25s | %-20s | %s%n",
                    teacher.getFirstName(),
                    teacher.getLastName(),
                    teacher.getEmail(),
                    teacher.getDepartment(),
                    teacher.getPosition());
        }
    }

}