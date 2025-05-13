package util;

import classes.devices.Thermostat;
import exceptions.NotFoundInRegistryException;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public HashMap<UUID, T> getItems() {
        return items;
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

    public <T> Set<T> getItemByClass(Class<T> clazz) {
        return items.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toSet());
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
