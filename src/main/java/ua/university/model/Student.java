package ua.university.model;

import ua.university.util.PersonUtils;
import ua.university.util.StudentUtils;

import java.util.Objects;

public class Student extends Person {
    private String studentId;
    private Group group;

    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String email, String studentId, Group group) {
        super(firstName, lastName, email);
        setStudentId(studentId);
        this.group = group;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (StudentUtils.isValidStudentId(studentId)) {
            this.studentId = StudentUtils.formatStudentId(studentId);
        }
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    protected String getFullName() {
        return super.getFullName() + " (Student ID: " + studentId + ")";
    }

    public static Student createStudent(String firstName, String lastName,
                                        String studentId, Group group) {
        if (StudentUtils.isValidStudentId(studentId) &&
                PersonUtils.isValidName(firstName) &&
                PersonUtils.isValidName(lastName)) {
            String email = PersonUtils.generateEmailFromNames(firstName, lastName, studentId);
            return new Student(firstName, lastName, email, studentId, group);
        }
        return null;
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
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId) &&
                Objects.equals(group, student.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentId, group);
    }
}
