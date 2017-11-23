package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.InetAddress;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMessage
        extends AbstractMessage< Long > {
    public static final int LONG_INDEX_PARAMETER_BYTE_INDEX = 1;
    public static final int LONG_MSG_BYTES_LENGTH           = 17;
    public static final int LONG_TYPE_BYTE_INDEX            = 0;
    public static final int LONG_VALUE_PARAMETER_BYTE_INDEX = 9;

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
            final long indexParameter,
            final Long valueParameter
    ) {
        super(address, port, type, indexParameter, ( valueParameter == null ) ? 0L : valueParameter);
    }

    @Override
    protected
    void initBytes() {
        bytes = new byte[LONG_MSG_BYTES_LENGTH];
        bytes[LONG_TYPE_BYTE_INDEX] = type.asByte();
        final byte[] index = longToBytes(indexParameter);
        System.arraycopy(index, 0, bytes, LONG_INDEX_PARAMETER_BYTE_INDEX, 8);
        final byte[] value = longToBytes(valueParameter);
        System.arraycopy(value, 0, bytes, LONG_VALUE_PARAMETER_BYTE_INDEX, 8);
    }

    @Override
    protected
    void initFromBytes( final byte[] bytes ) {
        if ( bytes.length != LONG_MSG_BYTES_LENGTH ) {
            throw new IllegalArgumentException("Wrong bytes.length=" + bytes.length + ", must be " + LONG_MSG_BYTES_LENGTH);
        }
        type = MessageType.valueOf((char) bytes[LONG_TYPE_BYTE_INDEX]);
        indexParameter = bytesToLong(bytes, LONG_INDEX_PARAMETER_BYTE_INDEX, LONG_INDEX_PARAMETER_BYTE_INDEX + 8);
        valueParameter = bytesToLong(bytes, LONG_VALUE_PARAMETER_BYTE_INDEX, LONG_VALUE_PARAMETER_BYTE_INDEX + 8);
    }
}
