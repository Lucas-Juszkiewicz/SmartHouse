package util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Rule<T> {
    private final Predicate<T> condition;
    private final Consumer<T> action;
    private final T device;

    public Rule(Predicate<T> condition, Consumer<T> action, T device) {
        this.condition = condition;
        this.action = action;
        this.device = device;
    }

    public void checkAndExecute() {
        if (condition.test(device)) {
            action.accept(device);
        }
    }
}
