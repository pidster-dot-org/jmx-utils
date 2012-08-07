package org.pidster.util.jmx;

public class ProxyAttributeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProxyAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyAttributeException(String message) {
        super(message);
    }

    public ProxyAttributeException(Throwable cause) {
        super(cause);
    }

}
