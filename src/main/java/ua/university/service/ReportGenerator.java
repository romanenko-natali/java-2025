package ua.university.service;

import ua.university.model.Student;
import ua.university.model.StudentGrade;

import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

    public static String formatExamResult(StudentGrade studentGrade) {
        return switch (studentGrade.examType()) {
            case EXAM -> "Іспит: " + studentGrade.grade() + " (" + studentGrade.points() + " балів)";
            case DIFFERENTIATED_CREDIT -> "Диф. залік: " + studentGrade.grade();
            case CREDIT -> studentGrade.grade().isPass() ? "Залік: зараховано" : "Залік: не зараховано";
            case COURSEWORK -> "Курсова робота: " + studentGrade.grade();
            case LABORATORY -> "Лабораторна: " + studentGrade.points() + " балів";
        };
    }

    public static String generateStudentReport(StudentGrade[] grades) {
        StringBuilder report = new StringBuilder();

        for (StudentGrade grade : grades) {
            String status = switch (grade.grade()) {
                case A -> "★ Відмінно";
                case B, C -> "✓ Добре";
                case D -> "○ Задовільно";
                case E -> "△ Достатньо";
                case F -> "✗ Незадовільно";
            };

            report.append(String.format(
                    "%s - %s: %s балів (%s)%n",
                    grade.subject().name(),
                    grade.teacher().getLastName(),
                    grade.points(),
                    status
            ));
        }

        return report.toString();
    }

    // Аналіз успішності групи
    public static String analyzeGroupPerformance(StudentGrade[] allGrades) {
        if (allGrades == null || allGrades.length == 0) {
            return "Немає даних для аналізу.";
        }

        double avgPoints = Arrays.stream(allGrades)
                .mapToInt(StudentGrade::points)
                .average()
                .orElse(0.0);

        long passed = Arrays.stream(allGrades)
                .filter(g -> g.grade().isPass())
                .count();

        long failed = allGrades.length - passed;

        Map<Student, Integer> studentTotal = new HashMap<>();
        for (StudentGrade g : allGrades) {
            studentTotal.merge(g.student(), g.points(), Integer::sum);
        }

        List<Map.Entry<Student, Integer>> top = studentTotal.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .toList();

        StringBuilder analysis = new StringBuilder();
        analysis.append("📊 Аналіз групи:\n")
                .append("Середній бал: ").append(String.format("%.2f", avgPoints)).append("\n")
                .append("Зараховано: ").append(passed).append("\n")
                .append("Не зараховано: ").append(failed).append("\n\n")
                .append("🏆 Топ студенти:\n");

        for (var entry : top) {
            analysis.append(entry.getKey().getFirstName())
                    .append(" ")
                    .append(entry.getKey().getLastName())
                    .append(" - ")
                    .append(entry.getValue())
                    .append(" балів\n");
        }

        return analysis.toString();
    }
}
