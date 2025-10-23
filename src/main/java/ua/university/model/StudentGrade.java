package ua.university.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record StudentGrade(
        Student student,
        Subject subject,
        Teacher teacher,
        Grade grade,
        int points,
        ExamType examType,
        String semester,
        LocalDate examDate
) implements Comparable<StudentGrade> {

    private static final Logger logger = LoggerFactory.getLogger(StudentGrade.class);

    public StudentGrade {
        List<String> errors = new ArrayList<>();

        if (student == null) errors.add("Student must not be null");
        if (subject == null) errors.add("Subject must not be null");
        if (teacher == null) errors.add("Teacher must not be null");
        if (grade == null) errors.add("Grade must not be null");
        if (points < 0 || points > 100) errors.add("Points must be between 0 and 100: " + points);
        if (examType == null) errors.add("Exam type must not be null");
        if (semester == null || semester.isBlank()) errors.add("Semester must not be null or empty");
        if (examDate == null) {
            errors.add("Exam date must not be null");
        } else if (examDate.isAfter(LocalDate.now())) {
            errors.add("Exam date must be today or in the past: " + examDate);
        }

        semester = (semester != null) ? semester.trim() : null;

        if (!errors.isEmpty()) {
            String errorMessage = String.join("; ", errors);
            logger.error("Validation errors while creating StudentGrade: {}", errorMessage);
            throw new InvalidDataException(errorMessage);
        }

        logger.info("Created StudentGrade: {} | {} | {} | {} points | {} | {} | {}",
                student.getFullName(), subject.name(), teacher.getFullName(),
                points, grade, examType, semester);
    }

    @Override
    public int compareTo(StudentGrade other) {
        int cmp = this.student.getFullName().compareTo(other.student.getFullName());
        if (cmp != 0) return cmp;

        cmp = this.subject.name().compareTo(other.subject.name());
        if (cmp != 0) return cmp;

        cmp = this.semester.compareTo(other.semester);
        if (cmp != 0) return cmp;

        return this.examDate.compareTo(other.examDate);
    }
}

//    public StudentGrade {
//        if (student == null) {
//            throw logAndThrow("Student must not be null");
//        }
//        if (subject == null) {
//            throw logAndThrow("Subject must not be null");
//        }
//        if (teacher == null) {
//            throw logAndThrow("Teacher must not be null");
//        }
//        if (grade == null) {
//            throw logAndThrow("Grade must not be null");
//        }
//        if (points < 0 || points > 100) {
//            throw logAndThrow("Points must be between 0 and 100: " + points);
//        }
//        if (examType == null) {
//            throw logAndThrow("Exam type must not be null");
//        }
//        if (semester == null || semester.isBlank()) {
//            throw logAndThrow("Semester must not be null or empty");
//        }
//        if (examDate == null) {
//            throw logAndThrow("Exam date must not be null");
//        }
//
//        semester = semester.trim();
//
//        logger.info("Created StudentGrade: {} | {} | {} | {} points | {} | {} | {}",
//                student.getFullName(), subject.name(), teacher.getFullName(),
//                points, grade, examType, semester);
//    }
//
//    private static InvalidDataException logAndThrow(String message) {
//        logger.error(message);
//        return new InvalidDataException(message);
//    }

