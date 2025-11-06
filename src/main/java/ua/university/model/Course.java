package ua.university.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;
import ua.university.util.PersonUtils;

import java.util.Comparator;
import java.util.Objects;

public class Course implements Comparable<Course> {

    private static final Logger logger = LoggerFactory.getLogger(Course.class);

    private final Subject subject;
    private final Teacher teacher;
    private final Group group;

    private static final Comparator<Course> COURSE_COMPARATOR =
            Comparator.comparing((Course c) -> c.getSubject().name())
                    .thenComparing(c -> c.getTeacher().getFullName())
                    .thenComparing(c -> c.getGroup().getFullName());

    public Course(Subject subject, Teacher teacher, Group group) {
        this.subject = Objects.requireNonNull(subject, "Subject must not be null");
        this.teacher = Objects.requireNonNull(teacher, "Teacher must not be null");
        this.group = Objects.requireNonNull(group, "Group must not be null");

        logger.debug("Created Course: {}", getIdentity());
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Group getGroup() {
        return group;
    }

    public static Course createCourse(Subject subject, Teacher teacher, Group group) {
        if (subject == null || teacher == null || group == null) {
            logger.error("Failed to create course: subject={}, teacher={}, group={}",
                    subject, teacher, group);
            throw new InvalidDataException("None of the objects (subject, teacher, or group) should be null.");
        }
        logger.info("Course created successfully: subject={}, teacher={}, group={}",
                subject, teacher, group);
        return new Course(subject, teacher, group);
    }

    public String getCourseInfo() {
        String info = String.format("Course: %s | Teacher: %s | Group: %s | Credits: %d",
                subject.name(),
                PersonUtils.formatName(teacher.getFirstName(), teacher.getLastName()),
                group.getFullName(),
                subject.credits());

        logger.debug("Retrieved course info: {}", info);
        return info;
    }

    public String getIdentity() {
        return subject.name() + "-" +
                PersonUtils.formatName(teacher.getFirstName(), teacher.getLastName()) + "-" +
                group.getFullName();
    }

    @Override
    public String toString() {
        return "Course{" +
                "subject=" + subject.name() +
                ", teacher=" + teacher.getFullName() +
                ", group=" + group.getFullName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return subject.equals(course.subject) &&
                teacher.equals(course.teacher) &&
                group.equals(course.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, teacher, group);
    }

    @Override
    public int compareTo(Course other) {
        return COURSE_COMPARATOR.compare(this, other);
    }
}
