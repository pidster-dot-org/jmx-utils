package org.pidster.util.jmx;


public class MBeanJSONException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MBeanJSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public MBeanJSONException(String message) {
        super(message);
    }

    public MBeanJSONException(Throwable cause) {
        super(cause);
    }

}
