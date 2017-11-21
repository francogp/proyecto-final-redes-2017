package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.InetAddress;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMessage
        extends Message< Long > {
    public static final int MSG_BYTES_LENGTH       = 17;
    public static final int PARAMETER_1_BYTE_INDEX = 1;
    public static final int PARAMETER_2_BYTE_INDEX = 9;
    public static final int TYPE_BYTE_INDEX        = 0;

    public
    LongMessage(
            final InetAddress address,
            final int port,
            final byte[] bytes
    ) {
        super(address, port, bytes);
    }

    public
    LongMessage(
            final InetAddress address,
            final int port,
            final MessageType type,
            final Long parameter1,
            final Long parameter2
    ) {
        super(address, port, type, ( parameter1 == null ) ? 0L : parameter1, ( parameter2 == null ) ? 0L : parameter2);
    }

    @Override
    protected
    void initBytes() {
        bytes = new byte[MSG_BYTES_LENGTH];
        bytes[TYPE_BYTE_INDEX] = type.asByte();
        final byte[] param1 = longToBytes(parameter1);
        System.arraycopy(param1, 0, bytes, PARAMETER_1_BYTE_INDEX, ( PARAMETER_1_BYTE_INDEX + 8 ) - 1);
        final byte[] param2 = longToBytes(parameter2);
        System.arraycopy(param2, 0, bytes, PARAMETER_2_BYTE_INDEX, ( PARAMETER_2_BYTE_INDEX + 8 ) - 9);
    }

    @Override
    protected
    void initFromBytes( final byte[] bytes ) {
        if ( bytes.length != MSG_BYTES_LENGTH ) {
            throw new IllegalArgumentException("Wrong bytes.length=" + bytes.length + ", must be " + MSG_BYTES_LENGTH);
        }
        type = MessageType.valueOf((char) bytes[TYPE_BYTE_INDEX]);
        parameter1 = bytesToLong(bytes, PARAMETER_1_BYTE_INDEX, PARAMETER_1_BYTE_INDEX + 8);
        parameter2 = bytesToLong(bytes, PARAMETER_2_BYTE_INDEX, PARAMETER_2_BYTE_INDEX + 8);
    }
}
