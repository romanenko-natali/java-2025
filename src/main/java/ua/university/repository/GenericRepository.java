package ua.university.repository;

import java.util.*;
import java.util.logging.Logger;

/**
 * Generic repository for managing collections of objects
 */
class GenericRepository<T> {
    private static final Logger logger = Logger.getLogger(GenericRepository.class.getName());

    protected final List<T> items;
    private final IdentityExtractor<T> identityExtractor;
    private final String entityType;

    public GenericRepository(IdentityExtractor<T> identityExtractor, String entityType) {
        this.items = new ArrayList<>();
        this.identityExtractor = identityExtractor;
        this.entityType = entityType;
        logger.info("Created repository for " + entityType);
    }

    /**
     * Add an item to the repository
     */
    public boolean add(T item) {
        if (item == null) {
            logger.warning("Attempted to add null " + entityType);
            return false;
        }

        String identity = identityExtractor.extractIdentity(item);
        if (findByIdentity(identity).isPresent()) {
            logger.warning("Cannot add " + entityType + " - already exists with identity: " + identity);
            return false;
        }

        boolean added = items.add(item);
        if (added) {
            logger.info("Added " + entityType + ": " + identity);
        }
        return added;
    }

    /**
     * Remove an item using equals() method - RECOMMENDED APPROACH
     */
    public boolean remove(T item) {
        if (item == null) {
            logger.warning("Attempted to remove null " + entityType);
            return false;
        }

        boolean removed = items.remove(item); // Uses equals() internally
        if (removed) {
            logger.info("Removed " + entityType + ": " + identityExtractor.extractIdentity(item));
        } else {
            logger.warning("Failed to remove " + entityType + ": " + identityExtractor.extractIdentity(item));
        }
        return removed;
    }

    /**
     * Remove by identity - alternative approach when you only have the identity
     */
    public boolean removeByIdentity(String identity) {
        if (identity == null) {
            logger.warning("Attempted to remove " + entityType + " with null identity");
            return false;
        }

        Optional<T> itemToRemove = items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();

        if (itemToRemove.isPresent()) {
            boolean removed = items.remove(itemToRemove.get());
            if (removed) {
                logger.info("Removed " + entityType + " by identity: " + identity);
            }
            return removed;
        } else {
            logger.warning("No " + entityType + " found with identity: " + identity + " to remove");
            return false;
        }
    }

    /**
     * Check if repository contains an item using equals()
     */
    public boolean contains(T item) {
        return items.contains(item); // Uses equals() internally
    }

    /**
     * Check if repository contains an item with given identity
     */
    public boolean containsIdentity(String identity) {
        return findByIdentity(identity).isPresent();
    }

    /**
     * Find an object by its unique identity field
     */
    public Optional<T> findByIdentity(String identity) {
        if (identity == null) {
            logger.warning("Attempted to find " + entityType + " with null identity");
            return Optional.empty();
        }

        Optional<T> result = items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();

        if (result.isPresent()) {
            logger.info("Found " + entityType + " with identity: " + identity);
        } else {
            logger.info("No " + entityType + " found with identity: " + identity);
        }

        return result;
    }

    public List<T> getAll() {
        logger.info("Retrieved all " + entityType + " items. Count: " + items.size());
        return new ArrayList<>(items);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        int sizeBefore = items.size();
        items.clear();
        logger.info("Cleared repository. Removed " + sizeBefore + " " + entityType + " items");
    }

    /**
     * Sort items by identity using ascending or descending order.
     */
    public void sortByIdentity(boolean asc) {
        items.sort(Comparator.comparing(identityExtractor::extractIdentity));
        if (!asc) {
            Collections.reverse(items);
        }
        logger.info("Sorted " + entityType + " by identity in " + (asc ? "ascending" : "descending") + " order");
    }

    /**
     * Alternative: sort using String order ("desc" for descending, any other value for ascending)
     */
    public void sortByIdentity(String order) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        sortByIdentity(asc);
    }

    /**
     * Package-private method for testing - allows direct access to items
     * This should ONLY be used by tests in the same package
     */
    List<T> getItemsForTesting() {
        return items;
    }
}
