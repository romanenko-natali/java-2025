package ua.university.service.reporting;

import ua.university.model.Group;
import ua.university.model.Student;

import java.util.List;
import java.util.Map;

/**
 * University report containing various statistics
 */
public record UniversityReport(
        Map<String, Integer> studentsPerGroup,
        Map<String, Long> teachersPerDepartment,
        List<Student> graduatingStudents,
        Map<String, Integer> creditsByDifficulty,
        List<Group> activeGroups,
        long generationTimeMs
) {
    public int getTotalStudentsInGroups() {
        return studentsPerGroup.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getTotalTeachers() {
        return teachersPerDepartment.values().stream().mapToInt(Long::intValue).sum();
    }

    @Override
    public String toString() {
        return String.format(
                """
                
                === University Report ===
                Groups: %d (total students: %d)
                Departments: %d (total teachers: %d)
                Graduating students: %d
                Active groups: %d
                Credits by difficulty: %s
                Generation time: %d ms
                """,
                studentsPerGroup.size(),
                getTotalStudentsInGroups(),
                teachersPerDepartment.size(),
                getTotalTeachers(),
                graduatingStudents.size(),
                activeGroups.size(),
                creditsByDifficulty,
                generationTimeMs
        );
    }
}