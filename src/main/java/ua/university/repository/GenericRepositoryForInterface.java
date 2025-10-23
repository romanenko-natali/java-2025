package ua.university.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenericRepositoryForInterface<T extends Identity> {

    private final List<T> items;
    private final String entityType;

    public GenericRepositoryForInterface(String entityType) {
        items = new ArrayList<>();
        this.entityType = entityType;
    }

    public boolean add(T item) {
        if (item == null) return false;
        if (findByIdentity(item.getIdentity()).isPresent()) {
            return false;
        }

        return items.add(item);
    }

    public Optional<T> findByIdentity(String identity) {
        return items.stream()
                .filter(i -> i.getIdentity().equals(identity))
                .findFirst();
    }

    public boolean containsIdentity(String identity) {
        return findByIdentity(identity).isPresent();
    }

    // other methods...
}
