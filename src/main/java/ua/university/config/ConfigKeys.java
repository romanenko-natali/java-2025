package ua.university.config;

/**
 * Constants for configuration property keys
 */
public final class ConfigKeys {

    private ConfigKeys() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String DATA_PATH_BASE = "data.path.base";
    public static final String DATA_PATH_COURSES_JSON = "data.path.courses.json";
    public static final String DATA_PATH_COURSES_YAML = "data.path.courses.yaml";
    public static final String DATA_PATH_TEACHERS_JSON = "data.path.teachers.json";
    public static final String DATA_PATH_TEACHERS_YAML = "data.path.teachers.yaml";
    public static final String DATA_PATH_STUDENTS_JSON = "data.path.students.json";
    public static final String DATA_PATH_STUDENTS_YAML = "data.path.students.yaml";
    public static final String DATA_PATH_GROUPS_JSON = "data.path.groups.json";
    public static final String DATA_PATH_GROUPS_YAML = "data.path.groups.yaml";

    public static final String TEST_DATA_COUNT = "test.data.count";
}