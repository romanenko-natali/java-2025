package ua.university.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Group;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing Group entities with custom search and sorting capabilities
 */
public class GroupRepository extends GenericRepository<Group> {
    private static final Logger logger = LoggerFactory.getLogger(GroupRepository.class);

    public GroupRepository() {
        super(Group::getFullName, "Group");
    }

    /**
     * Find groups by group number.
     *
     * @param number group number to search for
     * @return unmodifiable list of groups with the specified number
     */
    public List<Group> findByNumber(int number) {
        List<Group> results = getAll().stream()
                .filter(group -> group.number() == number)
                .collect(Collectors.toList());

        logger.info("Found {} groups with number {}", results.size(), number);
        return results;
    }

    /**
     * Find groups by specialty (case-insensitive, partial match).
     * Uses Stream API filter operation.
     *
     * @param specialtyPart partial specialty name to search for (case-insensitive)
     * @return unmodifiable list of groups matching the specialty
     */
    public List<Group> findBySpecialty(String specialtyPart) {
        if (specialtyPart == null || specialtyPart.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty specialty");
            return List.of();
        }

        String searchTerm = specialtyPart.trim().toLowerCase();
        List<Group> results = getAll().stream()
                .filter(group -> group.specialty().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.info("Found {} groups with specialty containing '{}'", results.size(), specialtyPart);
        return results;
    }


    /**
     * Find groups within a year range.
     *
     * @param startYearFrom minimum start year (inclusive)
     * @param startYearTo maximum start year (inclusive)
     * @return unmodifiable list of groups within the year range
     */
    public List<Group> findByYearRange(int startYearFrom, int startYearTo) {
        if (startYearFrom > startYearTo) {
            logger.warn("Invalid year range: {} > {}", startYearFrom, startYearTo);
            return List.of();
        }

        List<Group> results = getAll().stream()
                .filter(group -> group.startYear() >= startYearFrom
                        && group.startYear() <= startYearTo)
                .collect(Collectors.toList());

        logger.info("Found {} groups with start year between {} and {}",
                results.size(), startYearFrom, startYearTo);
        return results;
    }

    /**
     * Find graduated groups (current year > 4).
     *
     * @return unmodifiable list of graduated groups
     */
    public List<Group> findGraduated() {
        List<Group> results = getAll().stream()
                .filter(Group::isGraduated)
                .collect(Collectors.toList());

        logger.info("Found {} graduated groups", results.size());
        return results;
    }

    /**
     * Find active (not graduated) groups.
     *
     * @return unmodifiable list of active groups
     */
    public List<Group> findActive() {
        List<Group> results = getAll().stream()
                .filter(group -> !group.isGraduated())
                .collect(Collectors.toList());

        logger.info("Found {} active groups", results.size());
        return results;
    }

    /**
     * Sort groups by specialty, then by number, then by start year (all ascending).
     * This method does not modify the repository - it returns a new sorted copy.
     *
     * @return new sorted list of groups
     */
    public List<Group> sortBySpecialtyAndNumber() {
        List<Group> allGroups = getAll();
        allGroups.sort(
                Comparator.comparing(Group::specialty)
                        .thenComparing(Group::number)
                        .thenComparing(Group::startYear)
        );
        logger.info("Sorted {} by specialty, number, and start year", "Group");
        return allGroups;
    }

    /**
     * Sort groups by start year in descending order (newest first).
     * This method does not modify the repository - it returns a new sorted copy.
     *
     * @return new sorted list of groups
     */
    public List<Group> sortByStartYearDesc() {
        List<Group> allGroups = getAll();
        allGroups.sort(Comparator.comparing(Group::startYear).reversed());
        logger.info("Sorted {} by start year in descending order", "Group");
        return allGroups;
    }


    /**
     * Group by specialty.
     *
     * @return Map of specialty to list of groups
     */
    public java.util.Map<String, List<Group>> groupBySpecialty() {
        java.util.Map<String, List<Group>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Group::specialty));

        logger.info("Grouped groups by specialty: {} specialties", grouped.size());
        return grouped;
    }

    /**
     * Group by start year.
     * Uses Stream API collect with groupingBy.
     *
     * @return Map of start year to list of groups
     */
    public java.util.Map<Integer, List<Group>> groupByStartYear() {
        java.util.Map<Integer, List<Group>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Group::startYear));

        logger.info("Grouped groups by start year: {} years", grouped.size());
        return grouped;
    }

    /**
     * Count groups by specialty.
     *
     * @return Map of specialty to count
     */
    public java.util.Map<String, Long> countBySpecialty() {
        java.util.Map<String, Long> counts = getAll().stream()
                .collect(Collectors.groupingBy(
                        Group::specialty,
                        Collectors.counting()
                ));

        logger.info("Group counts by specialty: {}", counts);
        return counts;
    }

}