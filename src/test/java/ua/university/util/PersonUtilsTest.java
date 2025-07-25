package ua.university.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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

        @Test
        @DisplayName("Constructor should be accessible via reflection")
        void testConstructorAccessibleViaReflection() throws Exception {
            Constructor<PersonUtils> constructor = PersonUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            PersonUtils instance = constructor.newInstance();
            assertNotNull(instance);
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
                    PersonUtils.class.getDeclaredMethod("isValidName", String.class),
                    PersonUtils.class.getDeclaredMethod("isValidEmail", String.class)
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

        @Test
        @DisplayName("Should return null when input is null")
        void testNullInput() {
            assertNull(PersonUtils.capitalizeText(null));
        }

        @Test
        @DisplayName("Should return original when input is empty after trim")
        void testEmptyInput() {
            assertEquals("", PersonUtils.capitalizeText(""));
            assertEquals("   ", PersonUtils.capitalizeText("   "));
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

        @Test
        @DisplayName("Should return empty string when firstName is null")
        void testFirstNameNull() {
            assertEquals("", PersonUtils.formatName(null, "Doe"));
        }

        @Test
        @DisplayName("Should return empty string when lastName is null")
        void testLastNameNull() {
            assertEquals("", PersonUtils.formatName("John", null));
        }

        @Test
        @DisplayName("Should return empty string when both names are null")
        void testBothNamesNull() {
            assertEquals("", PersonUtils.formatName(null, null));
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
        @DisplayName("Should return null when email is null")
        void testNullEmail() {
            assertNull(PersonUtils.formatEmail(null));
        }

        @Test
        @DisplayName("Should convert to lowercase and trim")
        void testBasicFormatting() {
            assertEquals("john@example.com", PersonUtils.formatEmail("JOHN@EXAMPLE.COM"));
            assertEquals("jane@test.org", PersonUtils.formatEmail("Jane@Test.Org"));
            assertEquals("test@domain.com", PersonUtils.formatEmail("  TEST@DOMAIN.COM  "));
        }

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            assertEquals("", PersonUtils.formatEmail(""));
        }

        @Test
        @DisplayName("Should handle whitespace-only string")
        void testWhitespaceOnly() {
            assertEquals("", PersonUtils.formatEmail("   "));
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
        @DisplayName("Should return null when names array is null")
        void testNullArray() {
            assertNull(PersonUtils.generateEmailFromNames((String[]) null));
        }

        @Test
        @DisplayName("Should return null when names array is empty")
        void testEmptyArray() {
            assertNull(PersonUtils.generateEmailFromNames(new String[0]));
        }

        @Test
        @DisplayName("Should return null when any name is null")
        void testNullName() {
            assertNull(PersonUtils.generateEmailFromNames("John", null, "Doe"));
            assertNull(PersonUtils.generateEmailFromNames(null));
        }

        @Test
        @DisplayName("Should return null when any name is empty after trim")
        void testEmptyName() {
            assertNull(PersonUtils.generateEmailFromNames("John", "", "Doe"));
            assertNull(PersonUtils.generateEmailFromNames("John", "   ", "Doe"));
        }

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

        @Test
        @DisplayName("Should return false for null name")
        void testNullName() {
            assertFalse(PersonUtils.isValidName(null));
        }

        @Test
        @DisplayName("Should return false for empty name")
        void testEmptyName() {
            assertFalse(PersonUtils.isValidName(""));
            assertFalse(PersonUtils.isValidName("   "));
        }

        @Test
        @DisplayName("Should return true for valid length names")
        void testValidLengthNames() {
            assertTrue(PersonUtils.isValidName("A"));
            assertTrue(PersonUtils.isValidName("John"));
            assertTrue(PersonUtils.isValidName("A".repeat(50)));
        }

        @ParameterizedTest
        @ValueSource(ints = {51, 100, 200})
        @DisplayName("Should return false for names exceeding maximum length of 50 characters")
        void testNamesTooLong(int length) {
            String longName = "A".repeat(length);
            boolean actual = PersonUtils.isValidName(longName);
            assertFalse(actual,
                    () -> String.format("Expected name of length %d to be invalid (max allowed: 50), but was valid", length));
        }

        @ParameterizedTest
        @MethodSource("trimmedLengthTestCases")
        @DisplayName("Should validate names based on trimmed length")
        void testValidationUsesTrimmedLength(String name, int expectedLength, boolean expectedValid) {
            boolean actual = PersonUtils.isValidName(name);
            assertEquals(expectedValid, actual,
                    () -> String.format("Expected name '%s' (trimmed length: %d) to be %s",
                            name, expectedLength, expectedValid ? "valid" : "invalid"));
        }

        static Stream<Arguments> trimmedLengthTestCases() {
            return Stream.of(
                    Arguments.of("  John  ", 4, true),
                    Arguments.of("  A  ", 1, true),
                    Arguments.of("  " + "A".repeat(50) + "  ", 50, true),
                    Arguments.of("  " + "A".repeat(51) + "  ", 51, false)
            );
        }
    }

    @Nested
    @DisplayName("isValidEmail Tests")
    class IsValidEmailTests {

        @Test
        @DisplayName("Should return false for null email")
        void testNullEmail() {
            boolean actual = PersonUtils.isValidEmail(null);
            assertFalse(actual, "Expected null email to be invalid, but was valid");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "user@domain.com",
                "test.email@example.org",
                "user-name@sub.domain.co.uk",
                "123@numbers.net",
                "a@b.co"
        })
        @DisplayName("Should return true for valid email formats")
        void testValidEmails(String email) {
            boolean actual = PersonUtils.isValidEmail(email);
            assertTrue(actual,
                    () -> String.format("Expected email '%s' to be valid, but was invalid", email));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "invalid",
                "@domain.com",
                "user@",
                "user@domain",
                "user@domain.",
                "user@domain.c",
                "user.domain.com",
                "user@domain..com",
                "user@@domain.com"
        })
        @DisplayName("Should return false for invalid email formats")
        void testInvalidEmails(String email) {
            boolean actual = PersonUtils.isValidEmail(email);
            assertFalse(actual,
                    () -> String.format("Expected email '%s' to be invalid, but was valid", email));
        }
    }
}
