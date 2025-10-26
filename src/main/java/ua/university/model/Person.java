package ua.university.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.util.PersonUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Person implements Comparable<Person> {

    private static final Logger logger = LoggerFactory.getLogger(Person.class);

    private String firstName;
    private String lastName;
    private String email;

    public static final Comparator<Person> PERSON_COMPARATOR =
            Comparator.comparing(Person::getFirstName)
                    .thenComparing(Person::getLastName)
                    .thenComparing(Person::getEmail);

    public Person(String firstName, String lastName, String email) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        logger.info("Created Person: {}", this);
    }

    protected String getFullName() {
        return PersonUtils.formatName(firstName, lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        PersonUtils.validateName(firstName);
        this.firstName = PersonUtils.capitalizeText(firstName);
        logger.debug("Set firstName='{}'", this.firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        PersonUtils.validateName(lastName);
        this.lastName = PersonUtils.capitalizeText(lastName);
        logger.debug("Set lastName='{}'", this.lastName);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = PersonUtils.formatEmail(email);
        PersonUtils.validateEmail(email);
        this.email = email;
        logger.debug("Set email='{}'", this.email);
    }

    public static Person createPerson(String firstName, String lastName) {
        PersonUtils.validateName(firstName);
        PersonUtils.validateName(lastName);

        String email = PersonUtils.generateEmailFromNames(firstName, lastName);
        logger.info("Factory method: created Person with generated email '{}'", email);
        return new Person(firstName, lastName, email);
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

class PersonByEmailComparator implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        return o2.getEmail().compareTo(o1.getEmail());
    }
}

