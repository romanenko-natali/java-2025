package ua.university.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create person with null fields")
        void testDefaultConstructor() {
            Person person = new Person();

            assertNull(person.getFirstName(), "Expected firstName to be null after default constructor");
            assertNull(person.getLastName(), "Expected lastName to be null after default constructor");
            assertNull(person.getEmail(), "Expected email to be null after default constructor");
        }

        @ParameterizedTest
        @CsvSource({
                "john, doe, john@test.com, John, Doe, john@test.com",
                "JANE, SMITH, JANE@TEST.ORG, Jane, Smith, jane@test.org",
                "  bob  , '  wilson  ', '  BOB@EXAMPLE.COM  ', Bob, Wilson, bob@example.com"
        })
        @DisplayName("Parameterized constructor should set and format fields correctly")
        void testParameterizedConstructor(String inputFirst, String inputLast, String inputEmail,
                                          String expectedFirst, String expectedLast, String expectedEmail) {
            Person person = new Person(inputFirst, inputLast, inputEmail);

            assertEquals(expectedFirst, person.getFirstName(),
                    () -> String.format("Expected firstName to be '%s' but was '%s'", expectedFirst, person.getFirstName()));
            assertEquals(expectedLast, person.getLastName(),
                    () -> String.format("Expected lastName to be '%s' but was '%s'", expectedLast, person.getLastName()));
            assertEquals(expectedEmail, person.getEmail(),
                    () -> String.format("Expected email to be '%s' but was '%s'", expectedEmail, person.getEmail()));
        }

        @Test
        @DisplayName("Constructor should not set invalid fields")
        void testConstructorWithInvalidData() {
            Person person = new Person("", "validname", "invalid-email");

            assertNull(person.getFirstName(), "Expected firstName to be null when invalid name provided");
            assertEquals("Validname", person.getLastName(), "Expected lastName to be set when valid");
            assertNull(person.getEmail(), "Expected email to be null when invalid email provided");
        }
    }

    @Nested
    @DisplayName("Access Modifier Tests")
    class AccessModifierTests {

        @Test
        @DisplayName("getFullName method should be protected")
        void testGetFullNameIsProtected() throws NoSuchMethodException {
            Method method = Person.class.getDeclaredMethod("getFullName");
            assertTrue(Modifier.isProtected(method.getModifiers()),
                    "Expected getFullName method to be protected, but was: " + Modifier.toString(method.getModifiers()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"firstName", "lastName", "email"})
        @DisplayName("Fields should be protected")
        void testFieldsAreProtected(String fieldName) throws NoSuchFieldException {
            var field = Person.class.getDeclaredField(fieldName);
            assertTrue(Modifier.isProtected(field.getModifiers()),
                    () -> String.format("Expected field '%s' to be protected, but was: %s",
                            fieldName, Modifier.toString(field.getModifiers())));
        }
    }

    @Nested
    @DisplayName("getFullName Tests")
    class GetFullNameTests {

        @Test
        @DisplayName("Should format full name with both names set")
        void testGetFullNameWithBothNames() throws Exception {
            Person person = new Person();
            person.setFirstName("John");
            person.setLastName("Doe");

            // Use reflection to access protected method
            Method getFullNameMethod = Person.class.getDeclaredMethod("getFullName");
            getFullNameMethod.setAccessible(true);
            String actualFullName = (String) getFullNameMethod.invoke(person);

            assertEquals("John Doe", actualFullName,
                    () -> String.format("Expected full name to be '%s' but was '%s'", "John Doe", actualFullName));
        }

        @Test
        @DisplayName("Should format full name when firstName is null")
        void testGetFullNameWithNullFirstName() throws Exception {
            Person person = new Person();
            // firstName remains null (not set)
            person.setLastName("Wilson");

            Method getFullNameMethod = Person.class.getDeclaredMethod("getFullName");
            getFullNameMethod.setAccessible(true);
            String actualFullName = (String) getFullNameMethod.invoke(person);

            assertEquals("", actualFullName,
                    () -> String.format("Expected full name to be empty string when firstName is null, but was '%s'", actualFullName));
        }

        @Test
        @DisplayName("Should format full name when lastName is null")
        void testGetFullNameWithNullLastName() throws Exception {
            Person person = new Person();
            person.setFirstName("Bob");
            // lastName remains null (not set)

            Method getFullNameMethod = Person.class.getDeclaredMethod("getFullName");
            getFullNameMethod.setAccessible(true);
            String actualFullName = (String) getFullNameMethod.invoke(person);

            assertEquals("", actualFullName,
                    () -> String.format("Expected full name to be empty string when lastName is null, but was '%s'", actualFullName));
        }

        @Test
        @DisplayName("Should format full name when both names are null")
        void testGetFullNameWithBothNamesNull() throws Exception {
            Person person = new Person();
            // Both names remain null (not set)

            Method getFullNameMethod = Person.class.getDeclaredMethod("getFullName");
            getFullNameMethod.setAccessible(true);
            String actualFullName = (String) getFullNameMethod.invoke(person);

            assertEquals("", actualFullName,
                    () -> String.format("Expected full name to be empty string but was '%s'", actualFullName));
        }
    }

    @Nested
    @DisplayName("FirstName Tests")
    class FirstNameTests {

        @ParameterizedTest
        @CsvSource({
                "john, John",
                "JANE, Jane",
                "bOb, Bob",
                "'  alice  ', Alice"
        })
        @DisplayName("Should set and capitalize valid first names")
        void testSetValidFirstName(String input, String expected) {
            Person person = new Person();
            person.setFirstName(input);

            assertEquals(expected, person.getFirstName(),
                    () -> String.format("Expected firstName to be '%s' but was '%s'", expected, person.getFirstName()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @DisplayName("Should not set invalid first names - empty and whitespace")
        void testSetInvalidFirstName(String invalidName) {
            Person person = new Person();
            person.setFirstName(invalidName);

            assertNull(person.getFirstName(),
                    () -> String.format("Expected firstName to remain null for invalid input '%s', but was '%s'",
                            invalidName, person.getFirstName()));
        }

        @Test
        @DisplayName("Should not set first name that exceeds maximum length")
        void testSetFirstNameTooLong() {
            String tooLongName = "A".repeat(51);
            Person person = new Person();
            person.setFirstName(tooLongName);

            assertNull(person.getFirstName(),
                    () -> String.format("Expected firstName to remain null for name length %d (max: 50), but was '%s'",
                            tooLongName.length(), person.getFirstName()));
        }

        @Test
        @DisplayName("Should not set null first name")
        void testSetNullFirstName() {
            Person person = new Person();
            person.setFirstName(null);

            assertNull(person.getFirstName(), "Expected firstName to remain null when null is provided");
        }
    }

    @Nested
    @DisplayName("LastName Tests")
    class LastNameTests {

        @ParameterizedTest
        @CsvSource({
                "smith, Smith",
                "JOHNSON, Johnson",
                "wIlSoN, Wilson",
                "'  brown  ', Brown"
        })
        @DisplayName("Should set and capitalize valid last names")
        void testSetValidLastName(String input, String expected) {
            Person person = new Person();
            person.setLastName(input);

            assertEquals(expected, person.getLastName(),
                    () -> String.format("Expected lastName to be '%s' but was '%s'", expected, person.getLastName()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @DisplayName("Should not set invalid last names - empty and whitespace")
        void testSetInvalidLastName(String invalidName) {
            Person person = new Person();
            person.setLastName(invalidName);

            assertNull(person.getLastName(),
                    () -> String.format("Expected lastName to remain null for invalid input '%s', but was '%s'",
                            invalidName, person.getLastName()));
        }

        @Test
        @DisplayName("Should not set last name that exceeds maximum length")
        void testSetLastNameTooLong() {
            String tooLongName = "B".repeat(51);
            Person person = new Person();
            person.setLastName(tooLongName);

            assertNull(person.getLastName(),
                    () -> String.format("Expected lastName to remain null for name length %d (max: 50), but was '%s'",
                            tooLongName.length(), person.getLastName()));
        }

        @Test
        @DisplayName("Should not set null last name")
        void testSetNullLastName() {
            Person person = new Person();
            person.setLastName(null);

            assertNull(person.getLastName(), "Expected lastName to remain null when null is provided");
        }
    }

    @Nested
    @DisplayName("Email Tests")
    class EmailTests {

        @ParameterizedTest
        @CsvSource({
                "USER@DOMAIN.COM, user@domain.com",
                "Test.Email@Example.Org, test.email@example.org"
        })
        @DisplayName("Should set and format valid emails")
        void testSetValidEmail(String input, String expected) {
            Person person = new Person();
            person.setEmail(input);

            assertEquals(expected, person.getEmail(),
                    () -> String.format("Expected email to be '%s' but was '%s'", expected, person.getEmail()));
        }

        @Test
        @DisplayName("Should trim and format email with whitespace")
        void testSetEmailWithWhitespace() {
            // Current implementation formats FIRST, then validates
            // So emails with spaces are trimmed before validation
            String emailWithSpaces = "  ADMIN@SITE.NET  ";
            String expectedEmail = "admin@site.net";

            Person person = new Person();
            person.setEmail(emailWithSpaces);

            assertEquals(expectedEmail, person.getEmail(),
                    () -> String.format("Expected email to be formatted to '%s' but was '%s'",
                            expectedEmail, person.getEmail()));
        }

        @Test
        @DisplayName("Should set email without leading/trailing spaces")
        void testSetEmailWithoutSpaces() {
            String validEmail = "ADMIN@SITE.NET";
            String expectedEmail = "admin@site.net";

            Person person = new Person();
            person.setEmail(validEmail);

            assertEquals(expectedEmail, person.getEmail(),
                    () -> String.format("Expected email to be '%s' but was '%s'", expectedEmail, person.getEmail()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "invalid", "@domain.com", "user@", "user@domain", "user.domain.com"})
        @DisplayName("Should not set invalid emails")
        void testSetInvalidEmail(String invalidEmail) {
            Person person = new Person();
            person.setEmail(invalidEmail);

            assertNull(person.getEmail(),
                    () -> String.format("Expected email to remain null for invalid input '%s', but was '%s'",
                            invalidEmail, person.getEmail()));
        }

        @Test
        @DisplayName("Should not set null email")
        void testSetNullEmail() {
            Person person = new Person();
            person.setEmail(null);

            assertNull(person.getEmail(), "Expected email to remain null when null is provided");
        }
    }

    @Nested
    @DisplayName("createPerson Static Method Tests")
    class CreatePersonTests {

        @ParameterizedTest
        @CsvSource({
                "john, doe, John, Doe, john.doe@university.edu",
                "jane, smith, Jane, Smith, jane.smith@university.edu",
                "bob, wilson, Bob, Wilson, bob.wilson@university.edu"
        })
        @DisplayName("Should create person with valid names")
        void testCreatePersonWithValidNames(String inputFirst, String inputLast,
                                            String expectedFirst, String expectedLast, String expectedEmail) {
            Person person = Person.createPerson(inputFirst, inputLast);

            assertNotNull(person,
                    () -> String.format("Expected person to be created with names '%s' and '%s'", inputFirst, inputLast));
            assertEquals(expectedFirst, person.getFirstName(),
                    () -> String.format("Expected firstName to be '%s' but was '%s'", expectedFirst, person.getFirstName()));
            assertEquals(expectedLast, person.getLastName(),
                    () -> String.format("Expected lastName to be '%s' but was '%s'", expectedLast, person.getLastName()));
            assertEquals(expectedEmail, person.getEmail(),
                    () -> String.format("Expected email to be '%s' but was '%s'", expectedEmail, person.getEmail()));
        }

        @ParameterizedTest
        @CsvSource({
                "'', validname",
                "validname, ''",
                "'   ', validname",
                "validname, '   '",
                "null, validname",
                "validname, null"
        })
        @DisplayName("Should return null for invalid names")
        void testCreatePersonWithInvalidNames(String firstName, String lastName) {
            // Handle "null" string as actual null
            String actualFirst = "null".equals(firstName) ? null : firstName;
            String actualLast = "null".equals(lastName) ? null : lastName;

            Person person = Person.createPerson(actualFirst, actualLast);

            assertNull(person,
                    () -> String.format("Expected null person for invalid names '%s' and '%s'", actualFirst, actualLast));
        }

        @Test
        @DisplayName("Should return null when both names are null")
        void testCreatePersonWithBothNamesNull() {
            Person person = Person.createPerson(null, null);

            assertNull(person, "Expected null person when both names are null");
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should format toString correctly with all fields")
        void testToStringWithAllFields() {
            Person person = new Person("John", "Doe", "john@domain.com");
            String expectedString = "Person{firstName='John', lastName='Doe', email='john@domain.com'}";

            assertEquals(expectedString, person.toString(),
                    () -> String.format("Expected toString to be '%s' but was '%s'", expectedString, person.toString()));
        }

        @Test
        @DisplayName("Should format toString correctly with null fields")
        void testToStringWithNullFields() {
            Person person = new Person();
            String expectedString = "Person{firstName='null', lastName='null', email='null'}";

            assertEquals(expectedString, person.toString(),
                    () -> String.format("Expected toString to be '%s' but was '%s'", expectedString, person.toString()));
        }
    }

    @Nested
    @DisplayName("equals and hashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void testEqualsReflexive() {
            Person person = new Person("John", "Doe", "john@domain.com");

            assertTrue(person.equals(person), "Person should be equal to itself");
        }

        @Test
        @DisplayName("Should be equal to person with same data")
        void testEqualsSymmetric() {
            Person person1 = new Person("John", "Doe", "john@domain.com");
            Person person2 = new Person("John", "Doe", "john@domain.com");

            assertTrue(person1.equals(person2),
                    "Persons with same data should be equal");
            assertTrue(person2.equals(person1),
                    "Equality should be symmetric");
        }

        @Test
        @DisplayName("Should not be equal to null")
        void testEqualsWithNull() {
            Person person = new Person("John", "Doe", "john@domain.com");

            assertFalse(person.equals(null), "Person should not be equal to null");
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void testEqualsWithDifferentClass() {
            Person person = new Person("John", "Doe", "john@domain.com");
            String notAPerson = "Not a person";

            assertFalse(person.equals(notAPerson), "Person should not be equal to different class");
        }

        @ParameterizedTest
        @MethodSource("differentPersonsProvider")
        @DisplayName("Should not be equal to person with different data")
        void testNotEqualsWithDifferentData(Person person1, Person person2, String reason) {
            assertFalse(person1.equals(person2),
                    () -> String.format("Persons should not be equal: %s", reason));
        }

        @Test
        @DisplayName("Equal persons should have same hashCode")
        void testHashCodeConsistency() {
            Person person1 = new Person("John", "Doe", "john@domain.com");
            Person person2 = new Person("John", "Doe", "john@domain.com");

            assertTrue(person1.equals(person2), "Persons should be equal");
            assertEquals(person1.hashCode(), person2.hashCode(),
                    "Equal persons should have same hashCode");
        }

        static Stream<Arguments> differentPersonsProvider() {
            Person basePerson = new Person("John", "Doe", "john@domain.com");
            return Stream.of(
                    Arguments.of(basePerson, new Person("Jane", "Doe", "john@domain.com"), "different firstName"),
                    Arguments.of(basePerson, new Person("John", "Smith", "john@domain.com"), "different lastName"),
                    Arguments.of(basePerson, new Person("John", "Doe", "jane@domain.com"), "different email"),
                    Arguments.of(basePerson, new Person(), "other person has null fields")
            );
        }
    }
}