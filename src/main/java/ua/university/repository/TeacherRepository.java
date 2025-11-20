package ua.university.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Teacher;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository for managing Teacher entities with custom sorting and filtering capabilities
 */
public class TeacherRepository extends GenericRepository<Teacher> {
    private static final Logger logger = LoggerFactory.getLogger(TeacherRepository.class);

    public TeacherRepository() {
        super(Teacher::getEmail, "Teacher");
    }

    /**
     * Sort teachers by last name, then by first name (ascending).
     *
     * @return new sorted list of teachers
     */
    public List<Teacher> sortByName() {
        List<Teacher> allTeachers = getAll();
        allTeachers.sort(
                Comparator.comparing(Teacher::getLastName)
                        .thenComparing(Teacher::getFirstName)
        );
        logger.info("Sorted {} by last name and first name in ascending order", entityType);
        return allTeachers;
    }

    /**
     * Sort teachers by department, then by position, then by name.
     *
     * @return new sorted list of teachers
     */
    public List<Teacher> sortByDepartment() {
        List<Teacher> allTeachers = getAll();
        allTeachers.sort(Teacher.TEACHER_COMPARATOR_BY_DEPARTMENT);
        logger.info("Sorted {} by department, position, and name", entityType);
        return allTeachers;
    }

    /**
     * Find teachers by department (case-insensitive, partial match).
     *
     * @param departmentPart partial department name
     * @return list of matching teachers
     */
    public List<Teacher> findByDepartment(String departmentPart) {
        if (departmentPart == null || departmentPart.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty department");
            return List.of();
        }

        String searchTerm = departmentPart.trim().toLowerCase();
        List<Teacher> results = getAll().stream()
                .filter(teacher -> teacher.getDepartment().toLowerCase().contains(searchTerm))
                .toList();

        logger.info("Found {} teachers in department containing '{}'", results.size(), departmentPart);
        return results;
    }

    /**
     * Find teachers by position (case-insensitive, exact match).
     *
     * @param position position to search for
     * @return list of matching teachers
     */
    public List<Teacher> findByPosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty position");
            return List.of();
        }

        List<Teacher> results = getAll().stream()
                .filter(teacher -> teacher.getPosition().equalsIgnoreCase(position.trim()))
                .toList();

        logger.info("Found {} teachers with position '{}'", results.size(), position);
        return results;
    }

    /**
     * Find teachers by name (partial match in first or last name, case-insensitive).
     *
     * @param namePart partial name to search
     * @return list of matching teachers
     */
    public List<Teacher> findByName(String namePart) {
        if (namePart == null || namePart.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty name");
            return List.of();
        }

        String searchTerm = namePart.trim().toLowerCase();
        List<Teacher> results = getAll().stream()
                .filter(teacher ->
                        teacher.getFirstName().toLowerCase().contains(searchTerm) ||
                                teacher.getLastName().toLowerCase().contains(searchTerm))
                .toList();

        logger.info("Found {} teachers with name containing '{}'", results.size(), namePart);
        return results;
    }

    /**
     * Group teachers by department.
     *
     * @return Map of department to list of teachers
     */
    public Map<String, List<Teacher>> groupByDepartment() {
        Map<String, List<Teacher>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Teacher::getDepartment));

        logger.info("Grouped teachers by department: {} departments", grouped.size());
        return grouped;
    }

    /**
     * Group teachers by position.
     *
     * @return Map of position to list of teachers
     */
    public Map<String, List<Teacher>> groupByPosition() {
        Map<String, List<Teacher>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Teacher::getPosition));

        logger.info("Grouped teachers by position: {} positions", grouped.size());
        return grouped;
    }

    /**
     * Count teachers by department.
     *
     * @return Map of department to count
     */
    public Map<String, Long> countByDepartment() {
        Map<String, Long> counts = getAll().stream()
                .collect(Collectors.groupingBy(
                        Teacher::getDepartment,
                        Collectors.counting()
                ));

        logger.info("Teacher counts by department: {}", counts);
        return counts;
    }

    /**
     * Count teachers by position.
     *
     * @return Map of position to count
     */
    public Map<String, Long> countByPosition() {
        Map<String, Long> counts = getAll().stream()
                .collect(Collectors.groupingBy(
                        Teacher::getPosition,
                        Collectors.counting()
                ));

        logger.info("Teacher counts by position: {}", counts);
        return counts;
    }
}