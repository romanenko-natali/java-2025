package ua.university.service.loading;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadingStrategyTest {

    @Test
    void sequentialStrategy_ShouldNotBeNull() {
        LoadingStrategy strategy = new SequentialLoadingStrategy();
        assertNotNull(strategy);
    }

    @Test
    void parallelStrategy_ShouldNotBeNull() {
        LoadingStrategy strategy = new ParallelLoadingStrategy();
        assertNotNull(strategy);
    }

    @Test
    void executorStrategy_ShouldUseDefaultPoolSize() {
        ExecutorLoadingStrategy strategy = new ExecutorLoadingStrategy();
        assertNotNull(strategy);
    }

    @Test
    void executorStrategy_ShouldAcceptCustomPoolSize() {
        ExecutorLoadingStrategy strategy = new ExecutorLoadingStrategy(8);
        assertNotNull(strategy);
    }
}