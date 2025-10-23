package ua.university.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.util.PersonUtils;
import ua.university.util.TeacherUtils;

import java.util.*;

public class Teacher extends Person {

    private static final Logger logger = LoggerFactory.getLogger(Teacher.class);

    private String department;
    private String position;

    public Teacher() {
        super();
    }

    public Teacher(String firstName, String lastName, String email, String department, String position) {
        super(firstName, lastName, email);
        setDepartment(department);
        setPosition(position);
        logger.info("Created Teacher: {}", this);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        TeacherUtils.validateDepartment(department);
        this.department = department.trim();
        logger.debug("Set department='{}' for {}", this.department, getFullName());
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        TeacherUtils.validatePosition(position);
        this.position = position.trim();
        logger.debug("Set position='{}' for {}", this.position, getFullName());
    }

    @Override
    public String getFullName() {
        return super.getFullName() + " (" + position + ")";
    }

    public static Teacher createTeacher(String firstName, String lastName,
                                        String department, String position) {
        PersonUtils.validateName(firstName);
        PersonUtils.validateName(lastName);
        TeacherUtils.validateDepartment(department);
        TeacherUtils.validatePosition(position);

        String email = PersonUtils.generateEmailFromNames(firstName, lastName);
        logger.info("Factory method: created Teacher with email='{}'", email);
        return new Teacher(firstName, lastName, email, department, position);
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
        if (!(o instanceof Teacher teacher)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(department, teacher.department) &&
                Objects.equals(position, teacher.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), department, position);
    }
}
