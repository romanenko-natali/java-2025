package ua.university.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Student;

import java.util.Comparator;
import java.util.List;

/**
 * Repository for managing Student entities with custom sorting capabilities
 */
public class StudentRepository extends GenericRepository<Student> {
    private static final Logger logger = LoggerFactory.getLogger(StudentRepository.class);

    public StudentRepository() {
        super(Student::getStudentId, "Student");
    }

    /**
     * Sort students by last name, then by first name, then by email (all ascending).
     * This method does not modify the repository - it returns a new sorted copy.
     *
     * @return new sorted list of students
     */
    public List<Student> sortByName() {
        List<Student> allStudents = getAll();
        allStudents.sort(
                Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName)
                        .thenComparing(Student::getEmail)
        );
        logger.info("Sorted {} by last name, first name, and email in ascending order", "Student");
        return allStudents;
    }

    /**
     * Sort students by last name in descending order, then by first name, then by email.
     * This method does not modify the repository - it returns a new sorted copy.
     *
     * @return new sorted list of students
     */
    public List<Student> sortByNameDesc() {
        List<Student> allStudents = getAll();
        allStudents.sort(
                Comparator.comparing(Student::getLastName).reversed()
                        .thenComparing(Student::getFirstName)
                        .thenComparing(Student::getEmail)
        );
        logger.info("Sorted {} by last name (desc), first name, and email", "Student");
        return allStudents;
    }

    /**
     * Sort students by group name, then by last name, then by first name.
     * This method does not modify the repository - it returns a new sorted copy.
     *
     * @return new sorted list of students
     */
    public List<Student> sortByGroup() {
        List<Student> allStudents = getAll();
        allStudents.sort(
                Comparator.comparing((Student s) -> s.getGroup() != null ? s.getGroup().getFullName() : "")
                        .thenComparing(Student::getLastName)
                        .thenComparing(Student::getFirstName)
        );
        logger.info("Sorted {} by group, last name, and first name", "Student");
        return allStudents;
    }
}