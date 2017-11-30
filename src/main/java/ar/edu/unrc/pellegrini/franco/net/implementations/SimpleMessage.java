package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.END_MSG;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.*;

public final
class SimpleMessage
        implements Message {
    /**
     * byte length of the index message parameter.
     */
    public static final int         INDEX_BYTE_LENGTH      = 8;
    /**
     * byte index of the PGAS name.
     */
    public static final int         PGAS_NAME_BYTE_INDEX   = 0;
    /**
     * byte length of the PGAS name.
     */
    public static final int         PGAS_NAME_BYTE_LENGTH  = 4;
    /**
     * byte index of the message type name.
     */
    public static final int         TYPE_BYTE_INDEX        = PGAS_NAME_BYTE_INDEX + PGAS_NAME_BYTE_LENGTH;//4;
    /**
     * byte length of the message type name.
     */
    public static final int         TYPE_BYTE_LENGTH       = 1;
    /**
     * byte index of the index message parameter.
     */
    public static final int         INDEX_BYTE_INDEX       = TYPE_BYTE_INDEX + TYPE_BYTE_LENGTH;//5;
    /**
     * byte index of the value message parameter.
     */
    public static final int         VALUE_DATA_SIZE_INDEX  = INDEX_BYTE_INDEX + INDEX_BYTE_LENGTH; //13
    /**
     * byte index of the value message parameter.
     */
    public static final int         VALUE_DATA_SIZE_LENGTH = 4;
    /**
     * total byte length of the message without the vale.
     */
    public static final int         PAYLOAD_PREFIX_LENGTH  =
            PGAS_NAME_BYTE_LENGTH + TYPE_BYTE_LENGTH + INDEX_BYTE_LENGTH + VALUE_DATA_SIZE_LENGTH;
    /**
     * byte index of the value message parameter.
     */
    public static final int         VALUE_DATA_BYTE_INDEX  = VALUE_DATA_SIZE_INDEX + VALUE_DATA_SIZE_LENGTH; //17
    private             InetAddress address                = null;
    private             byte[]      asBytes                = null;
    private             long        index                  = 0L;
    private             int         pgasName               = 0;
    private             int         port                   = 0;
    private             MessageType type                   = null;
    private             byte[]      valueAsBytes           = null;
    private             int         valueBytesSize         = Integer.MIN_VALUE;

    @SuppressWarnings( "SimplifiableIfStatement" )
    @Override
    public
    boolean equals( final Object o ) {
        if ( this == o ) { return true; }
        if ( !( o instanceof SimpleMessage ) ) { return false; }

        final SimpleMessage that = (SimpleMessage) o;

        if ( index != that.index ) { return false; }
        if ( pgasName != that.pgasName ) { return false; }
        if ( port != that.port ) { return false; }
        if ( valueBytesSize != that.valueBytesSize ) { return false; }
        if ( !address.equals(that.address) ) { return false; }
        if ( !Arrays.equals(asBytes, that.asBytes) ) { return false; }
        if ( type != that.type ) { return false; }
        return Arrays.equals(valueAsBytes, that.valueAsBytes);
    }

    @Override
    public
    InetAddress getAddress() {
        return address;
    }

    @Override
    public
    long getIndex() {return index;}

    @Override
    public
    byte[] getMessageAsBytes() {
        return asBytes;
    }

    @Override
    public
    int getPgasName() {
        return pgasName;
    }

    @Override
    public
    int getPort() {
        return port;
    }

    @Override
    public
    MessageType getType() {
        return type;
    }

    @Override
    public
    byte[] getValueAsBytes() {return valueAsBytes;}

    public
    int getValueBytesSize() {
        return valueBytesSize;
    }

    @Override
    public
    int hashCode() {
        int result = address.hashCode();
        result = ( 31 * result ) + Arrays.hashCode(asBytes);
        result = ( 31 * result ) + (int) ( index ^ ( index >>> 32 ) );
        result = ( 31 * result ) + pgasName;
        result = ( 31 * result ) + port;
        result = ( 31 * result ) + type.hashCode();
        result = ( 31 * result ) + Arrays.hashCode(valueAsBytes);
        result = ( 31 * result ) + valueBytesSize;
        return result;
    }

    public
    void initUsing(
            final int pgasName,
            final InetAddress address,
            final int port,
            final MessageType type,
            final long index,
            final int valueBytesSize,
            final byte[] valueAsBytes
    ) {
        this.pgasName = pgasName;
        this.address = address;
        this.port = port;
        this.type = type;
        this.index = index;
        this.valueBytesSize = valueBytesSize;
        this.valueAsBytes = valueAsBytes;

        asBytes = new byte[PAYLOAD_PREFIX_LENGTH + valueBytesSize];
        final byte[] pgasNameBytes = integerToBytes(pgasName);
        assert pgasNameBytes.length == PGAS_NAME_BYTE_LENGTH;
        System.arraycopy(pgasNameBytes, 0, asBytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_LENGTH);
        asBytes[TYPE_BYTE_INDEX] = type.asByte();
        final byte[] indexBytes = longToBytes(index);
        assert indexBytes.length == INDEX_BYTE_LENGTH;
        System.arraycopy(indexBytes, 0, asBytes, INDEX_BYTE_INDEX, INDEX_BYTE_LENGTH);
        final byte[] valueBytesSizeAsBytes = integerToBytes(valueBytesSize);
        assert valueBytesSizeAsBytes.length == VALUE_DATA_SIZE_LENGTH;
        System.arraycopy(valueBytesSizeAsBytes, 0, asBytes, VALUE_DATA_SIZE_INDEX, VALUE_DATA_SIZE_LENGTH);
        if ( ( valueBytesSize > 0 ) && ( valueAsBytes != null ) ) {
            System.arraycopy(valueAsBytes, 0, asBytes, VALUE_DATA_BYTE_INDEX, valueBytesSize);
        }
    }

    @Override
    public
    void initUsing( final DatagramPacket packet ) {
        address = packet.getAddress();
        port = packet.getPort();
        asBytes = packet.getData();
        if ( asBytes.length <= PAYLOAD_PREFIX_LENGTH ) {
            throw new IllegalArgumentException(
                    "Wrong asBytes.length=" + asBytes.length + ", must be at least " + PAYLOAD_PREFIX_LENGTH);
        }
        pgasName = bytesToInteger(asBytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_INDEX + PGAS_NAME_BYTE_LENGTH);
        type = MessageType.valueOf((char) asBytes[TYPE_BYTE_INDEX]);
        index = bytesToLong(asBytes, INDEX_BYTE_INDEX, INDEX_BYTE_INDEX + INDEX_BYTE_LENGTH);
        valueBytesSize = bytesToInteger(asBytes, VALUE_DATA_SIZE_INDEX, VALUE_DATA_SIZE_INDEX + VALUE_DATA_SIZE_LENGTH);
        if ( valueBytesSize == 0 ) {
            valueAsBytes = null;
        } else {
            valueAsBytes = new byte[valueBytesSize];
            System.arraycopy(asBytes, VALUE_DATA_BYTE_INDEX, valueAsBytes, 0, valueBytesSize);
        }
    }

    @Override
    public
    boolean isEndMessage() {
        return type == END_MSG;
    }

    @Override
    public
    String toString() {
        return "SimpleMessage{" + "address=" + address + ", index=" + index + ", pgasName=" + pgasName + ", port=" + port +
               ", type=" + type + ", valueAsBytes=" + Arrays.toString(valueAsBytes) + ", valueBytesSize=" + valueBytesSize +
               ", asBytes=" + Arrays.toString(asBytes) + '}';
    }

}
