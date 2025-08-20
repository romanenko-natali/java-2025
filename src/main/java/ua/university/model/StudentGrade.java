package ua.university.model;

import java.time.LocalDate;

public record StudentGrade(
        Student student,
        Subject subject,
        Teacher teacher,
        Grade grade,
        int points,
        ExamType examType,
        String semester,
        LocalDate examDate
) {
}
