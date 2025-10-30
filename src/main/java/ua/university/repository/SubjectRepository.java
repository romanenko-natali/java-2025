package ua.university.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Subject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubjectRepository extends GenericRepository<Subject> {
    private static final Logger logger = LoggerFactory.getLogger(SubjectRepository.class);
    public SubjectRepository() {
        super(Subject::name, "Subject");
    }

    /**
     * Sort subjects by credits in descending order
     */
    public List<Subject> sortByCreditsDesc() {
        List<Subject> allSubjects = getAll();
        allSubjects.sort(Comparator.comparing(Subject::credits).reversed());
        logger.info("Sorted " + entityType + " by credits in descending order");
        return allSubjects;
    }

    /**
     * Sort subjects by credits in ascending order (optional)
     */
    public List<Subject> sortByCreditsAsc() {
        List<Subject> allSubjects = getAll();
        allSubjects.sort(Comparator.comparing(Subject::credits));
        logger.info("Sorted " + "Subject" + " by credits in ascending order");
        return allSubjects;
    }

    /**
     * Find subjects by partial name match (case-insensitive).
     *
     * @param partialName partial name to search for
     * @return immutable list of subjects matching the partial name
     */
    public List<Subject> findByNameContaining(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty partial name");
            return List.of();
        }

        String searchTerm = partialName.trim().toLowerCase();
        List<Subject> results = getAll().stream()
                .filter(subject -> subject.name().toLowerCase().contains(searchTerm))
                .toList();

        logger.info("Found {} subjects containing '{}' in name", results.size(), partialName);
        return results;
    }

    /**
     * Find subjects within a credit range (inclusive).
     *
     * @param minCredits minimum credits (inclusive)
     * @param maxCredits maximum credits (inclusive)
     * @return immutable list of subjects within the credit range
     */
    public List<Subject> findByCreditsRange(int minCredits, int maxCredits) {
        if (minCredits > maxCredits) {
            logger.warn("Invalid credit range: min={} > max={}", minCredits, maxCredits);
            return List.of();
        }

        List<Subject> results = getAll().stream()
                .filter(subject -> subject.credits() >= minCredits && subject.credits() <= maxCredits)
                .toList();

        logger.info("Found {} subjects with credits between {} and {}", results.size(), minCredits, maxCredits);
        return results;
    }

    /**
     * Find subjects by difficulty level.
     *
     * @param difficultyLevel difficulty level ("Easy", "Medium", "Hard")
     * @return immutable list of subjects with the specified difficulty level
     */
    public List<Subject> findByDifficulty(String difficultyLevel) {
        if (difficultyLevel == null || difficultyLevel.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty difficulty level");
            return List.of();
        }

        List<Subject> results = getAll().stream()
                .filter(subject -> subject.getDifficultyLevel().equalsIgnoreCase(difficultyLevel.trim()))
                .toList();

        logger.info("Found {} subjects with difficulty level '{}'", results.size(), difficultyLevel);
        return results;
    }

    /**
     * Find subjects with credits greater than or equal to specified value.
     *
     * @param minCredits minimum credits
     * @return immutable list of subjects with credits >= minCredits
     */
    public List<Subject> findByMinCredits(int minCredits) {
        List<Subject> results = getAll().stream()
                .filter(subject -> subject.credits() >= minCredits)
                .toList();

        logger.info("Found {} subjects with credits >= {}", results.size(), minCredits);
        return results;
    }


    /**
     * Get total number of credits for all subjects.
     *
     * @return total credits
     */
    public int getTotalCredits() {
        int total = getAll().stream()
                .map(Subject::credits)
                .reduce(0, Integer::sum);

        logger.info("Total credits across all subjects: {}", total);
        return total;
    }

    /**
     * Get all subjects with maximum credits.
     * If multiple subjects have the same maximum credit value, all are returned.
     *
     * @return unmodifiable list of subjects with max credits (empty if no subjects)
     */
    public List<Subject> getAllSubjectsWithMaxCredits() {
        List<Subject> allSubjects = getAll();
        if (allSubjects.isEmpty()) {
            logger.info("No subjects found");
            return List.of();
        }

        int maxCredits = allSubjects.stream()
                .mapToInt(Subject::credits)
                .max()
                .orElse(0);

        List<Subject> results = allSubjects.stream()
                .filter(subject -> subject.credits() == maxCredits)
                .toList();

        logger.info("Found {} subject(s) with max credits: {} credits", results.size(), maxCredits);
        return results;
    }

    /**
     * Get one subject with maximum credits.
     * <p>
     * <strong>Note:</strong> If multiple subjects have the same maximum credit value,
     * one is returned arbitrarily (non-deterministic).
     * This method is primarily for demonstrating the reduce operation.
     * For production use, consider {@link #getAllSubjectsWithMaxCredits()} instead.
     *
     * @return Optional containing one subject with max credits, or empty if no subjects
     */
    public Optional<Subject> getSubjectWithMaxCredits() {
        Optional<Subject> result = getAll().stream()
                .reduce((s1, s2) -> s1.credits() > s2.credits() ? s1 : s2);
//        Optional<Subject> result = getAll().stream()
//                .max(Comparator.comparingInt(Subject::credits));

        logger.debug("getSubjectWithMaxCredits result: {}", result.orElse(null));

        return result;
    }

    /**
     * Group subjects by difficulty level.
     *
     * @return Map of difficulty level to list of subjects
     */
    public Map<String, List<Subject>> groupByDifficulty() {
        Map<String, List<Subject>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Subject::getDifficultyLevel));

        logger.info("Grouped subjects by difficulty: {} groups", grouped.size());
        grouped.forEach((difficulty, subjects) ->
                logger.debug("  {} - {} subjects", difficulty, subjects.size())
        );

        return grouped;
    }


    /**
     * Get all subject names in uppercase.
     *
     * @return list of subject names in uppercase
     */
    public List<String> getAllNamesUpperCase() {
        List<String> names = getAll().stream()
                .map(Subject::name)
                .map(String::toUpperCase)
                .toList();

        logger.info("Retrieved {} subject names in uppercase", names.size());
        return names;
    }


    /**
     * Count subjects by difficulty level.
     *
     * @return Map of difficulty level to count
     */
    public Map<String, Long> countByDifficulty() {
        Map<String, Long> counts = getAll().stream()
                .collect(Collectors.groupingBy(
                        Subject::getDifficultyLevel,
                        Collectors.counting()
                ));

        logger.info("Subject counts by difficulty: {}", counts);
        return counts;
    }

    /**
     * Check if any subject has the specified number of credits.
     *
     * @param credits credits to check
     * @return true if any subject has the specified credits
     */
    public boolean hasSubjectWithCredits(int credits) {
        boolean exists = getAll().stream()
                .anyMatch(subject -> subject.credits() == credits);

        logger.info("Subjects with {} credits exist: {}", credits, exists);
        return exists;
    }

    /**
     * Check if all subjects have at least the specified number of credits.
     *
     * @param minCredits minimum credits
     * @return true if all subjects have at least minCredits
     */
    public boolean allSubjectsHaveMinCredits(int minCredits) {
        boolean result = getAll().stream()
                .allMatch(subject -> subject.credits() >= minCredits);

        logger.info("All subjects have >= {} credits: {}", minCredits, result);
        return result;
    }
}
