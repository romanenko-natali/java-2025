package ua.university.model;

import ua.university.util.PersonUtils;
import ua.university.util.StudentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Student extends Person {
    private String studentId;
    private Group group;

    private Student() {
        super();
    }

    public Student(String firstName, String lastName, String email, String studentId, Group group) {
        super(firstName, lastName, email);
        setStudentId(studentId);
        this.group = group;
    }

    public Student(String firstName, String lastName, String studentId, Group group) {
        super();
        Person p = Person.createPerson(firstName, lastName);
        this.firstName = p.getFirstName();
        this.lastName = p.getLastName();
        this.email = p.getEmail();
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

    public static void main(String[] args) {
        Student st1 = new Student("FN2", "LN2", "STD001", null);
        Student st2 = new Student("FN1", "LN2", "STD002", null);

        List<Student> students = new ArrayList<>(List.of(st1, st2));
        Collections.sort(students);
        System.out.println(students);

    }

}