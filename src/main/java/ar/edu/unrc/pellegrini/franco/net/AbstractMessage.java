package ar.edu.unrc.pellegrini.franco.net;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.END_MSG;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.*;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractMessage< I >
        implements Message< I > {
    public static final int INDEX_PARAMETER_BYTE_INDEX  = 5;
    public static final int INDEX_PARAMETER_BYTE_LENGTH = 8;
    public static final int PGAS_NAME_BYTE_INDEX        = 0;
    public static final int PGAS_NAME_BYTE_LENGTH       = 4;
    public static final int TYPE_BYTE_INDEX             = 4;
    public static final int TYPE_BYTE_LENGTH            = 1;
    public static final int PAYLOAD_PREFIX_LENGTH       = PGAS_NAME_BYTE_LENGTH + TYPE_BYTE_LENGTH + INDEX_PARAMETER_BYTE_LENGTH;
    public static final int VALUE_PARAMETER_BYTE_INDEX  = 13;
    protected InetAddress address;
    protected byte[]      asBytes;
    protected long        indexParameter;
    protected int         pgasName;
    protected int         port;
    protected MessageType type;
    protected I           valueParameter;

    @SuppressWarnings( "RedundantIfStatement" )
    @Override
    public
    boolean equals( final Object o ) {
        if ( this == o ) { return true; }
        if ( !( o instanceof AbstractMessage ) ) { return false; }

        final AbstractMessage< ? > that = (AbstractMessage< ? >) o;

        if ( indexParameter != that.indexParameter ) { return false; }
        if ( pgasName != that.pgasName ) { return false; }
        if ( port != that.port ) { return false; }
        if ( !address.equals(that.address) ) { return false; }
        if ( !Arrays.equals(asBytes, that.asBytes) ) { return false; }
        if ( type != that.type ) { return false; }
        if ( ( valueParameter != null ) ? !valueParameter.equals(that.valueParameter) : ( that.valueParameter != null ) ) { return false; }

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
    Long getIndexParameter() {return indexParameter;}

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
    I getValueParameter() {return valueParameter;}

    @Override
    public
    int hashCode() {
        int result = address.hashCode();
        result = ( 31 * result ) + Arrays.hashCode(asBytes);
        result = ( 31 * result ) + (int) ( indexParameter ^ ( indexParameter >>> 32 ) );
        result = ( 31 * result ) + pgasName;
        result = ( 31 * result ) + port;
        result = ( 31 * result ) + type.hashCode();
        result = ( 31 * result ) + ( ( valueParameter != null ) ? valueParameter.hashCode() : 0 );
        return result;
    }

    public
    void initUsing(
            final int pgasName,
            final InetAddress address,
            final int port,
            final MessageType type,
            final long indexParameter,
            final I valueParameter
    )
            throws InvalidValueParameterException {
        this.pgasName = pgasName;
        this.address = address;
        this.port = port;
        this.type = type;
        this.indexParameter = indexParameter;
        this.valueParameter = valueParameter;

        asBytes = new byte[PAYLOAD_PREFIX_LENGTH + getValueByteLength()];
        final byte[] pgasNameBytes = integerToBytes(pgasName);
        assert pgasNameBytes.length == PGAS_NAME_BYTE_LENGTH;
        System.arraycopy(pgasNameBytes, 0, asBytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_LENGTH);
        asBytes[TYPE_BYTE_INDEX] = type.asByte();
        final byte[] parameterBytes = longToBytes(indexParameter);
        assert parameterBytes.length == INDEX_PARAMETER_BYTE_LENGTH;
        System.arraycopy(parameterBytes, 0, asBytes, INDEX_PARAMETER_BYTE_INDEX, INDEX_PARAMETER_BYTE_LENGTH);
        initValueInBytes();
    }

    @Override
    public
    void initUsing( final DatagramPacket packet )
            throws InvalidValueParameterException {
        address = packet.getAddress();
        port = packet.getPort();
        asBytes = packet.getData();
        final int preferedLength = PAYLOAD_PREFIX_LENGTH + getValueByteLength();
        if ( asBytes.length != preferedLength ) {
            throw new IllegalArgumentException("Wrong asBytes.length=" + asBytes.length + ", must be " + preferedLength);
        }
        pgasName = bytesToInteger(asBytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_INDEX + PGAS_NAME_BYTE_LENGTH);
        type = MessageType.valueOf((char) asBytes[TYPE_BYTE_INDEX]);
        indexParameter = bytesToLong(asBytes, INDEX_PARAMETER_BYTE_INDEX, INDEX_PARAMETER_BYTE_INDEX + INDEX_PARAMETER_BYTE_LENGTH);
        initValueFromBytes(asBytes);
    }

    protected abstract
    void initValueFromBytes( final byte[] bytes )
            throws InvalidValueParameterException;

    protected abstract
    void initValueInBytes()
            throws InvalidValueParameterException;

    @Override
    public final
    boolean isEndMessage() {
        return type == END_MSG;
    }

    @Override
    public final
    String toString() {
        return "Message{" + "address=" + address + ", indexParameter=" + indexParameter + ", valueParameter=" + valueParameter + ", port=" + port +
               ", type=" + type + ", asBytes=" + Arrays.toString(asBytes) + '}';
    }

}
