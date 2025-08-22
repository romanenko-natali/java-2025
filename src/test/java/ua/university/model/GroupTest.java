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
import ua.university.parser.GroupFileParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class GroupTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("groups.csv");
    }

    @ParameterizedTest
    @CsvSource({
            "101, Computer Science, 2021, 25, 101, Computer Science, 2021",
            "202, Mathematics, 2020, 30, 202, Mathematics, 2020",
            "303, '  Physics  ', 2022, 15, 303, Physics, 2022"
    })
    void testValidGroupCreation(int inputNumber, String inputSpecialty, int inputStartYear, int inputStudentsCount,
                                int expectedNumber, String expectedSpecialty, int expectedStartYear) {
        SoftAssertions softly = new SoftAssertions();

        assertDoesNotThrow(() -> {
            Group group = new Group(inputNumber, inputSpecialty, inputStartYear);
            softly.assertThat(group.number()).isEqualTo(expectedNumber);
            softly.assertThat(group.specialty()).isEqualTo(expectedSpecialty);
            softly.assertThat(group.startYear()).isEqualTo(expectedStartYear);
        });

        softly.assertAll();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void testInvalidGroupNumber(int invalidNumber) {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Group(invalidNumber, "Computer Science", 2021);
        });
        assertTrue(exception.getMessage().contains("Group number must be positive"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "1234", "Spec@ialty", ""})
    void testInvalidSpecialty(String invalidSpecialty) {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Group(101, invalidSpecialty, 2021);
        });
        assertTrue(exception.getMessage().contains("Invalid specialty"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1800, 1899, 2030, 3000})
    void testInvalidStartYear(int invalidYear) {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Group(101, "Computer Science", invalidYear);
        });
        assertTrue(exception.getMessage().contains("Invalid start year"));
    }


    @ParameterizedTest
    @MethodSource("provideCurrentYearTestData")
    void testGetCurrentYear(int startYear, int expectedCurrentYear) {
        Group group = new Group(101, "Computer Science", startYear);

        assertEquals(expectedCurrentYear, group.getCurrentYear());
    }

    private static Stream<Arguments> provideCurrentYearTestData() {
        int currentYear = LocalDate.now().getYear();
        return Stream.of(
                Arguments.of(currentYear, 1),
                Arguments.of(currentYear - 1, 2),
                Arguments.of(currentYear - 2, 3),
                Arguments.of(currentYear - 3, 4),
                Arguments.of(currentYear - 4, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("provideFullNameTestData")
    void testGetFullName(int number, String specialty, int startYear, String expectedFullName) {
        Group group = new Group(number, specialty, startYear);

        assertEquals(expectedFullName, group.getFullName());
    }

    private static Stream<Arguments> provideFullNameTestData() {
        return Stream.of(
                Arguments.of(101, "Computer Science", 2021, "CO101-21"),
                Arguments.of(202, "Mathematics", 2020, "MA202-20"),
                Arguments.of(303, "Physics", 2022, "PH303-22"),
                Arguments.of(404, "chemistry", 2019, "CH404-19")
        );
    }

    @ParameterizedTest
    @MethodSource("provideGraduationTestData")
    void testIsGraduated(int startYear, boolean expectedGraduated) {
        Group group = new Group(101, "Computer Science", startYear);

        assertEquals(expectedGraduated, group.isGraduated());
    }

    private static Stream<Arguments> provideGraduationTestData() {
        int currentYear = LocalDate.now().getYear();
        return Stream.of(
                Arguments.of(currentYear - 5, true),    // 6th year - graduated
                Arguments.of(currentYear - 4, true),    // 5th year - graduated
                Arguments.of(currentYear - 3, false),   // 4th year - not graduated
                Arguments.of(currentYear - 2, false),   // 3rd year - not graduated
                Arguments.of(currentYear - 1, false),   // 2nd year - not graduated
                Arguments.of(currentYear, false)        // 1st year - not graduated
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidCSVTestData")
    void testParseValidCSVFile(String csvContent, int expectedSize, int firstGroupNumber, String firstSpecialty) throws IOException, InvalidDataException {
        Files.writeString(testFile, csvContent);

        List<Group> groups = GroupFileParser.parseFromCSV(testFile.toString());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(groups).hasSize(expectedSize);
        if (expectedSize > 0) {
            softly.assertThat(groups.get(0).number()).isEqualTo(firstGroupNumber);
            softly.assertThat(groups.get(0).specialty()).isEqualTo(firstSpecialty);
        }
        softly.assertAll();
    }

    private static Stream<Arguments> provideValidCSVTestData() {
        return Stream.of(
                Arguments.of("101,Computer Science,2021\n202,Mathematics,2020", 2, 101, "Computer Science"),
                Arguments.of("303,Physics,2022", 1, 303, "Physics"),
                Arguments.of("# Comment\n404,Chemistry,2019\n\n505,Biology,2021", 2, 404, "Chemistry"),
                Arguments.of("", 0, 0, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCSVTestData")
    void testParseInvalidCSVFile(String csvContent, int expectedValidGroups) throws IOException {
        Files.writeString(testFile, csvContent);

        assertDoesNotThrow(() -> {
            List<Group> groups = GroupFileParser.parseFromCSV(testFile.toString());
            assertEquals(expectedValidGroups, groups.size());
        });
    }

    private static Stream<Arguments> provideInvalidCSVTestData() {
        return Stream.of(
                Arguments.of("101,Computer Science,2021\nInvalid Line\n202,Mathematics,2020", 2),
                Arguments.of("abc,Computer Science,2021\n202,Mathematics,2020", 1),
                Arguments.of("101,Computer Science,abc\n202,Mathematics,2020", 1)
        );
    }

    @Test
    void testFileNotFound() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            GroupFileParser.parseFromCSV("nonexistent.csv");
        });
        assertTrue(exception.getMessage().contains("Error reading file"));
    }

    @Test
    void testGroupInfoMethod() {
        Group group = new Group(101, "Computer Science", 2021);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(group.groupInfo()).isEqualTo(group.getFullName());
        softly.assertThat(group.groupInfo()).isEqualTo("CO101-21");
        softly.assertAll();
    }
}