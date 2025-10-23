package ua.university;

import ua.university.model.*;
import ua.university.repository.GenericRepository;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        demonstrateTeacherRepository();

//        demonstrateStudentRepository();
//
//        demonstrateSubjectRepository();
//
//        demonstrateGroupRepository();
//
//        demonstrateCourseRepository();

        System.out.println("DONE!!!");

    }

    private static void demonstrateTeacherRepository() {

        GenericRepository<Teacher> teacherRepo = new GenericRepository<>(
                Teacher::getEmail,
                "Teacher"
        );

        Teacher teacher1 = new Teacher("Ivan", "Petrenko", "ivan.petrenko@university.ua", "Applied Math", "Professor");

        Teacher teacher2 = new Teacher("Maria", "Kovalenko", "maria.kovalenko@university.ua", "Mathematics", "Associate Professor");

        Teacher teacher3 = new Teacher("Oleh", "Shevchenko", "ivan.petrenko@university.ua", "Chemistry", "Assistant Professor");

        System.out.println("1. Add teacher:");
        teacherRepo.add(teacher1);
        teacherRepo.add(teacher2);
        teacherRepo.add(teacher3);
        System.out.println("Was added: " + teacherRepo.size());

        System.out.println("\n2. Find by email:");
        Optional<Teacher> found = teacherRepo.findByIdentity("maria.kovalenko@university.ua");
        found.ifPresentOrElse(
                t -> System.out.println("Found: " + t),
                () -> System.out.println("Not found")
        );

        System.out.println("\n3. All:");
        teacherRepo.getAll().forEach(t ->
                System.out.println("  - " + t)
        );

        System.out.println("\n4. Delete:");
        teacherRepo.remove(teacher1);
        System.out.println("Teachers left: " + teacherRepo.size());
    }

    private static void demonstrateStudentRepository() {

        GenericRepository<Student> studentRepo = new GenericRepository<>(
                Student::getStudentId,
                "Student"
        );

        Group group = new Group(21, "Applied Math", 2023);

        Student student1 = new Student("Anna", "Koval", "anna.koval@student.ua", "AAA001", group);

        Student student2 = new Student("Petro", "Melnyk", "petro.melnyk@student.ua", "AAA002", group);

        Student student3 = new Student("Olha", "Bondarenko", "olga.bond@student.ua", "AAA001", group);

        System.out.println("1. Add students:");
        studentRepo.add(student1);
        studentRepo.add(student2);
        studentRepo.add(student3);

        System.out.println("\n2. Find student by ID:");
        Student foundStudent = studentRepo.findByIdentity("AAA002")
                .orElseThrow(() -> new RuntimeException("Student not found"));
        System.out.println("Found: " + foundStudent);

        System.out.println("\n3. Check if exists:");
        System.out.println("AAA001 exists? " + studentRepo.containsIdentity("AAA001"));
        System.out.println("AAA003 exists? " + studentRepo.containsIdentity("AAA003"));

        System.out.println("\n4. Remove by studentId:");
        studentRepo.removeByIdentity("AAA001");
        System.out.println("Students left: " + studentRepo.size());
    }


    private static void demonstrateSubjectRepository() {

        GenericRepository<Subject> subjectRepo = new GenericRepository<>(
                Subject::name,
                "Subject"
        );

        Subject subject1 = new Subject("Алгоритми та структури даних", 5);
        Subject subject2 = new Subject("Бази даних", 5);
        Subject subject3 = new Subject("Веб-технології", 4);
        Subject subject4 = new Subject("Алгоритми та структури даних", 5);

        System.out.println("1. Add subjects:");
        subjectRepo.add(subject1);
        subjectRepo.add(subject2);
        subjectRepo.add(subject3);
        subjectRepo.add(subject4); // Won't be added

        System.out.println("\n2. All subjects:");
        subjectRepo.getAll().forEach(s ->
                System.out.println("  - " + s.name() + " (" + s.credits() + " credits)")
        );

        System.out.println("\n3. Find subject:");
        subjectRepo.findByIdentity("Бази даних")
                .ifPresent(s -> System.out.println("Found: " + s.name() + ", credits: " + s.credits()));

        System.out.println("\n4. Clear repository:");
        subjectRepo.clear();
        System.out.println("Repository is empty? " + subjectRepo.isEmpty());
    }

    private static void demonstrateGroupRepository() {

        GenericRepository<Group> groupRepo = new GenericRepository<>(
                Group::getFullName,
                "Group"
        );

        Group group1 = new Group(21, "Комп'ютерні науки", 2023);
        Group group2 = new Group(22, "Інформаційні системи", 2023);
        Group group3 = new Group(21, "Програмна інженерія", 2022);
        Group group4 = new Group(21, "Комп'ютерні науки", 2023); // Duplicate

        System.out.println("1. Add groups:");
        groupRepo.add(group1);
        groupRepo.add(group2);
        groupRepo.add(group3);
        groupRepo.add(group4); // Won't be added - same full name

        System.out.println("\n2. All groups:");
        groupRepo.getAll().forEach(g ->
                System.out.println("  - " + g)
        );

        System.out.println("\n3. Find group by full name:");
        groupRepo.findByIdentity("КО21-23")
                .ifPresentOrElse(
                        g -> System.out.println("Found group: " + g),
                        () -> System.out.println("Group not found")
                );

        System.out.println("\n4. Repository size: " + groupRepo.size());
    }

    private static void demonstrateCourseRepository() {

        GenericRepository<Course> courseRepo = new GenericRepository<>(
                course -> course.getSubject().name() + "|" +
                        course.getTeacher().getEmail() + "|" +
                        course.getGroup().getFullName(),
                "Course"
        );

        Subject algorithms = new Subject("Алгоритми", 4);
        Subject databases = new Subject("Бази даних", 5);

        Teacher teacher1 = new Teacher("Ivan", "Petrenko", "ivan@university.ua", "CS", "Professor");

        Teacher teacher2 = new Teacher("Maria", "Kovalenko", "maria@university.ua", "IT", "Associate Professor");

        Group group1 = new Group(21, "Комп'ютерні науки", 2023);
        Group group2 = new Group(22, "Інформаційні системи", 2023);

        Course course1 = new Course(algorithms, teacher1, group1);
        Course course2 = new Course(databases, teacher2, group1);
        Course course3 = new Course(algorithms, teacher1, group2);
        Course course4 = new Course(algorithms, teacher1, group1); // Duplicate

        System.out.println("1. Add courses:");
        courseRepo.add(course1);
        courseRepo.add(course2);
        courseRepo.add(course3);
        courseRepo.add(course4); // Won't be added

        System.out.println("\n2. All courses:");
        courseRepo.getAll().forEach(c ->
                System.out.println("  - " + c.getSubject().name() +
                        " | Teacher: " + c.getTeacher().getFullName() +
                        " | Group: " + c.getGroup().getFullName())
        );

        System.out.println("\n3. Find course:");
        String searchKey = algorithms.name() + "|" + teacher1.getEmail() + "|" + group1.getFullName();
        courseRepo.findByIdentity(searchKey)
                .ifPresentOrElse(
                        c -> System.out.println("Found course: " + c.getSubject().name() +
                                " for group " + c.getGroup().getFullName()),
                        () -> System.out.println("Course not found")
                );

        System.out.println("\n4. Remove course:");
        courseRepo.remove(course1);
        System.out.println("Courses left: " + courseRepo.size());

        System.out.println("\n5. Check if exists:");
        System.out.println("course2 is in repository? " + courseRepo.contains(course2));
        System.out.println("course1 is in repository? " + courseRepo.contains(course1));
    }
}