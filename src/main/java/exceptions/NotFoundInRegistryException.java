package exceptions;

import java.util.UUID;

public class NotFoundInRegistryException extends RuntimeException {
    public NotFoundInRegistryException(UUID id,String itemType) {
        super(String.format("%s with ID '%s' not found.", itemType, id));
    }
}
