package ua.university.model;

import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;
import ua.university.util.PersonUtils;
import ua.university.util.ValidationUtils;

import java.util.Comparator;
import java.util.Objects;

public class Student extends Person {

    private static final Logger logger = LoggerFactory.getLogger(Student.class);

    @NotBlank(message = "Student ID cannot be null or blank")
    @Pattern(
            regexp = "^[A-Z]{3}\\d{3}$",
            message = "Student ID must match pattern 'AAA999' (3 uppercase letters followed by 3 digits)"
    )
    private String studentId;

    @NotNull(message = "Group cannot be null")
    private Group group;

    private static final Comparator<Student> STUDENT_COMPARATOR =
            Comparator.comparing(Student::getFirstName)
                    .thenComparing(Student::getLastName)
                    .thenComparing(Student::getStudentId);

    public Student(String firstName, String lastName, String email, String studentId, Group group) {
        super(firstName, lastName, email);
        this.studentId = studentId;
        this.group = group;

        ValidationUtils.validate(this);

        logger.info("Created Student: {}", this);
    }

    public Student(String firstName, String lastName, String studentId, Group group) {
        this(firstName, lastName,
                PersonUtils.generateEmailFromNames(firstName, lastName, studentId),
                studentId,
                group);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId != null) {
            studentId = formatStudentId(studentId);
        }

        String oldValue = this.studentId;
        this.studentId = studentId;

        try {
            ValidationUtils.validate(this);
            logger.debug("Set studentId='{}'", this.studentId);
        } catch (InvalidDataException e) {
            this.studentId = oldValue;
            throw e;
        }
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        Group oldValue = this.group;
        this.group = group;

        try {
            ValidationUtils.validate(this);
            logger.debug("Set group='{}'", group);
        } catch (InvalidDataException e) {
            this.group = oldValue;
            throw e;
        }
    }

    @Override
    protected String getFullName() {
        return super.getFullName() + " (Student ID: " + studentId + ")";
    }

    private String formatStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            logger.error("Cannot format null or empty student ID: '{}'", studentId);
            throw new InvalidDataException("Student ID must not be null or empty");
        }

        String formatted = studentId.toUpperCase().trim();
        logger.debug("formatStudentId: input='{}' -> formatted='{}'", studentId, formatted);
        return formatted;
    }

    public static Student createStudent(String firstName, String lastName,
                                        String studentId, Group group) {
        Student student = new Student(firstName, lastName, studentId, group);

        ValidationUtils.validate(student);
        logger.info("Factory method: created Student with id='{}'", studentId);
        return student;
    }

    public static Person createValidPerson(String firstName, String lastName, String email) {
        Person person = new Person(firstName, lastName, email);
        ValidationUtils.validate(person);
        return person;
    }


    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", group=" + (group != null ? group.getFullName() : "No group") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(studentId, student.studentId) &&
                Objects.equals(group, student.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentId, group);
    }

    @Override
    public int compareTo(Person other) {
        if (other instanceof Student student) {
            return STUDENT_COMPARATOR.compare(this, student);
        }
        return super.compareTo(other);
    }
}