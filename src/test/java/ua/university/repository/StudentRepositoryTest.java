package ua.university.repository;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Group;
import ua.university.model.Student;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudentRepository Sorting Tests")
class StudentRepositorySortingTest {
    private static final Logger logger = LoggerFactory.getLogger(StudentRepositorySortingTest.class);

    private StudentRepository studentRepository;
    private Student studentAlice;
    private Student studentBob;
    private Student studentCharlie;
    private Student studentAliceB;
    private Student studentAliceA;
    private Group groupCS;
    private Group groupIT;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        studentRepository = new StudentRepository();

        groupCS = new Group(21, "Computer Science", 2023);
        groupIT = new Group(22, "Information Technology", 2023);

        // Different last names
        studentAlice = new Student("Alice", "Smith", "alice.smith@student.ua", "CSS001", groupCS);
        studentBob = new Student("Bob", "Johnson", "bob.johnson@student.ua", "CSS002", groupCS);
        studentCharlie = new Student("Charlie", "Brown", "charlie.brown@student.ua", "ITT001", groupIT);

        // Same last name, different first names
        studentAliceB = new Student("Alice", "Williams", "alice.williams@student.ua", "CSS003", groupCS);
        studentAliceA = new Student("Anna", "Williams", "anna.williams@student.ua", "CSS004", groupIT);

        studentRepository.add(studentBob);
        studentRepository.add(studentAlice);
        studentRepository.add(studentCharlie);
        studentRepository.add(studentAliceB);
        studentRepository.add(studentAliceA);

