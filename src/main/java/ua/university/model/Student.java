package ua.university.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.repository.Identity;
import ua.university.util.PersonUtils;
import ua.university.util.StudentUtils;

import java.util.Comparator;
import java.util.Objects;

public class Student extends Person implements Identity {

    private static final Logger logger = LoggerFactory.getLogger(Student.class);

    private String studentId;
    private Group group;

    private static final Comparator<Student> STUDENT_COMPARATOR =
            Comparator.comparing(Student::getFirstName)
                    .thenComparing(Student::getLastName)
                    .thenComparing(Student::getStudentId);

    public Student(String firstName, String lastName, String email, String studentId, Group group) {
        super(firstName, lastName, email);
        setStudentId(studentId);
        setGroup(group);
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
        StudentUtils.validateStudentId(studentId);
        this.studentId = StudentUtils.formatStudentId(studentId);
        logger.debug("Set studentId='{}'", this.studentId);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        if (group == null) {
            logger.warn("Assigned null group to Student '{}'", getFullName());
        }
        this.group = group;
        logger.debug("Set group='{}'", group);
    }

    @Override
    protected String getFullName() {
        return super.getFullName() + " (Student ID: " + studentId + ")";
    }

    public static Student createStudent(String firstName, String lastName,
                                        String studentId, Group group) {
        PersonUtils.validateName(firstName);
        PersonUtils.validateName(lastName);
        StudentUtils.validateStudentId(studentId);

        String email = PersonUtils.generateEmailFromNames(firstName, lastName, studentId);
        logger.info("Factory method: created Student with email='{}'", email);
        return new Student(firstName, lastName, email, studentId, group);
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

    @Override
    public String getIdentity() {
        return studentId;
    }
}
