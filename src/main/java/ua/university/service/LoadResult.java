package ua.university.service;

/**
 * Result of data loading operation
 */
public record LoadResult(
        int studentsLoaded,
        int teachersLoaded,
        int groupsLoaded,
        int subjectsLoaded,
        long durationMs
) {
    public int getTotalItems() {
        return studentsLoaded + teachersLoaded + groupsLoaded + subjectsLoaded;
    }

    @Override
    public String toString() {
        return String.format(
                "LoadResult{students=%d, teachers=%d, groups=%d, subjects=%d, total=%d, duration=%dms}",
                studentsLoaded, teachersLoaded, groupsLoaded, subjectsLoaded,
                getTotalItems(), durationMs
        );
    }
}