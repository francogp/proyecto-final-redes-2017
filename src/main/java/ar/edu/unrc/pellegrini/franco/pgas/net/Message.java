package ar.edu.unrc.pellegrini.franco.pgas.net;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class Message {
    public static final char AND_REDUCE_MSG         = 'A';
    public static final char BARRIER_MSG            = 'B';
    public static final char CONTINUE_MSG           = 'C';
    public static final char END_MSG                = 'E';
    public static final int  MSG_BYTES_LENGHT       = 17;
    public static final int  PARAMETER_1_BYTE_INDEX = 1;
    public static final int  PARAMETER_2_BYTE_INDEX = 9;
    public static final char READ_MSG               = 'R';
    public static final char READ_RESPONSE_MSG      = 'S';
    public static final int  TYPE_BYTE_INDEX        = 0;
    public static final char WRITE_MSG              = 'W';
    private final InetAddress address;
    private final byte[]      bytes;
    private final long        parameter1;
    private final long        parameter2;
    private final int         port;
    private final char        type;

    public
    Message(
            final InetAddress address,
            final int port,
            final byte[] bytes
    ) {
        this.address = address;
        this.port = port;
        if ( bytes.length != MSG_BYTES_LENGHT ) {
            throw new IllegalArgumentException("Wrong bytes.length=" + bytes.length + ", must be " + MSG_BYTES_LENGHT);
        }
        this.bytes = bytes;
        type = (char) bytes[TYPE_BYTE_INDEX];
        parameter1 = bytesToLong(bytes, PARAMETER_1_BYTE_INDEX, PARAMETER_1_BYTE_INDEX + 8);
        parameter2 = bytesToLong(bytes, PARAMETER_2_BYTE_INDEX, PARAMETER_2_BYTE_INDEX + 8);
    }

    public
    Message(
            final InetAddress address,
            final int port,
            final char type,
            final long parameter1
    ) {
        this(address, port, type, parameter1, 0L);
    }

    public
    Message(
            final InetAddress address,
            final int port,
            final char type
    ) {
        this(address, port, type, 0L, 0L);
    }

    public
    Message(
            final InetAddress address,
            final int port,
            final char type,
            final long parameter1,
            final long parameter2
    ) {
        this.address = address;
        this.port = port;
        this.type = type;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;

        this.bytes = new byte[MSG_BYTES_LENGHT];
        bytes[TYPE_BYTE_INDEX] = (byte) type;
        final byte[] param1 = longToBytes(parameter1);
        System.arraycopy(param1, 0, bytes, PARAMETER_1_BYTE_INDEX, PARAMETER_1_BYTE_INDEX + 8 - 1);
        final byte[] param2 = longToBytes(parameter2);
        System.arraycopy(param2, 0, bytes, PARAMETER_2_BYTE_INDEX, PARAMETER_2_BYTE_INDEX + 8 - 9);
    }

    public static
    Message defaultEndQueueMsg(
            final InetAddress address,
            final int port
    ) {
        return new Message(address, port, END_MSG, 0L, 0L);
    }

    public static
    Message defaultEndQueueMsg() {
        return new Message(null, 0, END_MSG, 0L, 0L);
    }

    public static
    List< Character > getMsgTypeList() {
        return List.of(AND_REDUCE_MSG, BARRIER_MSG, CONTINUE_MSG, END_MSG, READ_MSG, READ_RESPONSE_MSG, WRITE_MSG);
    }

    @Override
    public
    boolean equals( final Object o ) {
        if ( this == o ) { return true; }
        if ( !( o instanceof Message ) ) { return false; }

        final Message message = (Message) o;

        if ( port != message.port ) { return false; }
        if ( ( address != null ) ? !address.equals(message.address) : ( message.address != null ) ) { return false; }
        if ( !Arrays.equals(bytes, message.bytes) ) { return false; }

        return true;
    }

    public
    InetAddress getAddress() {
        return address;
    }

    public
    byte[] getBytes() {
        return bytes;
    }

    public
    long getParameter1() {
        return parameter1;
    }

    public
    long getParameter2() {
        return parameter2;
    }

    public
    int getPort() {
        return port;
    }

    public
    long getResponse() {
        return parameter1;
    }

    public
    char getType() {
        return type;
    }

    @Override
    public
    int hashCode() {
        int result = ( address != null ) ? address.hashCode() : 0;
        result = ( 31 * result ) + Arrays.hashCode(bytes);
        result = ( 31 * result ) + port;
        return result;
    }

    public
    boolean isEndMessage() {
        return type == END_MSG;
    }

    @Override
    public
    String toString() {
        return "Message{" + "address=" + address + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", port=" + port + ", type=" + type +
               ", bytes=" + Arrays.toString(bytes) + '}';
    }

}
