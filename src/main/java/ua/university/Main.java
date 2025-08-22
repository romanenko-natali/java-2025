package ua.university;

import ua.university.model.*;
import ua.university.service.ReportGenerator;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        Teacher teacher1 = new Teacher("Іван", "Петренко", "math@univ.edu", "Математичний", "Доцент");
        Teacher teacher2 = new Teacher("Марія", "Сидоренко", "phys@univ.edu", "Фізичний", "Професор");

        Group cs101 = new Group(101, "Computer Science", 2023);

        Student student1 = new Student("Олексій", "Іванов", "alex@univ.edu", "ST001", cs101);
        Student student2 = new Student("Анна", "Коваль", "anna@univ.edu", "ST002", cs101);
        Student student3 = new Student("Ігор", "Лисенко", "igor@univ.edu", "ST003", cs101);

        Subject math = new Subject("Математика", 5);
        Subject physics = new Subject("Фізика", 4);

        StudentGrade[] grades = {
                new StudentGrade(student1, math, teacher1, Grade.A, 95, ExamType.EXAM, "Fall", LocalDate.of(2025, 6, 10)),
                new StudentGrade(student1, physics, teacher2, Grade.B, 85, ExamType.CREDIT, "Fall", LocalDate.of(2025, 6, 12)),
                new StudentGrade(student2, math, teacher1, Grade.C, 75, ExamType.EXAM, "Fall", LocalDate.of(2025, 6, 10)),
                new StudentGrade(student2, physics, teacher2, Grade.F, 40, ExamType.DIFFERENTIATED_CREDIT, "Fall", LocalDate.of(2025, 6, 12)),
                new StudentGrade(student3, math, teacher1, Grade.B, 88, ExamType.LABORATORY, "Fall", LocalDate.of(2025, 6, 11)),
                new StudentGrade(student3, physics, teacher2, Grade.D, 65, ExamType.COURSEWORK, "Fall", LocalDate.of(2025, 6, 13))
        };

        System.out.println("=== Індивідуальні звіти студентів ===");
        for (StudentGrade grade : grades) {
            System.out.println(ReportGenerator.formatExamResult(grade));
        }

        System.out.println("\n=== Повний звіт студентів ===");
        System.out.println(ReportGenerator.generateStudentReport(grades));

        System.out.println("\n=== Аналіз групи ===");
        System.out.println(ReportGenerator.analyzeGroupPerformance(grades));
    }
}

