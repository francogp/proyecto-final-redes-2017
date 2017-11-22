package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.InetAddress;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.*;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DoubleMessage
        extends AbstractMessage< Double > {
    public static final int DOUBLE_INDEX_PARAMETER_BYTE_INDEX = 1;
    public static final int DOUBLE_MSG_BYTES_LENGTH           = 17;
    public static final int DOUBLE_TYPE_BYTE_INDEX            = 0;
    public static final int DOUBLE_VALUE_PARAMETER_BYTE_INDEX = 9;

    public
    DoubleMessage(
            final InetAddress address,
            final int port,
            final byte[] bytes
    ) {
        super(address, port, bytes);
    }

    public
    DoubleMessage(
            final InetAddress address,
            final int port,
            final MessageType type,
            final Long indexParameter,
            final Double valueParameter
    ) {
        super(address, port, type, indexParameter, ( valueParameter == null ) ? 0.0d : valueParameter);
    }

    @Override
    protected
    void initBytes() {
        bytes = new byte[DOUBLE_MSG_BYTES_LENGTH];
        bytes[DOUBLE_TYPE_BYTE_INDEX] = type.asByte();
        final byte[] index = longToBytes(indexParameter);
        System.arraycopy(index, 0, bytes, DOUBLE_INDEX_PARAMETER_BYTE_INDEX, 8);
        final byte[] value = doubleToBytes(valueParameter);
        System.arraycopy(value, 0, bytes, DOUBLE_VALUE_PARAMETER_BYTE_INDEX, 8);
    }

    @Override
    protected
    void initFromBytes( final byte[] bytes ) {
        if ( bytes.length != DOUBLE_MSG_BYTES_LENGTH ) {
            throw new IllegalArgumentException("Wrong bytes.length=" + bytes.length + ", must be " + DOUBLE_MSG_BYTES_LENGTH);
        }
        type = MessageType.valueOf((char) bytes[DOUBLE_TYPE_BYTE_INDEX]);
        indexParameter = bytesToLong(bytes, DOUBLE_INDEX_PARAMETER_BYTE_INDEX, DOUBLE_INDEX_PARAMETER_BYTE_INDEX + 8);
        valueParameter = bytesToDouble(bytes, DOUBLE_VALUE_PARAMETER_BYTE_INDEX, DOUBLE_VALUE_PARAMETER_BYTE_INDEX + 8);
    }
}
