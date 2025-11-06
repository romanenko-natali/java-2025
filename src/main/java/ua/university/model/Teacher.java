package ua.university.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;
import ua.university.util.PersonUtils;
import ua.university.util.ValidationUtils;

import java.util.*;

public class Teacher extends Person {

    private static final Logger logger = LoggerFactory.getLogger(Teacher.class);

    @NotBlank(message = "Department cannot be null or blank")
    @Pattern(
            regexp = "^[a-zA-Z\\s\\-']{2,100}$",
            message = "Department must be 2-100 characters long and contain only letters, spaces, hyphens, or apostrophes"
    )
    protected String department;

    @NotBlank(message = "Position cannot be null or blank")
    @Pattern(
            regexp = "^[a-zA-Z\\s\\-']{2,50}$",
            message = "Position must be 2-50 characters long and contain only letters, spaces, hyphens, or apostrophes"
    )
    protected String position;

    public static final Comparator<Teacher> TEACHER_COMPARATOR_BY_DEPARTMENT =
            Comparator.comparing(Teacher::getDepartment)
                    .thenComparing(Teacher::getPosition)
                    .thenComparing(Person.PERSON_COMPARATOR);

    @JsonCreator
    public Teacher(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("department") String department,
            @JsonProperty("position") String position) {
        super(firstName, lastName, email);
        setDepartment(department);
        setPosition(position);
        logger.info("Created Teacher: {}", this);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department != null) {
            department = department.trim();
        }

        String oldValue = this.department;
        this.department = department;

        try {
            ValidationUtils.validate(this);
            logger.debug("Set department='{}' for {}", this.department, getFullName());
        } catch (InvalidDataException e) {
            this.department = oldValue;
            throw e;
        }
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (position != null) {
            position = position.trim();
        }

        String oldValue = this.position;
        this.position = position;

        try {
            ValidationUtils.validate(this);
            logger.debug("Set position='{}' for {}", this.position, getFullName());
        } catch (InvalidDataException e) {
            this.position = oldValue;
            throw e;
        }
    }

    @Override
    protected String getFullName() {
        return super.getFullName() + " (" + position + ")";
    }

    public static Teacher createTeacher(String firstName, String lastName,
                                        String department, String position) {
        String email = PersonUtils.generateEmailFromNames(firstName, lastName);
        Teacher teacher = new Teacher(firstName, lastName, email, department, position);
        ValidationUtils.validate(teacher);
        logger.info("Factory method: created Teacher with email='{}'", email);
        return teacher;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
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