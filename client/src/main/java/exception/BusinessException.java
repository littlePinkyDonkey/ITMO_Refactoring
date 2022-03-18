package exception;

import java.io.IOException;

public class BusinessException extends IOException {
    public BusinessException(final String message) {
        super(message);
    }
}
