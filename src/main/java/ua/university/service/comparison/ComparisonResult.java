package ua.university.service.comparison;

/**
 * Result of performance comparison between different approaches
 */
public record ComparisonResult(
        String operationName,
        int resultCount,
        long sequentialTimeMs,
        long parallelStreamTimeMs,
        long executorServiceTimeMs
) {
    public String getFastestApproach() {
        if (sequentialTimeMs <= parallelStreamTimeMs && sequentialTimeMs <= executorServiceTimeMs) {
            return "Sequential";
        }
        if (parallelStreamTimeMs <= executorServiceTimeMs) {
            return "ParallelStream";
        }
        return "ExecutorService";

    }

    @Override
    public String toString() {
        return String.format(
                """
                        === %s ===
                        Result count: %d
                        Sequential:      %d ms
                        ParallelStream:  %d ms
                        ExecutorService: %d ms
                        Fastest: %s
                        """,
                operationName,
                resultCount,
                sequentialTimeMs,
                parallelStreamTimeMs,
                executorServiceTimeMs,
                getFastestApproach()
        );
    }
}