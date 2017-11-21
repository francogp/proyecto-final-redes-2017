package ar.edu.unrc.pellegrini.franco.pgas.net;

public
enum MessageType {
    AND_REDUCE_MSG('A'),
    BARRIER_MSG('B'),
    CONTINUE_MSG('C'),
    END_MSG('E'),
    READ_MSG('R'),
    READ_RESPONSE_MSG('S'),
    WRITE_MSG('W');

    private final char charType;

    MessageType(
            char charType
    ) {
        this.charType = charType;
    }

    public static
    MessageType valueOf( char type ) {
        //TODO optimize this! search for google
        switch ( type ) {
            case 'A': {
                return AND_REDUCE_MSG;
            }
            case 'B': {
                return BARRIER_MSG;
            }
            case 'C': {
                return CONTINUE_MSG;
            }
            case 'E': {
                return END_MSG;
            }
            case 'R': {
                return READ_MSG;
            }
            case 'S': {
                return READ_RESPONSE_MSG;
            }
            case 'W': {
                return WRITE_MSG;
            }
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
