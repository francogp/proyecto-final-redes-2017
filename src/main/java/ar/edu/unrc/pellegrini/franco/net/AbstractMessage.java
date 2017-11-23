package ar.edu.unrc.pellegrini.franco.net;

import java.net.InetAddress;
import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.END_MSG;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractMessage< I extends Comparable< I > >
        implements Message< I > {
    protected final InetAddress address;
    protected final int         port;
    protected       byte[]      bytes;
    protected       Long        indexParameter;
    protected       MessageType type;
    protected       I           valueParameter;

    protected
    AbstractMessage(

            final InetAddress address,
            final int port,
            final byte[] bytes
    ) {
        this.address = address;
        this.port = port;
        this.bytes = bytes;
        initFromBytes(bytes);
    }

    protected
    AbstractMessage(
            final InetAddress address,
            final int port,
            final MessageType type,
            final long indexParameter,
            final I valueParameter
    ) {
        this.address = address;
        this.port = port;
        this.type = type;
        if ( valueParameter == null ) {
            throw new IllegalArgumentException("parameters 1 and 2 cannot be null");
        }
        this.indexParameter = indexParameter;
        this.valueParameter = valueParameter;

        initBytes();
    }

    @SuppressWarnings( "RedundantIfStatement" )
    @Override
    public final
    boolean equals( final Object obj ) {
        if ( this == obj ) { return true; }
        if ( !( obj instanceof AbstractMessage ) ) { return false; }

        final AbstractMessage< ? > that = (AbstractMessage< ? >) obj;

        if ( port != that.port ) { return false; }
        if ( !address.equals(that.address) ) { return false; }
        if ( !Arrays.equals(bytes, that.bytes) ) { return false; }
        if ( ( indexParameter != null ) ? !indexParameter.equals(that.indexParameter) : ( that.indexParameter != null ) ) { return false; }
        if ( ( valueParameter != null ) ? !valueParameter.equals(that.valueParameter) : ( that.valueParameter != null ) ) { return false; }
        if ( type != that.type ) { return false; }

        return true;
    }

    @Override
    public final
    InetAddress getAddress() {
        return address;
    }

    @Override
    public final
    byte[] getBytes() {
        return bytes;
    }

    @Override
    public final
    Long getIndexParameter() {return indexParameter;}

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
    public final
    int hashCode() {
        int result = address.hashCode();
        result = ( 31 * result ) + port;
        result = ( 31 * result ) + Arrays.hashCode(bytes);
        result = ( 31 * result ) + ( ( indexParameter != null ) ? indexParameter.hashCode() : 0 );
        result = ( 31 * result ) + ( ( valueParameter != null ) ? valueParameter.hashCode() : 0 );
        result = ( 31 * result ) + type.hashCode();
        return result;
    }

    protected abstract
    void initBytes();

    protected abstract
    void initFromBytes( final byte[] bytes );

    @Override
    public final
    boolean isEndMessage() {
        return type == END_MSG;
    }

    @Override
    public final
    String toString() {
        return "Message{" + "address=" + address + ", indexParameter=" + indexParameter + ", valueParameter=" + valueParameter + ", port=" + port +
               ", type=" + type + ", bytes=" + Arrays.toString(bytes) + '}';
    }

}
