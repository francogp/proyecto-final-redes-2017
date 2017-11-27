package ar.edu.unrc.pellegrini.franco.net.exceptions;

public
class InvalidValueParameterException
        extends Exception {
    private static final long serialVersionUID = 3720148999557270116L;

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
