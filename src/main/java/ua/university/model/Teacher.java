package ua.university.model;

import ua.university.util.PersonUtils;
import ua.university.util.TeacherUtils;
import java.util.Objects;

public class Teacher extends Person {
    private String department;
    private String position;

    public Teacher() {
        super();
    }

    public Teacher(String firstName, String lastName, String email, String department, String position) {
        super(firstName, lastName, email);
        setDepartment(department);
        setPosition(position);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (TeacherUtils.isValidDepartment(department)) {
            this.department = department;
        }
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (TeacherUtils.isValidPosition(position)) {
            this.position = position;
        }
    }

    @Override
    protected String getFullName() {
        return super.getFullName() + " (" + position + ")";
    }

    public static Teacher createTeacher(String firstName, String lastName,
                                                   String department, String position) {
        if (PersonUtils.isValidName(firstName) &&
                PersonUtils.isValidName(lastName) &&
                TeacherUtils.isValidDepartment(department) &&
                TeacherUtils.isValidPosition(position)) {

            String email = PersonUtils.generateEmailFromNames(firstName, lastName);
            return new Teacher(firstName, lastName, email, department, position);
        }
        return null;
    }


    @Override
    public String toString() {
        return "Teacher{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(department, teacher.department) &&
                Objects.equals(position, teacher.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), department, position);
    }
}
