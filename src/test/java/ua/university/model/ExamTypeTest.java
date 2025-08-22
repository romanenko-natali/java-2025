package ua.university.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ua.university.exception.InvalidDataException;

import java.util.stream.Stream;

public class ExamTypeTest {


    @ParameterizedTest
    @DisplayName("Test correct ExamType creation and properties")
    @MethodSource("examTypeTestData")
    void testExamTypeProperties(ExamType examType, String expectedUkrainianName,
                                boolean expectedIsGraded, int expectedMaxDuration,
                                boolean expectedIsWrittenExam) {
        assertEquals(expectedUkrainianName, examType.getUkrainianName());
        assertEquals(expectedIsGraded, examType.isGraded());
        assertEquals(expectedMaxDuration, examType.getMaxDuration());
        assertEquals(expectedIsWrittenExam, examType.isWrittenExam());
    }

    static Stream<Arguments> examTypeTestData() {
        return Stream.of(
                Arguments.of(ExamType.EXAM, "Іспит", true, 180, true),
                Arguments.of(ExamType.CREDIT, "Залік", false, 60, false),
                Arguments.of(ExamType.DIFFERENTIATED_CREDIT, "Диференційований залік", true, 90, true),
                Arguments.of(ExamType.COURSEWORK, "Курсова робота", true, 240, false),
                Arguments.of(ExamType.LABORATORY, "Лабораторна робота", true, 120, false)
        );
    }

    @Test
    @DisplayName("Test successful ExamType parsing")
    void testValidExamTypeParsing() throws InvalidDataException {
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(ExamType.parseExamType("EXAM"))
                .as("Parsing uppercase 'EXAM'")
                .isEqualTo(ExamType.EXAM);

        softly.assertThat(ExamType.parseExamType("credit"))
                .as("Parsing lowercase 'credit'")
                .isEqualTo(ExamType.CREDIT);

        softly.assertThat(ExamType.parseExamType("  LABORATORY  "))
                .as("Parsing 'LABORATORY' with whitespace")
                .isEqualTo(ExamType.LABORATORY);

        softly.assertAll();
    }

    @ParameterizedTest
    @DisplayName("Test ExamType parsing with invalid data")
    @MethodSource("invalidExamTypeData")
    void testInvalidExamTypeParsing(String invalidValue, String expectedMessagePart, String testDescription) {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            ExamType.parseExamType(invalidValue);
        }, testDescription);

        assertThat(exception.getMessage())
                .as("Exception message for " + testDescription)
                .contains(expectedMessagePart);
    }

    static Stream<Arguments> invalidExamTypeData() {
        return Stream.of(
                Arguments.of(null, "ExamType value cannot be null", "parsing null value"),
                Arguments.of("INVALID_TYPE", "Invalid ExamType: 'INVALID_TYPE'", "parsing invalid enum value"),
                Arguments.of("", "Invalid ExamType", "parsing empty string"),
                Arguments.of("   ", "Invalid ExamType", "parsing whitespace only"),
                Arguments.of("exam_type", "Invalid ExamType", "parsing with underscore"),
                Arguments.of("123", "Invalid ExamType", "parsing numeric value")
        );
    }

    @ParameterizedTest
    @DisplayName("Test isWrittenExam logic")
    @MethodSource("writtenExamTestData")
    void testIsWrittenExamLogic(ExamType examType, boolean expectedIsWritten) {
        assertThat(examType.isWrittenExam())
                .as("Testing isWrittenExam for " + examType.name())
                .isEqualTo(expectedIsWritten);
    }

    static Stream<Arguments> writtenExamTestData() {
        return Stream.of(
                Arguments.of(ExamType.EXAM, true),
                Arguments.of(ExamType.DIFFERENTIATED_CREDIT, true),
                Arguments.of(ExamType.CREDIT, false),
                Arguments.of(ExamType.COURSEWORK, false),
                Arguments.of(ExamType.LABORATORY, false)
        );
    }

    @Test
    @DisplayName("Test enum constructor validation")
    void testEnumConstructorValidation() {
        SoftAssertions softly = new SoftAssertions();

        for (ExamType examType : ExamType.values()) {
            softly.assertThat(examType.getUkrainianName())
                    .as("Ukrainian name for " + examType.name() + " should not be null")
                    .isNotNull();

            softly.assertThat(examType.getUkrainianName().trim())
                    .as("Ukrainian name for " + examType.name() + " should not be empty")
                    .isNotEmpty();

            softly.assertThat(examType.getMaxDuration())
                    .as("Max duration for " + examType.name() + " should be positive")
                    .isPositive();
        }

        softly.assertAll();
    }
}
