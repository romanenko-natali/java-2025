package ua.university.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.assertj.core.api.SoftAssertions;
import ua.university.model.Group;
import ua.university.model.Student;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;

/**
 * Comprehensive unit tests for GenericRepository<Student> with parameterized tests and soft assertions
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Student Repository Tests")
public class StudentRepositoryTest {

    private GenericRepository<Student> studentRepository;
    private Group testGroup1, testGroup2;
    private Student testStudent1, testStudent2, testStudent3;

    @BeforeAll
    void setUpTestData() {
        testGroup1 = new Group(101, "CS", LocalDate.now().getYear());
        testGroup2 = new Group(102, "Math", LocalDate.now().getYear());

        testStudent1 = new Student("John", "john@email.com", "Doe", "STD001", testGroup1);
        testStudent2 = new Student("Jane", "jane@email.com", "Smith", "STD002", testGroup1);
        testStudent3 = new Student("Bob", "bob@email.com", "Johnson", "STD003", testGroup2);
    }

    @BeforeEach
    void setUp() {
        studentRepository = new GenericRepository<>(Student::getStudentId, "Student");
        studentRepository.getItemsForTesting().add(testStudent1);
    }


    /**
     * Provides student IDs for search operations
     */
    static Stream<Arguments> studentIdsProvider() {
        return Stream.of(
                Arguments.of("STD001", true, "Valid student ID"),
                Arguments.of("STD002", true, "Another valid student ID"),
                Arguments.of("ST999", false, "Non-existent student ID"),
                Arguments.of("INVALID", false, "Invalid format student ID"),
                Arguments.of("", false, "Empty string ID"),
                Arguments.of(null, false, "Null ID")
        );
    }

    /**
     * Provides invalid data for edge case testing
     */
    static Stream<Arguments> invalidDataProvider() {
        return Stream.of(
                Arguments.of("STD001", "John", "Doe", "CS"),
                Arguments.of(null, "null student object"),
                Arguments.of("", "empty string"),
                Arguments.of("   ", "whitespace string")
        );
    }



    @DisplayName("Test adding valid students")
    @Test
    void testAddValidStudent() {
        SoftAssertions softly = new SoftAssertions();

        int initialSize = studentRepository.size();

        // Test adding student
        boolean added = studentRepository.add(testStudent2);

        softly.assertThat(added)
                .as("Should successfully add student %s", testStudent2.getStudentId())
                .isTrue();

        softly.assertThat(studentRepository.size())
                .as("Repository size should increase by 1")
                .isEqualTo(initialSize + 1);

        softly.assertAll();
    }


    @DisplayName("Test getting existing student testStudent1")
    @Test
    void testFoundStudent() {
        SoftAssertions softly = new SoftAssertions();

        String expectedId = testStudent1.getStudentId();

        Optional<Student> found = studentRepository.findByIdentity(expectedId);
        softly.assertThat(found.isPresent())
                .as("Should find added student by ID %s", expectedId)
                .isTrue();

        if (found.isPresent()) {
            Student foundStudent = found.get();
            softly.assertThat(foundStudent.getFirstName())
                    .as("Student first name should match")
                    .isEqualTo(testStudent1.getFirstName());

            softly.assertThat(foundStudent.getLastName())
                    .as("Student last name should match")
                    .isEqualTo(testStudent1.getLastName());

            softly.assertThat(foundStudent.getGroup().getFullName())
                    .as("Student group should match")
                    .isEqualTo(testStudent1.getGroup().getFullName());
        }

        softly.assertAll();
    }

    @Test
    @DisplayName("Test duplicate prevention")
    void testDuplicatePrevention() {
        SoftAssertions softly = new SoftAssertions();

        // Try to add different student with same ID
        Student duplicateIdStudent = new Student("Different", "different@email.com", "Name", testStudent1.getStudentId(),  testGroup2);
        boolean duplicateIdAdd = studentRepository.add(duplicateIdStudent);
        softly.assertThat(duplicateIdAdd)
                .as("Adding student with duplicate ID should fail")
                .isFalse();

        softly.assertThat(studentRepository.size())
                .as("Repository should contain only one student")
                .isEqualTo(1);

        softly.assertAll();
    }

    @Test
    @DisplayName("Test null adding prevention")
    void testNullPrevention() {
        SoftAssertions softly = new SoftAssertions();

        boolean added = studentRepository.add(null);
        softly.assertThat(added)
                .as("Second add of same student should fail")
                .isFalse();
    }

    @ParameterizedTest(name = "Find by ID: {0} (should find: {1}) - {2}")
    @MethodSource("studentIdsProvider")
    @DisplayName("Test finding students by ID")
    void testFindByIdentity(String studentId, boolean shouldFind, String description) {
        SoftAssertions softly = new SoftAssertions();

        studentRepository.getItemsForTesting().add(testStudent2);

        Optional<Student> result = studentRepository.findByIdentity(studentId);

        softly.assertThat(result.isPresent())
                .as("Find result for %s should be %s", description, shouldFind ? "present" : "absent")
                .isEqualTo(shouldFind);

        if (shouldFind && result.isPresent()) {
            softly.assertThat(result.get().getStudentId())
                    .as("Found student should have correct ID")
                    .isEqualTo(studentId);
        }

        softly.assertAll();
    }

    @Test
    @DisplayName("Test getAll operation")
    void testGetAllStudents() {
        SoftAssertions softly = new SoftAssertions();

        GenericRepository<Student> emptyRepository = new GenericRepository<>(Student::getStudentId, "Student");
        List<Student> emptyList = emptyRepository.getAll();
        softly.assertThat(emptyList)
                .as("Initially should return empty list")
                .isEmpty();


        studentRepository.getItemsForTesting().add(testStudent2);
        studentRepository.getItemsForTesting().add(testStudent3);

        List<Student> allStudents = studentRepository.getAll();

        softly.assertThat(allStudents)
                .as("Should return all added students")
                .hasSize(3)
                .contains(testStudent1, testStudent2, testStudent3);

        allStudents.clear();

        softly.assertThat(studentRepository.size())
                .as("Repository size should not be affected by external list modification")
                .isEqualTo(3);

        softly.assertAll();
    }


    @DisplayName("Test removing students by identity")
    void testRemoveByIdentity() {
        SoftAssertions softly = new SoftAssertions();
        int initialSize = studentRepository.size();

        // Remove by identity
        boolean removed = studentRepository.removeByIdentity(testStudent1.getStudentId());

        softly.assertThat(removed)
                .as("Should successfully remove student %s", testStudent1.getStudentId())
                .isTrue();

        softly.assertThat(studentRepository.size())
                .as("Repository size should decrease by 1")
                .isEqualTo(initialSize - 1);

        softly.assertAll();
    }

    @Test
    @DisplayName("Test removing non-existent student")
    void testRemoveNonExistentStudent() {
        SoftAssertions softly = new SoftAssertions();

        studentRepository.add(testStudent1);
        int initialSize = studentRepository.size();

        boolean removed = studentRepository.removeByIdentity("ST999");

        softly.assertThat(removed)
                .as("Should not remove non-existent student")
                .isFalse();

        softly.assertThat(studentRepository.size())
                .as("Repository size should remain unchanged")
                .isEqualTo(initialSize);

        softly.assertAll();
    }

    @Test
    @DisplayName("Test removing with null identity")
    void testRemoveNullIdentity() {
        SoftAssertions softly = new SoftAssertions();

        studentRepository.add(testStudent1);
        int initialSize = studentRepository.size();

        boolean removed = studentRepository.removeByIdentity(null);

        softly.assertThat(removed)
                .as("Should not remove with null identity")
                .isFalse();

        softly.assertThat(studentRepository.size())
                .as("Repository size should remain unchanged")
                .isEqualTo(initialSize);

        softly.assertAll();
    }

    @Test
    @DisplayName("Test clear operation")
    void testClearRepository() {
        SoftAssertions softly = new SoftAssertions();

        // Add students
        studentRepository.add(testStudent1);
        studentRepository.add(testStudent2);
        studentRepository.add(testStudent3);

        softly.assertThat(studentRepository.size())
                .as("Should have 3 students before clear")
                .isEqualTo(3);

        // Clear repository
        studentRepository.clear();

        softly.assertThat(studentRepository.size())
                .as("Repository size should be 0 after clear")
                .isEqualTo(0);

        softly.assertThat(studentRepository.isEmpty())
                .as("Repository should be empty after clear")
                .isTrue();

        softly.assertThat(studentRepository.getAll())
                .as("GetAll should return empty list after clear")
                .isEmpty();

        softly.assertAll();
    }


}