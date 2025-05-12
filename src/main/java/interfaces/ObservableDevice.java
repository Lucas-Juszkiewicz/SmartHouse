package interfaces;

public interface ObservableDevice<T> {
    void addObserver(T observer);
    void removeObserver(T observer);
    void notifyObservers();
}
