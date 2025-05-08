package util;

import exceptions.NotFoundInRegistryException;

import java.util.HashMap;
import java.util.UUID;

public abstract class Registry<T> {
    private final HashMap<UUID, T> items = new HashMap<>();
    private final String itemClass;

    public Registry(Class<T> itemClass) {
        this.itemClass = itemClass.getSimpleName();
    }

    public UUID generateId() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (items.containsKey(id));
        return id;
    }

    public String addItem(UUID id, T item) {
        items.put(id, item);
        return String.format("%s added", this.itemClass);
    }

    public T getItem(UUID id) {
        T item = items.get(id);
        if (item == null) {
            throw new NotFoundInRegistryException(id, itemClass);
        }
        return item;
    }

    public String updateItem(UUID id, T item) {
        items.put(id, item);
        return String.format("%s updated", this.itemClass);
    }

    public String removeItem(UUID id) {
        items.remove(id);
        return String.format("%s removed", this.itemClass);
    }

    @Override
    public String toString() {
        StringBuilder itemsStr = new StringBuilder();
        for(T item : items.values()){
            itemsStr.append(item.toString()).append("\n");
        }
        return String.format("""
                %sRegistry
                %s
                """, this.itemClass, itemsStr);
    }
}
