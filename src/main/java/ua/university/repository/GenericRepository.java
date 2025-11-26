package ua.university.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.AlreadyExistsException;
import ua.university.exception.InvalidDataException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Generic repository for managing collections of objects
 * Thread-safe implementation for concurrent access
 */
public class GenericRepository<T> {
    private static final Logger logger = LoggerFactory.getLogger(GenericRepository.class);

    protected final List<T> items;
    private final IdentityExtractor<T> identityExtractor;
    protected final String entityType;

    public GenericRepository(IdentityExtractor<T> identityExtractor, String entityType) {
        this.items = new CopyOnWriteArrayList<>();
        this.identityExtractor = identityExtractor;
        this.entityType = entityType;
        logger.info("Created thread-safe repository for {}", entityType);
    }

    /**
     * Add an item to the repository (thread-safe)
     *
     * @throws InvalidDataException if item is null
     * @throws AlreadyExistsException if item with this identity already exists
     */
    public synchronized boolean add(T item) {
        if (item == null) {
            throw new InvalidDataException(entityType + " cannot be null");
        }

        String identity = identityExtractor.extractIdentity(item);

        if (findByIdentity(identity).isPresent()) {
            String errorMsg = String.format("%s already exists with identity: %s", entityType, identity);
            logger.error(errorMsg);
            throw new AlreadyExistsException(errorMsg);
        }

        items.add(item);
        logger.info("Added {}: {}", entityType, identity);
        return true;
    }

    public int addAll(Collection<T> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            return 0;
        }

        int addedCount = 0;
        for (T item : newItems) {
            try {
                if (add(item)) {
                    addedCount++;
                }
            } catch (AlreadyExistsException e) {
                logger.debug("Skipping duplicate: {}", e.getMessage());
            }
        }

        logger.info("Bulk added {} of {} items to {}", addedCount, newItems.size(), entityType);
        return addedCount;
    }

    /**
     * Remove an item using equals() method (thread-safe)
     */
    public synchronized boolean remove(T item) {
        if (item == null) {
            logger.warn("Attempted to remove null {}", entityType);
            return false;
        }

        boolean removed = items.remove(item);
        if (removed) {
            logger.info("Removed {}: {}", entityType, item);
        } else {
            logger.warn("Failed to remove {}: {}", entityType, item);
        }
        return removed;
    }

    /**
     * Remove by identity (thread-safe)
     */
    public synchronized boolean removeByIdentity(String identity) {
        if (identity == null) {
            logger.warn("Attempted to remove {} with null identity", entityType);
            return false;
        }

        Optional<T> itemToRemove = findByIdentityInternal(identity);

        if (itemToRemove.isPresent()) {
            boolean removed = items.remove(itemToRemove.get());
            if (removed) {
                logger.info("Removed {} by identity: {}", entityType, identity);
            }
            return removed;
        } else {
            logger.warn("No {} found with identity: {} to remove", entityType, identity);
            return false;
        }
    }

    /**
     * Update an existing item (thread-safe)
     * Finds item by identity extracted from newItem and replaces it
     *
     * @param newItem New item to replace with
     * @return true if updated, false if item not found
     * @throws InvalidDataException if newItem is null
     */
    public synchronized boolean update(T newItem) {
        if (newItem == null) {
            throw new InvalidDataException(entityType + " cannot be null");
        }

        String identity = identityExtractor.extractIdentity(newItem);
        Optional<T> existingItem = findByIdentityInternal(identity);

        if (existingItem.isEmpty()) {
            logger.warn("Cannot update: {} not found with identity: {}", entityType, identity);
            return false;
        }

        items.remove(existingItem.get());
        items.add(newItem);

        logger.info("Updated {}: {}", entityType, identity);
        return true;
    }

    /**
     * Check if repository contains an item using equals()
     */
    public boolean contains(T item) {
        return items.contains(item);
    }

    /**
     * Check if repository contains an item with given identity
     */
    public boolean containsIdentity(String identity) {
        return findByIdentityInternal(identity).isPresent();
    }

    /**
     * Find an object by its unique identity field
     */
    public Optional<T> findByIdentity(String identity) {
        if (identity == null) {
            logger.warn("Attempted to find {} with null identity", entityType);
            return Optional.empty();
        }

        Optional<T> result = items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();

        logger.debug("Find {} by identity '{}': {}", entityType, identity,
                result.isPresent() ? "found" : "not found");

        return result;
    }

    /**
     * Internal find method without logging (for use in synchronized blocks)
     */
    private Optional<T> findByIdentityInternal(String identity) {
        return items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();
    }

    public List<T> getAll() {
        logger.info("Retrieved all {} items. Count: {}", entityType, items.size());
        return new ArrayList<>(items);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public synchronized void clear() {
        int sizeBefore = items.size();
        items.clear();
        logger.info("Cleared repository. Removed {} {} items", sizeBefore, entityType);
    }

    /**
     * Sort items by identity using ascending or descending order.
     */
    public synchronized void sortByIdentity(boolean asc) {
        List<T> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(identityExtractor::extractIdentity));
        if (!asc) {
            Collections.reverse(sorted);
        }
        items.clear();
        items.addAll(sorted);
        logger.info("Sorted {} by identity in {} order", entityType, asc ? "ascending" : "descending");
    }

    /**
     * Alternative: sort using String order
     */
    public void sortByIdentity(String order) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        logger.debug("sortByIdentity called with order: '{}', interpreted as: {}", order, asc ? "ascending" : "descending");
        sortByIdentity(asc);
    }

    /**
     * Package-private method for testing
     */
    List<T> getItemsForTesting() {
        return new ArrayList<>(items);
    }
}