package ua.university.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;
import ua.university.util.PersonUtils;
import ua.university.util.ValidationUtils;

import java.util.Comparator;
import java.util.Objects;

public class Person implements Comparable<Person> {

    private static final Logger logger = LoggerFactory.getLogger(Person.class);

    @NotBlank(message = "First name cannot be null or blank")
    @Pattern(
            regexp = "^[\\p{L}\\s\\-']{2,50}$",
            message = "First name must be 2-50 characters long and contain only letters, spaces, hyphens, or apostrophes"
    )
    private String firstName;

    @NotBlank(message = "Last name cannot be null or blank")
    @Pattern(
            regexp = "^[\\p{L}\\s\\-']{2,50}$",
            message = "Last name must be 2-50 characters long and contain only letters, spaces, hyphens, or apostrophes"
    )
    private String lastName;

    @NotBlank(message = "Email cannot be null or blank")
    @Email(message = "Email must be valid")
    private String email;

    public static final Comparator<Person> PERSON_COMPARATOR =
            Comparator.comparing(Person::getFirstName)
                    .thenComparing(Person::getLastName)
                    .thenComparing(Person::getEmail);

    @JsonCreator
    public Person(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email) {
        this.firstName = PersonUtils.capitalizeText(firstName);
        this.lastName = PersonUtils.capitalizeText(lastName);
        this.email = PersonUtils.formatEmail(email);

        logger.info("Created Person: {}", this);
    }

    public static Person createValidPerson(String firstName, String lastName, String email) {
        Person person = new Person(firstName, lastName, email);
        ValidationUtils.validate(person);
        return person;
    }

    protected String getFullName() {
        return PersonUtils.formatName(firstName, lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        String normalized = PersonUtils.capitalizeText(firstName != null ? firstName.trim() : null);
        String oldValue = this.firstName;
        this.firstName = normalized;

        try {
            ValidationUtils.validate(this);
            logger.debug("Set firstName='{}'", this.firstName);
        } catch (InvalidDataException e) {
            this.firstName = oldValue;
            throw e;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        String normalized = PersonUtils.capitalizeText(lastName != null ? lastName.trim() : null);
        String oldValue = this.lastName;
        this.lastName = normalized;

        try {
            ValidationUtils.validate(this);
            logger.debug("Set lastName='{}'", this.lastName);
        } catch (InvalidDataException e) {
            this.lastName = oldValue;
            throw e;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String normalized = PersonUtils.formatEmail(email != null ? email.trim() : null);
        String oldValue = this.email;
        this.email = normalized;

        try {
            ValidationUtils.validate(this);
            logger.debug("Set email='{}'", this.email);
        } catch (InvalidDataException e) {
            this.email = oldValue;
            throw e;
        }
    }

    public static Person createPerson(String firstName, String lastName) {
        String email = PersonUtils.generateEmailFromNames(firstName, lastName);
        Person person = new Person(firstName, lastName, email);
        ValidationUtils.validate(person);
        logger.info("Factory method: created Person with generated email '{}'", email);
        return person;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email);
    }

    @Override
    public int compareTo(Person other) {
        return PERSON_COMPARATOR.compare(this, other);
    }
}