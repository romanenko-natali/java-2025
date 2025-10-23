package ua.university.repository;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Group;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GroupRepository Tests")
class GroupRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(GroupRepositoryTest.class);

    private GroupRepository groupRepository;
    private Group groupCS21_2023;
    private Group groupCS22_2023;
    private Group groupIT21_2022;
    private Group groupIT22_2024;
    private Group groupSE21_2023;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        groupRepository = new GroupRepository();

        groupCS21_2023 = new Group(21, "Computer Science", 2023);
        groupCS22_2023 = new Group(22, "Computer Science", 2023);
        groupIT21_2022 = new Group(21, "Information Technology", 2022);
        groupIT22_2024 = new Group(22, "Information Technology", 2024);
        groupSE21_2023 = new Group(21, "Software Engineering", 2023);

        groupRepository.add(groupCS21_2023);
        groupRepository.add(groupCS22_2023);
        groupRepository.add(groupIT21_2022);
        groupRepository.add(groupIT22_2024);
        groupRepository.add(groupSE21_2023);

        logger.info("Test setup completed with {} groups", groupRepository.size());
    }

    @Test
    @DisplayName("findByNumber should find all groups with specified number")
    void testFindByNumber() {
        logger.info("Testing findByNumber");

        List<Group> results = groupRepository.findByNumber(21);

        assertThat(results)
                .as("Should find all groups with number 21")
                .hasSize(3);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(results)
                .as("All results should have number 21")
                .allMatch(group -> group.number() == 21);

        softly.assertAll();
        logger.info("findByNumber test completed");
    }

    @Test
    @DisplayName("findBySpecialty should find groups by partial name (case-insensitive)")
    void testFindBySpecialty() {
        logger.info("Testing findBySpecialty with case-insensitive partial match");

        List<Group> results = groupRepository.findBySpecialty("computer");

        assertThat(results)
                .as("Should find groups with 'Computer' in specialty")
                .hasSize(2);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(results)
                .as("All results should contain 'Computer' in specialty (case-insensitive)")
                .allMatch(group -> group.specialty().toLowerCase().contains("computer"));

        softly.assertAll();
        logger.info("findBySpecialty test completed");
    }

    @Test
    @DisplayName("findBySpecialty with uppercase should work (case-insensitive)")
    void testFindBySpecialtyUpperCase() {
        logger.info("Testing findBySpecialty with uppercase");

        List<Group> results = groupRepository.findBySpecialty("INFORMATION");

        assertThat(results)
                .as("Should find groups with 'Information' regardless of case")
                .hasSize(2)
                .extracting(Group::specialty)
                .allMatch(specialty -> specialty.toLowerCase().contains("information"));

        logger.info("findBySpecialty uppercase test completed");
    }

    @Test
    @DisplayName("findBySpecialty with partial match should work")
    void testFindBySpecialtyPartial() {
        logger.info("Testing findBySpecialty with partial match");

        List<Group> results = groupRepository.findBySpecialty("ware");

        assertThat(results)
                .as("Should find 'Software Engineering' by partial 'ware'")
                .hasSize(1);

        assertThat(results.get(0).specialty())
                .isEqualTo("Software Engineering");

        logger.info("findBySpecialty partial match test completed");
    }

    @Test
    @DisplayName("findBySpecialty with empty string should return empty list")
    void testFindBySpecialtyEmpty() {
        logger.info("Testing findBySpecialty with empty string");

        List<Group> results = groupRepository.findBySpecialty("");

        assertThat(results)
                .as("Should return empty list for empty search term")
                .isEmpty();

        logger.info("findBySpecialty empty test completed");
    }

    @Test
    @DisplayName("findByYearRange should find groups within year range")
    void testFindByYearRange() {
        logger.info("Testing findByYearRange");

        List<Group> results = groupRepository.findByYearRange(2022, 2023);

        assertThat(results)
                .as("Should find groups from 2022-2023")
                .hasSize(4);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(results)
                .as("All results should be within year range")
                .allMatch(group -> group.startYear() >= 2022 && group.startYear() <= 2023);

        softly.assertAll();
        logger.info("findByYearRange test completed");
    }

    @Test
    @DisplayName("findByYearRange with invalid range should return empty list")
    void testFindByYearRangeInvalid() {
        logger.info("Testing findByYearRange with invalid range");

        List<Group> results = groupRepository.findByYearRange(2024, 2022);

        assertThat(results)
                .as("Should return empty list for invalid range")
                .isEmpty();

        logger.info("findByYearRange invalid test completed");
    }


    @Test
    @DisplayName("sortBySpecialtyAndNumber should sort correctly")
    void testSortBySpecialtyAndNumber() {
        logger.info("Testing sortBySpecialtyAndNumber");

        List<Group> sorted = groupRepository.sortBySpecialtyAndNumber();

        assertThat(sorted)
                .as("Should return all groups")
                .hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // Computer Science < Information Technology < Software Engineering
        softly.assertThat(sorted.get(0).specialty())
                .as("First should be Computer Science")
                .isEqualTo("Computer Science");

        softly.assertThat(sorted.get(1).specialty())
                .as("Second should be Computer Science")
                .isEqualTo("Computer Science");

        // Within Computer Science: number 21 < 22
        softly.assertThat(sorted.get(0).number())
                .as("First CS should be number 21")
                .isEqualTo(21);

        softly.assertThat(sorted.get(1).number())
                .as("Second CS should be number 22")
                .isEqualTo(22);

        softly.assertAll();
        logger.info("sortBySpecialtyAndNumber test completed");
    }

    @Test
    @DisplayName("sortByStartYearDesc should sort by year descending")
    void testSortByStartYearDesc() {
        logger.info("Testing sortByStartYearDesc");

        List<Group> sorted = groupRepository.sortByStartYearDesc();

        assertThat(sorted)
                .as("Should return all groups")
                .hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // 2024 > 2023 > 2022
        softly.assertThat(sorted.get(0).startYear())
                .as("First should be from 2024")
                .isEqualTo(2024);

        softly.assertThat(sorted.get(4).startYear())
                .as("Last should be from 2022")
                .isEqualTo(2022);

        // Check descending order
        for (int i = 0; i < sorted.size() - 1; i++) {
            int current = sorted.get(i).startYear();
            int next = sorted.get(i + 1).startYear();
            softly.assertThat(current)
                    .as("Year at index %d should be >= year at index %d", i, i + 1)
                    .isGreaterThanOrEqualTo(next);
        }

        softly.assertAll();
        logger.info("sortByStartYearDesc test completed");
    }

    @Test
    @DisplayName("Sorting should not modify the original repository")
    void testSortBySpecialtyDoesNotModifyRepository() {
        logger.info("Testing that sortBySpecialtyAndNumber does not modify repository");

        List<Group> originalOrder = groupRepository.getAll();
        List<Group> sorted = groupRepository.sortBySpecialtyAndNumber();
        List<Group> currentOrder = groupRepository.getAll();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currentOrder)
                .as("Repository order should remain unchanged")
                .isEqualTo(originalOrder);

        softly.assertThat(sorted)
                .as("Sorted list should be a different instance")
                .isNotSameAs(currentOrder);

        softly.assertAll();
        logger.info("sortBySpecialtyAndNumber does not modify repository - test completed");
    }

    @Test
    @DisplayName("sortByStartYearDesc should not modify the original repository")
    void testSortByStartYearDescDoesNotModifyRepository() {
        logger.info("Testing that sortByStartYearDesc does not modify repository");

        List<Group> originalOrder = groupRepository.getAll();
        List<Group> sorted = groupRepository.sortByStartYearDesc();
        List<Group> currentOrder = groupRepository.getAll();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currentOrder)
                .as("Repository order should remain unchanged")
                .isEqualTo(originalOrder);

        softly.assertThat(sorted)
                .as("Sorted list should be a different instance")
                .isNotSameAs(currentOrder);

        softly.assertAll();
        logger.info("sortByStartYearDesc does not modify repository - test completed");
    }

    @Test
    @DisplayName("groupBySpecialty should group correctly")
    void testGroupBySpecialty() {
        logger.info("Testing groupBySpecialty");

        Map<String, List<Group>> grouped = groupRepository.groupBySpecialty();

        assertThat(grouped)
                .as("Should have 3 specialties")
                .hasSize(3);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(grouped.get("Computer Science"))
                .as("Computer Science should have 2 groups")
                .hasSize(2);

        softly.assertThat(grouped.get("Information Technology"))
                .as("Information Technology should have 2 groups")
                .hasSize(2);

        softly.assertThat(grouped.get("Software Engineering"))
                .as("Software Engineering should have 1 group")
                .hasSize(1);

        softly.assertAll();
        logger.info("groupBySpecialty test completed");
    }

    @Test
    @DisplayName("countBySpecialty should count correctly")
    void testCountBySpecialty() {
        logger.info("Testing countBySpecialty");

        Map<String, Long> counts = groupRepository.countBySpecialty();

        assertThat(counts)
                .as("Should have counts for 3 specialties")
                .hasSize(3);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(counts.get("Computer Science"))
                .as("Computer Science should have 2 groups")
                .isEqualTo(2L);

        softly.assertThat(counts.get("Information Technology"))
                .as("Information Technology should have 2 groups")
                .isEqualTo(2L);

        softly.assertThat(counts.get("Software Engineering"))
                .as("Software Engineering should have 1 group")
                .isEqualTo(1L);

        softly.assertAll();
        logger.info("countBySpecialty test completed");
    }


    @Test
    @DisplayName("Empty repository operations should work correctly")
    void testEmptyRepository() {
        logger.info("Testing operations on empty repository");

        GroupRepository emptyRepo = new GroupRepository();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(emptyRepo.findByNumber(21))
                .as("findByNumber on empty repository")
                .isEmpty();

        softly.assertThat(emptyRepo.findBySpecialty("Computer"))
                .as("findBySpecialty on empty repository")
                .isEmpty();

        softly.assertThat(emptyRepo.sortBySpecialtyAndNumber())
                .as("sortBySpecialtyAndNumber on empty repository")
                .isEmpty();

        softly.assertThat(emptyRepo.groupBySpecialty())
                .as("groupBySpecialty on empty repository")
                .isEmpty();

        softly.assertAll();
        logger.info("Empty repository test completed");
    }

    @Test
    @DisplayName("Single group operations should work correctly")
    void testSingleGroup() {
        logger.info("Testing operations with single group");

        GroupRepository singleRepo = new GroupRepository();
        Group group = new Group(21, "Test Specialty", 2023);
        singleRepo.add(group);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(singleRepo.findByNumber(21))
                .as("findByNumber with single group")
                .hasSize(1)
                .containsExactly(group);

        softly.assertAll();
        logger.info("Single group test completed");
    }
}