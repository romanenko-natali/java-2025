package ua.university.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.university.model.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class GenericRepositoryThreadSafetyTest {

    private SubjectRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SubjectRepository();
    }

    @Test
    void add_FromMultipleThreads_ShouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        int itemsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadNum = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < itemsPerThread; i++) {
                        String name = "Subject-" + threadNum + "-" + i;
                        repository.add(new Subject(name, (i % 5) + 1));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount * itemsPerThread, repository.size());
    }

    @Test
    void addAll_ShouldAddMultipleItems() {
        List<Subject> subjects = List.of(
                new Subject("Програмування", 5),
                new Subject("Математика", 4),
                new Subject("Фізика", 3)
        );

        int added = repository.addAll(subjects);

        assertEquals(3, added);
        assertEquals(3, repository.size());
    }

    @Test
    void addAll_WithDuplicates_ShouldSkipDuplicates() {
        repository.add(new Subject("Програмування", 5));

        List<Subject> subjects = List.of(
                new Subject("Програмування", 5), // дублікат
                new Subject("Математика", 4)
        );

        int added = repository.addAll(subjects);

        assertEquals(1, added); // тільки Математика
        assertEquals(2, repository.size());
    }
}