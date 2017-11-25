package ar.edu.unrc.pellegrini.franco.net;

public
class InvalidValueParameterException
        extends Exception {
    public
    InvalidValueParameterException() {
    }

    public
    InvalidValueParameterException( final String message ) {
        super(message);
    }

    public
    InvalidValueParameterException(
            final String message,
            final Throwable cause
    ) {
        super(message, cause);
    }

    public
    InvalidValueParameterException( final Throwable cause ) {
        super(cause);
    }

    protected
    InvalidValueParameterException(
            final String message,
            final Throwable cause,
            final boolean enableSuppression,
            final boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
