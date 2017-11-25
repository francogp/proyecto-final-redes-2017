package ar.edu.unrc.pellegrini.franco.net;

import java.util.Set;

public
enum MessageType {
    AND_REDUCE_MSG('A'),
    BARRIER_MSG('B'),
    CONTINUE_BARRIER_MSG('C'),
    CONTINUE_AND_REDUCE_MSG('V'),
    END_MSG('E'),
    READ_MSG('R'),
    READ_RESPONSE_MSG('S'),
    WRITE_MSG('W');

    public static final Set< MessageType > MIDDLEWARE_MESSAGES =
            Set.of(AND_REDUCE_MSG, BARRIER_MSG, CONTINUE_BARRIER_MSG, CONTINUE_AND_REDUCE_MSG, END_MSG);
    public static final Set< MessageType > PROCESS_MESSAGES    = Set.of(READ_MSG, READ_RESPONSE_MSG, WRITE_MSG);

    private final char charType;

    MessageType(
            final char charType
    ) {
        this.charType = charType;
    }

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

    public
    byte asByte() {
        return (byte) charType;
    }

    public
    char asChar() {
        return charType;
    }

    public
    boolean isMiddlewareMessageType() {
        return MIDDLEWARE_MESSAGES.contains(this);
    }
}
