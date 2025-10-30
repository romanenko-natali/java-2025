package ua.university.repository;

/**
 * Functional interface for extracting identity from objects
 */
@FunctionalInterface
public interface IdentityExtractor<T> {
    String extractIdentity(T object);
}
