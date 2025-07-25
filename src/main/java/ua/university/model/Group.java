package ua.university.model;

import ua.university.util.GroupUtils;

import java.time.LocalDate;
import java.util.Objects;

public class Group {
    private int number;
    private String specialty;
    private int startYear;

    public Group() {
    }

    public Group(int number, String specialty, int startYear) {
        setNumber(number);
        setSpecialty(specialty);
        setStartYear(startYear);
    }

    public Group(int number, String specialty) {
        this(number, specialty, LocalDate.now().getYear());
    }

    public int getGNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        if (GroupUtils.isValidSpecialty(specialty)) {
            this.specialty = specialty;
        }
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        if (GroupUtils.isValidStartYear(startYear)) {
            this.startYear = startYear;
        }
    }

    public int getCurrentCourse() {
        return LocalDate.now().getYear() - startYear + 1;
    }

    public static Group createGroup(int number, String specialty, int startYear) {
        if (GroupUtils.isValidSpecialty(specialty) && GroupUtils.isValidStartYear(startYear)) {
            return new Group(number, specialty, startYear);
        }
        return null;
    }

    public static Group createGroup(int number, String specialty){
        return createGroup(number, specialty, LocalDate.now().getYear());
    }

    public String groupInfo() {
        return GroupUtils.formatGroupFullNumber(this);
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupNumber=" + number +
                ", specialty='" + specialty + '\'' +
                ", startYear=" + startYear +
                ", currentCourse=" + getCurrentCourse() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return startYear == group.startYear &&
                Objects.equals(number, group.number) &&
                Objects.equals(specialty, group.specialty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, specialty, startYear);
    }
}