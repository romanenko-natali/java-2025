package ua.university.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Course;
import ua.university.model.Group;
import ua.university.model.Teacher;

import java.util.List;

/**
 * Repository for managing Course objects with specialized search methods
 */
public class CourseRepository extends GenericRepository<Course> {

    private static final Logger logger = LoggerFactory.getLogger(CourseRepository.class);

    public CourseRepository() {
        super(Course::getIdentity, "Course");
    }

    /**
     * Find courses by subject name (exact match)
     */
    public List<Course> findBySubjectName(String subjectName) {
        if (subjectName == null) {
            logger.warn("Attempted to find courses with null subject name");
            return List.of();
        }

        List<Course> results = items.stream()
                .filter(course -> subjectName.equals(course.getSubject().name()))
                .toList();

        logger.info("Found {} course(s) with subject name: {}", results.size(), subjectName);
        return results;
    }

    /**
     * Find courses by teacher name (partial match on first name or last name, case-insensitive)
     */
    public List<Course> findByTeacher(String teacherNamePart) {
        if (teacherNamePart == null || teacherNamePart.isBlank()) {
            logger.warn("Attempted to find courses with null/blank teacher name");
            return List.of();
        }

        String searchTerm = teacherNamePart.trim().toLowerCase();
        List<Course> results = items.stream()
                .filter(course -> {
                    Teacher teacher = course.getTeacher();
                    return teacher.getFirstName().toLowerCase().contains(searchTerm) ||
                            teacher.getLastName().toLowerCase().contains(searchTerm);
                })
                .toList();

        logger.info("Found {} course(s) with teacher name containing: {}", results.size(), teacherNamePart);
        return results;
    }

    /**
     * Find courses by group full name (partial match, case-insensitive)
     * Searches in group's fullName (e.g., "CS01-23")
     */
    public List<Course> findByGroup(String groupNamePart) {
        if (groupNamePart == null || groupNamePart.isBlank()) {
            logger.warn("Attempted to find courses with null/blank group name");
            return List.of();
        }

        String searchTerm = groupNamePart.trim().toLowerCase();
        List<Course> results = items.stream()
                .filter(course -> course.getGroup().getFullName().toLowerCase().contains(searchTerm))
                .toList();

        logger.info("Found {} course(s) with group name containing: {}", results.size(), groupNamePart);
        return results;
    }

    /**
     * Find courses by teacher (exact match)
     */
    public List<Course> findByTeacher(Teacher teacher) {
        if (teacher == null) {
            logger.warn("Attempted to find courses with null teacher");
            return List.of();
        }

        List<Course> results = items.stream()
                .filter(course -> teacher.equals(course.getTeacher()))
                .toList();

        logger.info("Found {} course(s) for teacher: {} {}",
                results.size(), teacher.getFirstName(), teacher.getLastName());
        return results;
    }

    /**
     * Find courses by teacher names (partial match in first or last name, case-insensitive)
     */
    public List<Course> findByTeacherNames(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            logger.warn("Attempted to find courses with null or empty teacher name search term");
            return List.of();
        }

        String searchLower = searchTerm.toLowerCase().trim();

        List<Course> results = items.stream()
                .filter(course -> {
                    Teacher teacher = course.getTeacher();
                    String firstName = teacher.getFirstName().toLowerCase();
                    String lastName = teacher.getLastName().toLowerCase();
                    return firstName.contains(searchLower) || lastName.contains(searchLower);
                })
                .toList();

        logger.info("Found {} course(s) matching teacher name search term: '{}'",
                results.size(), searchTerm);
        return results;
    }

    /**
     * Find courses by group (exact match)
     */
    public List<Course> findByGroup(Group group) {
        if (group == null) {
            logger.warn("Attempted to find courses with null group");
            return List.of();
        }

        List<Course> results = items.stream()
                .filter(course -> group.equals(course.getGroup()))
                .toList();

        logger.info("Found {} course(s) for group: {}", results.size(), group.getFullName());
        return results;
    }

    /**
     * Find courses by group start year
     */
    public List<Course> findByGroupStartYear(int startYear) {
        List<Course> results = items.stream()
                .filter(course -> course.getGroup().startYear() == startYear)
                .toList();

        logger.info("Found {} course(s) for groups starting in year: {}", results.size(), startYear);
        return results;
    }

    /**
     * Find courses by partial group full name (case-insensitive substring match)
     * Searches in the full name generated by Group.getFullName() (e.g., "CS01-24")
     */
    public List<Course> findByPartFullName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            logger.warn("Attempted to find courses with null or empty partial group name");
            return List.of();
        }

        String searchLower = partialName.toLowerCase().trim();

        List<Course> results = items.stream()
                .filter(course -> {
                    try {
                        String fullName = course.getGroup().getFullName().toLowerCase();
                        return fullName.contains(searchLower);
                    } catch (Exception e) {
                        logger.warn("Error getting full name for group in course: {}", e.getMessage());
                        return false;
                    }
                })
                .toList();

        logger.info("Found {} course(s) matching partial group name: '{}'",
                results.size(), partialName);
        return results;
    }
}