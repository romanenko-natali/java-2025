package ua.university.model;

import ua.university.util.PersonUtils;

import java.util.Objects;

public class Course {
    private Subject subject;
    private Teacher teacher;
    private Group group;

    public Course() {
    }

    public Course(Subject subject, Teacher teacher, Group group) {
        this.subject = subject;
        this.teacher = teacher;
        this.group = group;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public static Course createCourse(Subject subject, Teacher teacher, Group group) {
        if (subject != null && teacher != null && group != null) {
            return new Course(subject, teacher, group);
        }
        return null;
    }

    public String getCourseInfo() {
        return String.format("Course: %s | Teacher: %s | Group: %s | Credits: %d",
                subject.getName(),
                PersonUtils.formatName(teacher.getFirstName(), teacher.getLastName()),
                group.groupInfo(),
                subject.getCredits());
    }

    @Override
    public String toString() {
        return "Course{" +
                "subject=" + subject +
                ", teacher=" + teacher +
                ", group=" + group +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(subject, course.subject) &&
                Objects.equals(teacher, course.teacher) &&
                Objects.equals(group, course.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, teacher, group);
    }
}
