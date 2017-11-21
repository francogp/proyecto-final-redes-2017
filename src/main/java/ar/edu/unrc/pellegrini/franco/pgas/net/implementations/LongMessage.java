package ar.edu.unrc.pellegrini.franco.pgas.net.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.MessageType;

import java.net.InetAddress;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

public final
class LongMessage
        extends Message< Long > {
    public static final int MSG_BYTES_LENGHT       = 17;
    public static final int PARAMETER_1_BYTE_INDEX = 1;
    public static final int PARAMETER_2_BYTE_INDEX = 9;
    public static final int TYPE_BYTE_INDEX        = 0;

    public
    LongMessage(
            InetAddress address,
            int port,
            byte[] bytes
    ) {
        super(address, port, bytes);
    }

    public
    LongMessage(
            InetAddress address,
            int port,
            MessageType type,
            Long parameter1,
            Long parameter2
    ) {
        super(address, port, type, ( parameter1 == null ) ? 0L : parameter1, ( parameter2 == null ) ? 0L : parameter2);
    }

    @Override
    protected
    void initBytes() {
        this.bytes = new byte[MSG_BYTES_LENGHT];
        bytes[TYPE_BYTE_INDEX] = type.asByte();
        final byte[] param1 = longToBytes(parameter1);
        System.arraycopy(param1, 0, bytes, PARAMETER_1_BYTE_INDEX, PARAMETER_1_BYTE_INDEX + 8 - 1);
        final byte[] param2 = longToBytes(parameter2);
        System.arraycopy(param2, 0, bytes, PARAMETER_2_BYTE_INDEX, PARAMETER_2_BYTE_INDEX + 8 - 9);
    }

    @Override
    protected
    void initFromBytes( byte[] bytes ) {
        if ( bytes.length != MSG_BYTES_LENGHT ) {
            throw new IllegalArgumentException("Wrong bytes.length=" + bytes.length + ", must be " + MSG_BYTES_LENGHT);
        }
        type = MessageType.valueOf((char) bytes[TYPE_BYTE_INDEX]);
        parameter1 = bytesToLong(bytes, PARAMETER_1_BYTE_INDEX, PARAMETER_1_BYTE_INDEX + 8);
        parameter2 = bytesToLong(bytes, PARAMETER_2_BYTE_INDEX, PARAMETER_2_BYTE_INDEX + 8);
    }
}
