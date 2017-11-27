package ar.edu.unrc.pellegrini.franco.net;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.END_MSG;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.*;

public abstract
class AbstractMessage< I >
        implements Message< I > {
    /**
     * byte index of the index message parameter.
     */
    public static final int         INDEX_PARAMETER_BYTE_INDEX  = 5;
    /**
     * byte length of the index message parameter.
     */
    public static final int         INDEX_PARAMETER_BYTE_LENGTH = 8;
    /**
     * byte index of the PGAS name.
     */
    public static final int         PGAS_NAME_BYTE_INDEX        = 0;
    /**
     * byte length of the PGAS name.
     */
    public static final int         PGAS_NAME_BYTE_LENGTH       = 4;
    /**
     * byte index of the message type name.
     */
    public static final int         TYPE_BYTE_INDEX             = 4;
    /**
     * byte length of the message type name.
     */
    public static final int         TYPE_BYTE_LENGTH            = 1;
    /**
     * total byte length of the message without the vale.
     */
    public static final int         PAYLOAD_PREFIX_LENGTH       = PGAS_NAME_BYTE_LENGTH + TYPE_BYTE_LENGTH + INDEX_PARAMETER_BYTE_LENGTH;
    /**
     * byte index of the value message parameter.
     */
    public static final int         VALUE_PARAMETER_BYTE_INDEX  = 13;
    protected           InetAddress address                     = null;
    protected           byte[]      asBytes                     = null;
    protected           long        index                       = 0L;
    protected           int         pgasName                    = 0;
    protected           int         port                        = 0;
    protected           MessageType type                        = null;
    protected           I           value                       = null;

    @SuppressWarnings( "RedundantIfStatement" )
    @Override
    public
    boolean equals( final Object o ) {
        if ( this == o ) { return true; }
        if ( !( o instanceof AbstractMessage ) ) { return false; }

        final AbstractMessage< ? > that = (AbstractMessage< ? >) o;

        if ( index != that.index ) { return false; }
        if ( pgasName != that.pgasName ) { return false; }
        if ( port != that.port ) { return false; }
        if ( !address.equals(that.address) ) { return false; }
        if ( !Arrays.equals(asBytes, that.asBytes) ) { return false; }
        if ( type != that.type ) { return false; }
        if ( ( value != null ) ? !value.equals(that.value) : ( that.value != null ) ) { return false; }

        return true;
    }

    @Override
    public final
    InetAddress getAddress() {
        return address;
    }

    @Override
    public final
    byte[] getAsBytes() {
        return asBytes;
    }

    @Override
    public final
    long getIndex() {return index;}

    @Override
    public final
    int getPgasName() {
        return pgasName;
    }

    @Override
    public final
    int getPort() {
        return port;
    }

    @Override
    public final
    MessageType getType() {
        return type;
    }

    @Override
    public final
    I getValue() {return value;}

    @Override
    public
    int hashCode() {
        int result = address.hashCode();
        result = ( 31 * result ) + Arrays.hashCode(asBytes);
        result = ( 31 * result ) + (int) ( index ^ ( index >>> 32 ) );
        result = ( 31 * result ) + pgasName;
        result = ( 31 * result ) + port;
        result = ( 31 * result ) + type.hashCode();
        result = ( 31 * result ) + ( ( value != null ) ? value.hashCode() : 0 );
        return result;
    }

    /**
     * Initialize {@code asBytes} from {@code value}
     *
     * @throws InvalidValueParameterException an error trying to parse a value into data bytes.
     */
    protected abstract
    void initBytesFromValue()
            throws InvalidValueParameterException;

    public final
    void initUsing(
            final int pgasName,
            final InetAddress address,
            final int port,
            final MessageType type,
            final long index,
            final I value
    )
            throws InvalidValueParameterException {
        this.pgasName = pgasName;
        this.address = address;
        this.port = port;
        this.type = type;
        this.index = index;
        this.value = value;

        asBytes = new byte[PAYLOAD_PREFIX_LENGTH + getValueByteLength()];
        final byte[] pgasNameBytes = integerToBytes(pgasName);
        assert pgasNameBytes.length == PGAS_NAME_BYTE_LENGTH;
        System.arraycopy(pgasNameBytes, 0, asBytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_LENGTH);
        asBytes[TYPE_BYTE_INDEX] = type.asByte();
        final byte[] parameterBytes = longToBytes(index);
        assert parameterBytes.length == INDEX_PARAMETER_BYTE_LENGTH;
        System.arraycopy(parameterBytes, 0, asBytes, INDEX_PARAMETER_BYTE_INDEX, INDEX_PARAMETER_BYTE_LENGTH);
        initBytesFromValue();
    }

    @Override
    public final
    void initUsing( final DatagramPacket packet )
            throws InvalidValueParameterException {
        address = packet.getAddress();
        port = packet.getPort();
        asBytes = packet.getData();
        final int preferredLength = PAYLOAD_PREFIX_LENGTH + getValueByteLength();
        if ( asBytes.length != preferredLength ) {
            throw new IllegalArgumentException("Wrong asBytes.length=" + asBytes.length + ", must be " + preferredLength);
        }
        pgasName = bytesToInteger(asBytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_INDEX + PGAS_NAME_BYTE_LENGTH);
        type = MessageType.valueOf((char) asBytes[TYPE_BYTE_INDEX]);
        index = bytesToLong(asBytes, INDEX_PARAMETER_BYTE_INDEX, INDEX_PARAMETER_BYTE_INDEX + INDEX_PARAMETER_BYTE_LENGTH);
        initValueFromBytes(asBytes);
    }

    /**
     * @param bytes complete message in byte[] to be parsed into the message value.
     *
     * @throws InvalidValueParameterException an error trying to parse byte[] into a value.
     */
    protected abstract
    void initValueFromBytes( final byte[] bytes )
            throws InvalidValueParameterException;

    @Override
    public final
    boolean isEndMessage() {
        return type == END_MSG;
    }

    @Override
    public
    String toString() {
        return "AbstractMessage{" + "address=" + address + ", index=" + index + ", pgasName=" + pgasName + ", port=" + port + ", type=" + type +
               ", value=" + value + ", asBytes=" + Arrays.toString(asBytes) + '}';
    }
}
