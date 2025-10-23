package ua.university.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ua.university.model.Subject;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

class SubjectRepositoryTest {

    private SubjectRepository subjectRepository;

    @BeforeEach
    void setUp() {
        subjectRepository = new SubjectRepository();

        subjectRepository.add(new Subject("Math", 3));
        subjectRepository.add(new Subject("Physics", 5));
        subjectRepository.add(new Subject("History", 2));
    }

    @ParameterizedTest
    @CsvSource({
            "true, History, Math, Physics",
            "false, Physics, Math, History"
    })
    @DisplayName("Test sort by credits ascending/descending")
    void testSortByCredits(boolean asc, String first, String second, String third) {
        List<Subject> sorted = asc ? subjectRepository.sortByCreditsAsc()
                : subjectRepository.sortByCreditsDesc();

        assertSoftly(softly -> {
            softly.assertThat(sorted.get(0).name()).as("first element").isEqualTo(first);
            softly.assertThat(sorted.get(1).name()).as("second element").isEqualTo(second);
            softly.assertThat(sorted.get(2).name()).as("third element").isEqualTo(third);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "Math, 3",
            "Physics, 5",
            "History, 2"
    })
    @DisplayName("Test findByIdentity")
    void testFindByIdentity(String name, int credits) {
        subjectRepository.findByIdentity(name).ifPresentOrElse(
                subject -> assertAll(
                        () -> assertEquals(name, subject.name()),
                        () -> assertEquals(credits, subject.credits())
                ),
                () -> fail("Subject not found: " + name)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "Chemistry,4",
            "Biology,3"
    })
    @DisplayName("Test add new subjects")
    void testAdd(String name, int credits) {
        int oldCount = subjectRepository.getItemsForTesting().size();
        boolean added = subjectRepository.add(new Subject(name, credits));
        assertTrue(added, "Subject should be added");
        assertEquals(oldCount + 1, subjectRepository.getItemsForTesting().size(), "The count of items should increase by 1");
    }

    @ParameterizedTest
    @CsvSource({
            "Math",
            "History"
    })
    @DisplayName("Test removeByIdentity")
    void testRemoveByIdentity(String name) {
        int oldCount = subjectRepository.getItemsForTesting().size();
        boolean removed = subjectRepository.removeByIdentity(name);
        assertTrue(removed, "Should remove subject: " + name);
        assertEquals(oldCount - 1, subjectRepository.getItemsForTesting().size(), "The count of items should be decrease by 1");
    }
}

