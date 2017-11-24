package ar.edu.unrc.pellegrini.franco.net;

public
class InvalidValueParameterException
        extends Exception {
    public
    InvalidValueParameterException() {
        super();
    }

    public
    InvalidValueParameterException( String message ) {
        super(message);
    }

    public
    InvalidValueParameterException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }

    public
    InvalidValueParameterException( Throwable cause ) {
        super(cause);
    }

    protected
    InvalidValueParameterException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