        logger.info("Test setup completed with {} students", studentRepository.size());
    }

    @Test
    @DisplayName("sortByName should sort by last name, then first name, then email")
    void testSortByName() {
        logger.info("Testing sortByName");

        List<Student> sorted = studentRepository.sortByName();

        assertThat(sorted)
                .as("Should return all students")
                .hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // Brown < Johnson < Smith < Williams (Alice) < Williams (Anna)
        softly.assertThat(sorted.get(0).getLastName())
                .as("First student should be Brown")
                .isEqualTo("Brown");

        softly.assertThat(sorted.get(1).getLastName())
                .as("Second student should be Johnson")
                .isEqualTo("Johnson");

        softly.assertThat(sorted.get(2).getLastName())
                .as("Third student should be Smith")
                .isEqualTo("Smith");

        // Both Williams, but Alice < Anna
        softly.assertThat(sorted.get(3).getLastName())
                .as("Fourth student should be Williams")
                .isEqualTo("Williams");
        softly.assertThat(sorted.get(3).getFirstName())
                .as("Fourth student first name should be Alice")
                .isEqualTo("Alice");

        softly.assertThat(sorted.get(4).getLastName())
                .as("Fifth student should be Williams")
                .isEqualTo("Williams");
        softly.assertThat(sorted.get(4).getFirstName())
                .as("Fifth student first name should be Anna")
                .isEqualTo("Anna");

        softly.assertAll();
        logger.info("sortByName test completed successfully");
    }

    @Test
    @DisplayName("sortByNameDesc should sort by last name descending, then first name, then email")
    void testSortByNameDesc() {
        logger.info("Testing sortByNameDesc");

        List<Student> sorted = studentRepository.sortByNameDesc();

        assertThat(sorted)
                .as("Should return all students")
                .hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // Williams > Smith > Johnson > Brown (descending last name)
        softly.assertThat(sorted.get(0).getLastName())
                .as("First student should be Williams")
                .isEqualTo("Williams");
        softly.assertThat(sorted.get(0).getFirstName())
                .as("First Williams should be Alice (comes before Anna)")
                .isEqualTo("Alice");

        softly.assertThat(sorted.get(1).getLastName())
                .as("Second student should be Williams")
                .isEqualTo("Williams");
        softly.assertThat(sorted.get(1).getFirstName())
                .as("Second Williams should be Anna")
                .isEqualTo("Anna");

        softly.assertThat(sorted.get(2).getLastName())
                .as("Third student should be Smith")
                .isEqualTo("Smith");

        softly.assertThat(sorted.get(3).getLastName())
                .as("Fourth student should be Johnson")
                .isEqualTo("Johnson");

        softly.assertThat(sorted.get(4).getLastName())
                .as("Fifth student should be Brown")
                .isEqualTo("Brown");

        softly.assertAll();
        logger.info("sortByNameDesc test completed successfully");
    }

    @Test
    @DisplayName("sortByGroup should sort by group, then last name, then first name")
    void testSortByGroup() {
        logger.info("Testing sortByGroup");

        List<Student> sorted = studentRepository.sortByGroup();

        assertThat(sorted)
                .as("Should return all students")
                .hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // Group CS comes before IT (alphabetically: КО21-23 < ІН22-23)
        softly.assertThat(sorted.get(0).getGroup().getFullName())
                .as("First three students should be from CS group")
                .isEqualTo(groupCS.getFullName());

        softly.assertThat(sorted.get(1).getGroup().getFullName())
                .as("Second student should be from CS group")
                .isEqualTo(groupCS.getFullName());

        softly.assertThat(sorted.get(2).getGroup().getFullName())
                .as("Third student should be from CS group")
                .isEqualTo(groupCS.getFullName());

        // Within CS group: Johnson < Smith < Williams
        softly.assertThat(sorted.get(0).getLastName())
                .as("First CS student should be Johnson")
                .isEqualTo("Johnson");

        softly.assertThat(sorted.get(1).getLastName())
                .as("Second CS student should be Smith")
                .isEqualTo("Smith");

        softly.assertThat(sorted.get(2).getLastName())
                .as("Third CS student should be Williams")
                .isEqualTo("Williams");

        // IT group students
        softly.assertThat(sorted.get(3).getGroup().getFullName())
                .as("Fourth student should be from IT group")
                .isEqualTo(groupIT.getFullName());

        softly.assertThat(sorted.get(4).getGroup().getFullName())
                .as("Fifth student should be from IT group")
                .isEqualTo(groupIT.getFullName());

        softly.assertAll();
        logger.info("sortByGroup test completed successfully");
    }

    @Test
    @DisplayName("sortByName should not modify the original repository")
    void testSortByNameDoesNotModifyRepository() {
        logger.info("Testing that sortByName does not modify repository");

        List<Student> originalOrder = studentRepository.getAll();
        List<Student> sorted = studentRepository.sortByName();
        List<Student> currentOrder = studentRepository.getAll();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currentOrder)
                .as("Repository order should remain unchanged")
                .isEqualTo(originalOrder);

        softly.assertThat(sorted)
                .as("Sorted list should be a different instance")
                .isNotSameAs(currentOrder);

        softly.assertAll();
    }

    @Test
    @DisplayName("sortByNameDesc should not modify the original repository")
    void testSortByNameDescDoesNotModifyRepository() {
        logger.info("Testing that sortByNameDesc does not modify repository");

        List<Student> originalOrder = studentRepository.getAll();
        List<Student> sorted = studentRepository.sortByNameDesc();
        List<Student> currentOrder = studentRepository.getAll();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currentOrder)
                .as("Repository order should remain unchanged")
                .isEqualTo(originalOrder);

        softly.assertThat(sorted)
                .as("Sorted list should be a different instance")
                .isNotSameAs(currentOrder);

        softly.assertAll();
    }

    @Test
    @DisplayName("sortByGroup should not modify the original repository")
    void testSortByGroupDoesNotModifyRepository() {
        logger.info("Testing that sortByGroup does not modify repository");

        List<Student> originalOrder = studentRepository.getAll();
        List<Student> sorted = studentRepository.sortByGroup();
        List<Student> currentOrder = studentRepository.getAll();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currentOrder)
                .as("Repository order should remain unchanged")
                .isEqualTo(originalOrder);

        softly.assertThat(sorted)
                .as("Sorted list should be a different instance")
                .isNotSameAs(currentOrder);

        softly.assertAll();
    }

    @Test
    @DisplayName("sortByName with students having same last and first name should sort by email")
    void testSortByNameWithIdenticalNames() {
        logger.info("Testing sortByName with identical names");

        StudentRepository repo = new StudentRepository();
        Group group = new Group(21, "Test", 2023);

        // Same last name and first name, different emails
        Student student1 = new Student("John", "Doe", "john.doe.c@student.ua", "IDD001", group);
        Student student2 = new Student("John", "Doe", "john.doe.a@student.ua", "IDD002", group);
        Student student3 = new Student("John", "Doe", "john.doe.b@student.ua", "IDD003", group);

        repo.add(student1);
        repo.add(student2);
        repo.add(student3);

        List<Student> sorted = repo.sortByName();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(sorted.get(0).getEmail())
                .as("First should be sorted by email: a")
                .isEqualTo("john.doe.a@student.ua");

        softly.assertThat(sorted.get(1).getEmail())
                .as("Second should be sorted by email: b")
                .isEqualTo("john.doe.b@student.ua");

        softly.assertThat(sorted.get(2).getEmail())
                .as("Third should be sorted by email: c")
                .isEqualTo("john.doe.c@student.ua");

        softly.assertAll();
        logger.info("sortByName with identical names test completed");
    }

    @Test
    @DisplayName("Sorting empty repository should return empty list")
    void testSortingEmptyRepository() {
        logger.info("Testing sorting on empty repository");

        StudentRepository emptyRepo = new StudentRepository();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(emptyRepo.sortByName())
                .as("sortByName on empty repository should return empty list")
                .isEmpty();

        softly.assertThat(emptyRepo.sortByNameDesc())
                .as("sortByNameDesc on empty repository should return empty list")
                .isEmpty();

        softly.assertThat(emptyRepo.sortByGroup())
                .as("sortByGroup on empty repository should return empty list")
                .isEmpty();

        softly.assertAll();
        logger.info("Empty repository sorting test completed");
    }

    @Test
    @DisplayName("Sorting single student should return list with that student")
    void testSortingSingleStudent() {
        logger.info("Testing sorting with single student");

        StudentRepository singleRepo = new StudentRepository();
        Group group = new Group(21, "Test", 2023);
        Student student = new Student("Alice", "Smith", "alice@student.ua", "IDD001", group);
        singleRepo.add(student);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(singleRepo.sortByName())
                .as("sortByName with single student")
                .hasSize(1)
                .containsExactly(student);

        softly.assertThat(singleRepo.sortByNameDesc())
                .as("sortByNameDesc with single student")
                .hasSize(1)
                .containsExactly(student);

        softly.assertThat(singleRepo.sortByGroup())
                .as("sortByGroup with single student")
                .hasSize(1)
                .containsExactly(student);

        softly.assertAll();
        logger.info("Single student sorting test completed");
    }
}