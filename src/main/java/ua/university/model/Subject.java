package ua.university.model;

import ua.university.util.SubjectUtils;

import java.util.Objects;

public class Subject {
    private String name;
    private int credits;

    public Subject() {
    }

    public Subject(String name, int credits) {
        setName(name);
        setCredits(credits);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (SubjectUtils.isValidName(name)) {
            this.name = name;
        }
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        if (SubjectUtils.isValidCredit(credits)) {
            this.credits = credits;
        }
    }

    public static Subject createSubject(String name, int credits) {
        if (SubjectUtils.isValidName(name) &&
                SubjectUtils.isValidCredit(credits)) {
            return new Subject(name, credits);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", credits=" + credits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return credits == subject.credits &&
                Objects.equals(name, subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, credits);
    }
}

