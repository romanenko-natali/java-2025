package ua.university.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.util.PersonUtils;
import ua.university.util.StudentUtils;

import java.util.Objects;

public class Student extends Person {

    private static final Logger logger = LoggerFactory.getLogger(Student.class);

    private String studentId;
    private Group group;

    public Student() {
        super();
    }

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
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", studentId='" + studentId + '\'' +
                ", group=" + (group != null ? group.toString() : "No group") +
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
}
