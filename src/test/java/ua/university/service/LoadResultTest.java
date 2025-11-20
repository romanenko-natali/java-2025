package ua.university.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadResultTest {

    @Test
    void getTotalItems_ShouldSumAllCounts() {
        LoadResult result = new LoadResult(10, 5, 3, 8, 100);

        assertEquals(26, result.getTotalItems());
    }

    @Test
    void toString_ShouldContainAllInfo() {
        LoadResult result = new LoadResult(10, 5, 3, 8, 100);

        String str = result.toString();

        assertTrue(str.contains("10"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("3"));
        assertTrue(str.contains("8"));
        assertTrue(str.contains("100"));
    }
}