package ua.university.service.comparison;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonResultTest {

    @Test
    void getFastestApproach_WhenSequentialFastest_ShouldReturnSequential() {
        ComparisonResult result = new ComparisonResult("Test", 10, 5, 10, 15);

        assertEquals("Sequential", result.getFastestApproach());
    }

    @Test
    void getFastestApproach_WhenParallelStreamFastest_ShouldReturnParallelStream() {
        ComparisonResult result = new ComparisonResult("Test", 10, 15, 5, 10);

        assertEquals("ParallelStream", result.getFastestApproach());
    }

    @Test
    void getFastestApproach_WhenExecutorServiceFastest_ShouldReturnExecutorService() {
        ComparisonResult result = new ComparisonResult("Test", 10, 15, 10, 5);

        assertEquals("ExecutorService", result.getFastestApproach());
    }

    @Test
    void getFastestApproach_WhenAllEqual_ShouldReturnSequential() {
        ComparisonResult result = new ComparisonResult("Test", 10, 10, 10, 10);

        assertEquals("Sequential", result.getFastestApproach());
    }
}