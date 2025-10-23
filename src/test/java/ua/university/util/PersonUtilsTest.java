package ua.university.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import ua.university.exception.InvalidDataException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PersonUtilsTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should be private")
        void testConstructorIsPrivate() throws NoSuchMethodException {
            Constructor<PersonUtils> constructor = PersonUtils.class.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Method Access Modifiers Tests")
    class MethodAccessModifiersTests {

        @Test
        @DisplayName("All methods should be public static")
        void testAllMethodsArePublicStatic() throws NoSuchMethodException {
            Method[] methods = {
                    PersonUtils.class.getDeclaredMethod("capitalizeText", String.class),
                    PersonUtils.class.getDeclaredMethod("formatName", String.class, String.class),
                    PersonUtils.class.getDeclaredMethod("formatEmail", String.class),
                    PersonUtils.class.getDeclaredMethod("generateEmailFromNames", String[].class),
                    PersonUtils.class.getDeclaredMethod("validateName", String.class),
                    PersonUtils.class.getDeclaredMethod("validateEmail", String.class)
            };

            for (Method method : methods) {
                assertTrue(Modifier.isPublic(method.getModifiers()),
                        method.getName() + " should be public");
                assertTrue(Modifier.isStatic(method.getModifiers()),
                        method.getName() + " should be static");
            }
        }
    }

    @Nested
    @DisplayName("capitalizeText Tests")
    class CapitalizeTextTests {

        @ParameterizedTest
        @NullSource
        void testInvalidNames(String invalidName) {
            assertThrows(
                    InvalidDataException.class,
                    () -> PersonUtils.capitalizeText(invalidName),
                    "Expected capitalizeText() to throw InvalidDataException for: " + invalidName
            );
        }

        @Test
        @DisplayName("Should return empty string when input is empty after trim")
        void testEmptyInput() {
            assertEquals("", PersonUtils.capitalizeText(""));
            assertEquals("", PersonUtils.capitalizeText("   "));
        }

        @Test
        @DisplayName("Should capitalize first letter and lowercase rest")
        void testBasicCapitalization() {
            assertEquals("Hello", PersonUtils.capitalizeText("hello"));
            assertEquals("Hello", PersonUtils.capitalizeText("HELLO"));
            assertEquals("Hello", PersonUtils.capitalizeText("HeLLo"));
            assertEquals("A", PersonUtils.capitalizeText("a"));
            assertEquals("A", PersonUtils.capitalizeText("A"));
        }

        @Test
        @DisplayName("Should trim whitespace before capitalizing")
        void testTrimsWhitespace() {
            assertEquals("Hello", PersonUtils.capitalizeText("  hello  "));
            assertEquals("Hello", PersonUtils.capitalizeText("\thello\n"));
        }

        @Test
        @DisplayName("Should handle special characters")
        void testSpecialCharacters() {
            assertEquals("Hello-world", PersonUtils.capitalizeText("hello-world"));
            assertEquals("Hello123", PersonUtils.capitalizeText("hello123"));
            assertEquals("123hello", PersonUtils.capitalizeText("123hello"));
        }
    }

    @Nested
    @DisplayName("formatName Tests")
    class FormatNameTests {

        static Stream<Object[]> invalidNamePairs() {
            return Stream.of(
                    new Object[]{null, null},
                    new Object[]{null, "Smith"},
                    new Object[]{"John", null}
            );
        }

        @ParameterizedTest
        @MethodSource("invalidNamePairs")
        void testInvalidNamePairs(String firstName, String lastName) {
            assertThrows(
                    InvalidDataException.class,
                    () -> PersonUtils.formatName(firstName, lastName),
                    () -> "Expected InvalidDataException for firstName='" + firstName + "', lastName='" + lastName + "'"
            );
        }

        @Test
        @DisplayName("Should format names correctly")
        void testBasicFormatting() {
            assertEquals("John Doe", PersonUtils.formatName("John", "Doe"));
            assertEquals("Jane Smith", PersonUtils.formatName("Jane", "Smith"));
        }

        @Test
        @DisplayName("Should trim whitespace from names")
        void testTrimsWhitespace() {
            assertEquals("John Doe", PersonUtils.formatName("  John  ", "  Doe  "));
            assertEquals("Jane Smith", PersonUtils.formatName("\tJane\n", "\rSmith\t"));
        }

        @Test
        @DisplayName("Should handle empty strings")
        void testEmptyStrings() {
            assertEquals(" ", PersonUtils.formatName("", ""));
            assertEquals("John ", PersonUtils.formatName("John", ""));
            assertEquals(" Doe", PersonUtils.formatName("", "Doe"));
        }
    }

    @Nested
    @DisplayName("formatEmail Tests")
    class FormatEmailTests {

        @Test
        @DisplayName("Should convert to lowercase and trim")
        void testBasicFormatting() {
            assertEquals("john@example.com", PersonUtils.formatEmail("JOHN@EXAMPLE.COM"));
            assertEquals("jane@test.org", PersonUtils.formatEmail("Jane@Test.Org"));
            assertEquals("test@domain.com", PersonUtils.formatEmail("  TEST@DOMAIN.COM  "));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testNullAndEmptyNames(String invalidName) {
            assertThrows(
                    InvalidDataException.class,
                    () -> PersonUtils.formatEmail(invalidName),
                    "Expected formatEmail() to throw InvalidDataException for: " + invalidName
            );
        }

        @Test
        void testWhitespaceOnly() {
            assertThrows(
                    InvalidDataException.class,
                    () -> PersonUtils.formatEmail("   ")
            );
        }

        @Test
        @DisplayName("Should preserve email structure")
        void testPreservesStructure() {
            assertEquals("user.name@sub.domain.com", PersonUtils.formatEmail("User.Name@Sub.Domain.Com"));
        }
    }

    @Nested
    @DisplayName("generateEmailFromNames Tests")
    class GenerateEmailFromNamesTests {

        @Test
        @DisplayName("Should generate email from single name")
        void testSingleName() {
            assertEquals("john@university.edu", PersonUtils.generateEmailFromNames("John"));
            assertEquals("jane@university.edu", PersonUtils.generateEmailFromNames("JANE"));
        }

        @Test
        @DisplayName("Should generate email from multiple names")
        void testMultipleNames() {
            assertEquals("john.doe@university.edu", PersonUtils.generateEmailFromNames("John", "Doe"));
            assertEquals("john.michael.doe@university.edu", PersonUtils.generateEmailFromNames("John", "Michael", "Doe"));
        }

        @Test
        @DisplayName("Should trim and lowercase names")
        void testTrimsAndLowercases() {
            assertEquals("john.doe@university.edu", PersonUtils.generateEmailFromNames("  JOHN  ", "  DOE  "));
            assertEquals("jane.smith@university.edu", PersonUtils.generateEmailFromNames("\tJANE\n", "\rSMITH\t"));
        }
    }

    @Nested
    @DisplayName("isValidName Tests")
    class IsValidNameTests {

        @ParameterizedTest
        @NullAndEmptySource
        void testInvalidNames(String invalidName) {
            assertThrows(
                    InvalidDataException.class,
                    () -> PersonUtils.validateName(invalidName),
                    "Expected validateName() to throw InvalidDataException for: " + invalidName
            );
        }
    }
}
