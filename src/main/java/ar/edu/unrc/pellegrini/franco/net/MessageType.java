package ar.edu.unrc.pellegrini.franco.net;

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
            case 'B':
                return BARRIER_MSG;
            case 'C':
                return CONTINUE_BARRIER_MSG;
            case 'V':
                return CONTINUE_AND_REDUCE_MSG; //TODO es necesario o se puede reusar R?
            case 'E':
                return END_MSG;
            case 'R':
                return READ_MSG;
            case 'S':
                return READ_RESPONSE_MSG; //TODO es necesario o se puede reusar R?
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
}
