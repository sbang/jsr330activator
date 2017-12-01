package no.steria.osgi.jsr330activator;

public class Jsr330ActivatorException extends RuntimeException {
    private static final long serialVersionUID = -5439770395826105699L;

    public Jsr330ActivatorException() {
        super();
    }

    public Jsr330ActivatorException(String message) {
        super(message);
    }

    public Jsr330ActivatorException(Throwable cause) {
        super(cause);
    }

    public Jsr330ActivatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public Jsr330ActivatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
