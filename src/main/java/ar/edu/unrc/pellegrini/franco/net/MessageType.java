package ar.edu.unrc.pellegrini.franco.net;

import java.util.Set;

/**
 * Message types supported by the PGAS middleware.
 */
public
enum MessageType {
    /**
     * And reduce barrier.
     */
    AND_REDUCE_MSG('A'),
    /**
     * Barrier.
     */
    BARRIER_MSG('B'),
    /**
     * Used to wait this message to continue from a barrier.
     */
    CONTINUE_BARRIER_MSG('C'),
    /**
     * Used to wait this message to continue from an and reduce.
     */
    CONTINUE_AND_REDUCE_MSG('V'),
    /**
     * Close the listener from the middleware.
     */
    END_MSG('E'),
    /**
     * Read a value from the PGAS.
     */
    READ_MSG('R'),
    /**
     * Wait for the requested read value.
     */
    READ_RESPONSE_MSG('S'),
    /**
     * Write a value into a PGAS.
     */
    WRITE_MSG('W');

    /**
     * All the messages supported and processed by the middleware
     */
    public static final Set< MessageType > MIDDLEWARE_MESSAGES =
            Set.of(AND_REDUCE_MSG, BARRIER_MSG, CONTINUE_BARRIER_MSG, CONTINUE_AND_REDUCE_MSG, END_MSG);

    /**
     * All the messages supported and processed by a process.
     */
    public static final Set< MessageType > PROCESS_MESSAGES = Set.of(READ_MSG, READ_RESPONSE_MSG, WRITE_MSG);

    private final char charType;

    MessageType(
            final char charType
    ) {
        this.charType = charType;
    }

    /**
     * @param type to be parsed.
     *
     * @return a {@link MessageType} parsed from a char.
     */
    public static
    MessageType valueOf( final char type ) {
        switch ( type ) {
            case 'A':
                return AND_REDUCE_MSG;
            case 'V':
                return CONTINUE_AND_REDUCE_MSG;
            case 'B':
                return BARRIER_MSG;
            case 'C':
                return CONTINUE_BARRIER_MSG;
            case 'E':
                return END_MSG;
            case 'R':
                return READ_MSG;
            case 'S':
                return READ_RESPONSE_MSG;
            case 'W':
                return WRITE_MSG;
            default:
                throw new IllegalArgumentException("unknown charType " + type);
        }
    }

    /**
     * @return byte representation of the {@link MessageType}
     */
    public
    byte asByte() {
        return (byte) charType;
    }

    /**
     * @return char representation of the {@link MessageType}
     */
    public
    char asChar() {
        return charType;
    }

    /**
     * @return true if the {@link MessageType} is for a middleware only.
     */
    public
    boolean isMiddlewareMessageType() {
        return MIDDLEWARE_MESSAGES.contains(this);
    }
}
