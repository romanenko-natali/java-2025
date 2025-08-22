package ua.university.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.assertj.core.api.SoftAssertions;
import static org.junit.jupiter.api.Assertions.*;

import ua.university.exception.InvalidDataException;
import ua.university.parser.SubjectFileParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class SubjectTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("subjects.csv");
    }

    @ParameterizedTest
    @CsvSource({
            "Mathematics, 5, Mathematics, 5",
            "Physics, 4, Physics, 4",
            "  Chemistry  , 3, Chemistry, 3",
            "Computer Science, 1, Computer Science, 1",
            "A, 2, A, 2",
            "123, 5, 123, 5"
    })
    void testValidSubjectCreation(String inputName, int inputCredits, String expectedName, int expectedCredits) {
        SoftAssertions softly = new SoftAssertions();

        assertDoesNotThrow(() -> {
            Subject subject = new Subject(inputName, inputCredits);
            softly.assertThat(subject.name()).isEqualTo(expectedName);
            softly.assertThat(subject.credits()).isEqualTo(expectedCredits);
        });

        softly.assertAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "Very long subject name that exceeds the maximum allowed length limit of one hundred characters which should fail validation"})
    void testInvalidSubjectNames(String invalidName) {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Subject(invalidName, 3);
        });
        assertTrue(exception.getMessage().contains("Invalid subject name"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6, 10, 100})
    void testInvalidSubjectCredits(int invalidCredits) {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Subject("Physics", invalidCredits);
        });
        assertTrue(exception.getMessage().contains("Invalid credit amount"));
    }

    @ParameterizedTest
    @MethodSource("provideDifficultyTestData")
    void testGetDifficultyLevel(String subjectName, int credits, String expectedDifficulty) {
        Subject subject = new Subject(subjectName, credits);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(subject.getDifficultyLevel()).isEqualTo(expectedDifficulty);
        softly.assertThat(subject.name()).isEqualTo(subjectName);
        softly.assertThat(subject.credits()).isEqualTo(credits);
        softly.assertAll();
    }

    private static Stream<Arguments> provideDifficultyTestData() {
        return Stream.of(
                Arguments.of("Basic Math", 1, "Easy"),
                Arguments.of("Elementary Science", 2, "Easy"),
                Arguments.of("Calculus", 3, "Medium"),
                Arguments.of("Advanced Math", 4, "Medium"),
                Arguments.of("Advanced Physics", 5, "Hard")
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidCSVTestData")
    void testParseValidCSVFile(String csvContent, int expectedSize, String firstSubjectName, int firstSubjectCredits) throws IOException, InvalidDataException {
        Files.writeString(testFile, csvContent);

        List<Subject> subjects = SubjectFileParser.parseFromCSV(testFile.toString());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(subjects).hasSize(expectedSize);
        if (expectedSize > 0) {
            softly.assertThat(subjects.get(0).name()).isEqualTo(firstSubjectName);
            softly.assertThat(subjects.get(0).credits()).isEqualTo(firstSubjectCredits);
        }
        softly.assertAll();
    }

    private static Stream<Arguments> provideValidCSVTestData() {
        return Stream.of(
                Arguments.of("Mathematics,5\nPhysics,4\nChemistry,3\nBiology,2", 4, "Mathematics", 5),
                Arguments.of("Computer Science,3", 1, "Computer Science", 3),
                Arguments.of("# Comment\nMath,2\n\nPhysics,4", 2, "Math", 2),
                Arguments.of("", 0, null, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCSVTestData")
    void testParseInvalidCSVFile(String csvContent, int expectedValidSubjects) throws IOException {
        Files.writeString(testFile, csvContent);

        assertDoesNotThrow(() -> {
            List<Subject> subjects = SubjectFileParser.parseFromCSV(testFile.toString());
            assertEquals(expectedValidSubjects, subjects.size());
        });
    }

    private static Stream<Arguments> provideInvalidCSVTestData() {
        return Stream.of(
                Arguments.of("Mathematics,5\nInvalid Line\nPhysics,10", 1),
                Arguments.of("Math,abc\nPhysics,4", 1),
                Arguments.of("OnlyOnePart\nPhysics,4", 1),
                Arguments.of("Too,Many,Parts,Here\nMath,3", 1)
        );
    }

    @Test
    void testFileNotFound() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            SubjectFileParser.parseFromCSV("nonexistent.csv");
        });
        assertTrue(exception.getMessage().contains("Error reading file"));
    }

}