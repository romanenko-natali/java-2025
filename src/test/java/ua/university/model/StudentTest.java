package ua.university.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.university.exception.InvalidDataException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Student tests")
class StudentTest {

    private Group validGroup;

    @BeforeEach
    void setUp() {
        validGroup = Group.createValidGroup(101, "Computer Science", 2023);
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Create valid student with all parameters")
        void testCreateValidStudent() {
            Student student = new Student("John", "Doe", "john.doe@example.com",
                    "CS12345", validGroup);

            assertThat(student).isNotNull();
            assertThat(student.getFirstName()).isEqualTo("John");
            assertThat(student.getLastName()).isEqualTo("Doe");
            assertThat(student.getEmail()).isEqualTo("john.doe@example.com");
            assertThat(student.getStudentId()).isEqualTo("CS12345");
            assertThat(student.getGroup()).isEqualTo(validGroup);
        }

        @Test
        @DisplayName("Create student without email")
        void testCreateStudentWithoutEmail() {
            Student student = new Student("John", "Doe", "CSS234", validGroup);

            assertThat(student).isNotNull();
            assertThat(student.getFirstName()).isEqualTo("John");
            assertThat(student.getLastName()).isEqualTo("Doe");
            assertThat(student.getEmail()).matches("john\\.doe\\.\\w+@university\\.edu");
            assertThat(student.getStudentId()).isEqualTo("CSS234");
        }

        @Test
        @DisplayName("Constructor should capitalize names")
        void testConstructorCapitalizesNames() {
            Student student = new Student("john", "doe", "john@example.com",
                    "CS12345", validGroup);

            assertThat(student.getFirstName()).isEqualTo("John");
            assertThat(student.getLastName()).isEqualTo("Doe");
        }

        @Test
        @DisplayName("Constructor should trim whitespace")
        void testConstructorTrimsWhitespace() {
            Student student = Student.createStudent("  John  ", "  Doe  ",
                    "  CSS234  ", validGroup);

            assertThat(student.getFirstName()).isEqualTo("John");
            assertThat(student.getLastName()).isEqualTo("Doe");
            assertThat(student.getEmail()).isEqualTo("john.doe.css234@university.edu");
            assertThat(student.getStudentId()).isEqualTo("CSS234");
        }

        @Test
        @DisplayName("Constructor should format email to lowercase")
        void testConstructorFormatsEmail() {
            Student student = new Student("John", "Doe", "John.Doe@Example.COM",
                    "CS12345", validGroup);

            assertThat(student.getEmail()).isEqualTo("john.doe@example.com");
        }
    }

    @Nested
    @DisplayName("Factory method createStudent tests")
    class CreateStudentFactoryMethodTest {

        @Test
        @DisplayName("Create valid student using factory method")
        void testCreateValidStudent() {
            Student student = Student.createStudent("John", "Doe", "CSS234", validGroup);

            assertThat(student).isNotNull();
            assertThat(student.getFirstName()).isEqualTo("John");
            assertThat(student.getLastName()).isEqualTo("Doe");
            assertThat(student.getStudentId()).isEqualTo("CSS234");
            assertThat(student.getGroup()).isEqualTo(validGroup);
        }

        @Test
        @DisplayName("Factory method validates and throws for null first name")
        void testCreateStudentWithNullFirstName() {
            assertThatThrownBy(() -> Student.createStudent(null, "Doe", "CSS234", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Names");
        }

        @Test
        @DisplayName("Factory method validates and throws for blank first name")
        void testCreateStudentWithBlankFirstName() {
            assertThatThrownBy(() -> Student.createStudent("   ", "Doe", "CS12345", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Names");
        }

        @Test
        @DisplayName("Factory method validates and throws for null last name")
        void testCreateStudentWithNullLastName() {
            assertThatThrownBy(() -> Student.createStudent("John", null, "CSS234", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Names");
        }

        @Test
        @DisplayName("Factory method validates and throws for blank last name")
        void testCreateStudentWithBlankLastName() {
            assertThatThrownBy(() -> Student.createStudent("John", "   ", "CSS234", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Names");
        }

        @Test
        @DisplayName("Factory method validates and throws for invalid first name")
        void testCreateStudentWithInvalidFirstName() {
            assertThatThrownBy(() -> Student.createStudent("J0hn123", "Doe", "CSS234", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("First name");
        }

        @Test
        @DisplayName("Factory method validates and throws for invalid last name")
        void testCreateStudentWithInvalidLastName() {
            assertThatThrownBy(() -> Student.createStudent("John", "D0e!", "CSS234", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Last name");
        }

        @Test
        @DisplayName("Factory method validates and throws for null student ID")
        void testCreateStudentWithNullStudentId() {
            assertThatThrownBy(() -> Student.createStudent("John", "Doe", null, validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("studentID");
        }

        @Test
        @DisplayName("Factory method validates and throws for blank student ID")
        void testCreateStudentWithBlankStudentId() {
            assertThatThrownBy(() -> Student.createStudent("John", "Doe", "   ", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("studentID");
        }

        @Test
        @DisplayName("Factory method validates and throws for invalid student ID format")
        void testCreateStudentWithInvalidStudentIdFormat() {
            assertThatThrownBy(() -> Student.createStudent("John", "Doe", "INVALID", validGroup))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Student ID");
        }

        @Test
        @DisplayName("Factory method validates and throws for null group")
        void testCreateStudentWithNullGroup() {
            assertThatThrownBy(() -> Student.createStudent("John", "Doe", "CS12345", null))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Group");
        }

        @ParameterizedTest
        @ValueSource(strings = {"CSS234", "ITT876", "MAA111"})
        @DisplayName("Factory method accepts valid student IDs")
        void testCreateStudentWithValidStudentIds(String studentId) {
            Student student = Student.createStudent("John", "Doe", studentId, validGroup);

            assertThat(student).isNotNull();
            assertThat(student.getStudentId()).isEqualTo(studentId);
        }

        @ParameterizedTest
        @CsvSource({
                "john, doe, John, Doe",
                "JOHN, DOE, John, Doe",
                "jOhN, dOe, John, Doe",
                "mary-jane, o'connor, Mary-Jane, O'Connor"
        })
        @DisplayName("Factory method properly capitalizes names")
        void testCreateStudentNameCapitalization(String firstName, String lastName,
                                                 String expectedFirst, String expectedLast) {
            Student student = Student.createStudent(firstName, lastName, "CSS234", validGroup);

            assertThat(student.getFirstName()).isEqualTo(expectedFirst);
            assertThat(student.getLastName()).isEqualTo(expectedLast);
        }
    }

    @Nested
    @DisplayName("Setter validation tests")
    class SetterTests {

        private Student student;

        @BeforeEach
        void setUp() {
            student = new Student("John", "Doe", "john@example.com", "CSS234", validGroup);
        }

        @Test
        @DisplayName("Set valid first name")
        void testSetValidFirstName() {
            student.setFirstName("Jane");
            assertThat(student.getFirstName()).isEqualTo("Jane");
        }

        @Test
        @DisplayName("Set first name with capitalization")
        void testSetFirstNameWithCapitalization() {
            student.setFirstName("jane");
            assertThat(student.getFirstName()).isEqualTo("Jane");
        }

        @Test
        @DisplayName("Set invalid first name should throw and rollback")
        void testSetInvalidFirstName() {
            String originalName = student.getFirstName();

            assertThatThrownBy(() -> student.setFirstName("J0hn123"))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("First name");

            assertThat(student.getFirstName()).isEqualTo(originalName);
        }

        @Test
        @DisplayName("Set null first name should throw and rollback")
        void testSetNullFirstName() {
            String originalName = student.getFirstName();

            assertThatThrownBy(() -> student.setFirstName(null))
                    .isInstanceOf(InvalidDataException.class);

            assertThat(student.getFirstName()).isEqualTo(originalName);
        }

        @Test
        @DisplayName("Set blank first name should throw and rollback")
        void testSetBlankFirstName() {
            String originalName = student.getFirstName();

            assertThatThrownBy(() -> student.setFirstName("   "))
                    .isInstanceOf(InvalidDataException.class);

            assertThat(student.getFirstName()).isEqualTo(originalName);
        }

        @Test
        @DisplayName("Set valid last name")
        void testSetValidLastName() {
            student.setLastName("Smith");
            assertThat(student.getLastName()).isEqualTo("Smith");
        }

        @Test
        @DisplayName("Set invalid last name should throw and rollback")
        void testSetInvalidLastName() {
            String originalName = student.getLastName();

            assertThatThrownBy(() -> student.setLastName("Sm1th!"))
                    .isInstanceOf(InvalidDataException.class);

            assertThat(student.getLastName()).isEqualTo(originalName);
        }

        @Test
        @DisplayName("Set valid email")
        void testSetValidEmail() {
            student.setEmail("jane.smith@example.com");
            assertThat(student.getEmail()).isEqualTo("jane.smith@example.com");
        }

        @Test
        @DisplayName("Set email with uppercase should format to lowercase")
        void testSetEmailWithUppercase() {
            student.setEmail("Jane.Smith@Example.COM");
            assertThat(student.getEmail()).isEqualTo("jane.smith@example.com");
        }

        @Test
        @DisplayName("Set invalid email should throw and rollback")
        void testSetInvalidEmail() {
            String originalEmail = student.getEmail();

            assertThatThrownBy(() -> student.setEmail("invalid-email"))
                    .isInstanceOf(InvalidDataException.class);

            assertThat(student.getEmail()).isEqualTo(originalEmail);
        }

        @Test
        @DisplayName("Set valid student ID")
        void testSetValidStudentId() {
            student.setStudentId("ITT876");
            assertThat(student.getStudentId()).isEqualTo("ITT876");
        }

        @Test
        @DisplayName("Set invalid student ID should throw and rollback")
        void testSetInvalidStudentId() {
            String originalId = student.getStudentId();

            assertThatThrownBy(() -> student.setStudentId("INVALID"))
                    .isInstanceOf(InvalidDataException.class);

            assertThat(student.getStudentId()).isEqualTo(originalId);
        }

        @Test
        @DisplayName("Set null student ID should throw and rollback")
        void testSetNullStudentId() {
            String originalId = student.getStudentId();

            assertThatThrownBy(() -> student.setStudentId(null))
                    .isInstanceOf(InvalidDataException.class);

            assertThat(student.getStudentId()).isEqualTo(originalId);
        }

        @Test
        @DisplayName("Set valid group")
        void testSetValidGroup() {
            Group newGroup = Group.createValidGroup(202, "Mathematics", 2024);
            student.setGroup(newGroup);
            assertThat(student.getGroup()).isEqualTo(newGroup);
        }

        @Test
        @DisplayName("Set null group should throw and rollback")
        void testSetNullGroup() {
            Group originalGroup = student.getGroup();

            assertThatThrownBy(() -> student.setGroup(null))
                    .isInstanceOf(InvalidDataException.class);

            assertThat(student.getGroup()).isEqualTo(originalGroup);
        }
    }

    @Nested
    @DisplayName("Business logic tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Get student fullName returns correct format")
        void testGetStudentInfo() {
            Student student = new Student("John", "Doe", "john@example.com",
                    "CSS234", validGroup);
            String info = student.getFullName();

            assertThat(info).contains("John Doe");
            assertThat(info).contains("CSS234");
        }

        @Test
        @DisplayName("Get full name returns formatted name")
        void testGetFullName() {
            Student student = new Student("John", "Doe", "john@example.com",
                    "CSS235", validGroup);

            assertThat(student.getFullName()).isEqualTo("John Doe (ID: CSS235)");
        }
    }

    @Nested
    @DisplayName("Equals and HashCode tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Students with different student IDs are not equal")
        void testNotEqualsDifferentStudentId() {
            Student student1 = new Student("John", "Doe", "john@example.com",
                    "CSS235", validGroup);
            Student student2 = new Student("John", "Doe", "john@example.com",
                    "ITT875", validGroup);

            assertThat(student1).isNotEqualTo(student2);
        }

        @Test
        @DisplayName("Student equals itself")
        void testEqualsItself() {
            Student student = new Student("John", "Doe", "john@example.com",
                    "CSS234", validGroup);

            assertThat(student).isEqualTo(student);
        }

        @Test
        @DisplayName("Student not equals null")
        void testNotEqualsNull() {
            Student student = new Student("John", "Doe", "john@example.com",
                    "CSS234", validGroup);

            assertThat(student).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Student not equals different type")
        void testNotEqualsDifferentType() {
            Student student = new Student("John", "Doe", "john@example.com",
                    "CSS234", validGroup);

            assertThat(student).isNotEqualTo("CSS234");
        }
    }

    @Nested
    @DisplayName("CompareTo tests")
    class CompareToTests {

        @Test
        @DisplayName("Students with different first names compare by first name")
        void testCompareDifferentFirstName() {
            Student student1 = new Student("Alice", "Doe", "alice@example.com",
                    "CSS235", validGroup);
            Student student2 = new Student("Bob", "Doe", "bob@example.com",
                    "CSS236", validGroup);

            assertThat(student1.compareTo(student2)).isNegative();
            assertThat(student2.compareTo(student1)).isPositive();
        }

        @Test
        @DisplayName("Students with same first name compare by last name")
        void testCompareSameFirstName() {
            Student student1 = new Student("John", "Adams", "john.a@example.com",
                    "CSS235", validGroup);
            Student student2 = new Student("John", "Baker", "john.b@example.com",
                    "CSS236", validGroup);

            assertThat(student1.compareTo(student2)).isNegative();
            assertThat(student2.compareTo(student1)).isPositive();
        }

        @Test
        @DisplayName("Students with same names compare by student ID")
        void testCompareSameNames() {
            Student student1 = new Student("John", "Doe", "john@example.com",
                    "CSS235", validGroup);
            Student student2 = new Student("John", "Doe", "jane@example.com",
                    "CSS236", validGroup);

            assertThat(student1.compareTo(student2)).isNegative();
            assertThat(student2.compareTo(student1)).isPositive();
        }

        @Test
        @DisplayName("Students with identical first name, last name, and student ID compare as equal")
        void testCompareIdenticalStudents() {
            Student student1 = new Student("John", "Doe", "john@example.com",
                    "CSS235", validGroup);
            Student student2 = new Student("John", "Doe", "different@example.com",
                    "CSS235", validGroup);

            assertThat(student1.compareTo(student2)).isZero();
        }

        @Test
        @DisplayName("Student compared to Person uses Person comparator")
        void testCompareToPersonFallback() {
            Student student = new Student("John", "Doe", "john@example.com",
                    "CSS234", validGroup);
            Person person = Person.createValidPerson("Alice", "Smith", "alice@example.com");

            assertThat(student.compareTo(person)).isPositive(); // "John" > "Alice"
        }
    }

    @Nested
    @DisplayName("ToString tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString contains all student information")
        void testToString() {
            Student student = new Student("John", "Doe", "john@example.com",
                    "CSS234", validGroup);
            String toString = student.toString();

            assertThat(toString).contains("John");
            assertThat(toString).contains("Doe");
            assertThat(toString).contains("john@example.com");
            assertThat(toString).contains("CSS234");
        }
    }
}