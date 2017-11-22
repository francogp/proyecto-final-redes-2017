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
    protected       I           parameter1;
    protected       I           parameter2;
    protected       MessageType type;

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
            final I parameter1,
            final I parameter2
    ) {
        this.address = address;
        this.port = port;
        this.type = type;
        if ( ( parameter1 == null ) || ( parameter2 == null ) ) {
            throw new IllegalArgumentException("parameters 1 and 2 cannot be null");
        }
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;

        initBytes();
    }

    @Override
    public final
    boolean equals( final Object obj ) {
        if ( this == obj ) { return true; }
        if ( !( obj instanceof AbstractMessage ) ) { return false; }

        final AbstractMessage< ? > that = (AbstractMessage< ? >) obj;

        if ( port != that.port ) { return false; }
        if ( !address.equals(that.address) ) { return false; }
        if ( !Arrays.equals(bytes, that.bytes) ) { return false; }
        if ( ( parameter1 != null ) ? !parameter1.equals(that.parameter1) : ( that.parameter1 != null ) ) { return false; }
        if ( ( parameter2 != null ) ? !parameter2.equals(that.parameter2) : ( that.parameter2 != null ) ) { return false; }
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
    I getParameter1() {return parameter1;}

    @Override
    public final
    I getParameter2() {return parameter2;}

    @Override
    public final
    int getPort() {
        return port;
    }

    @Override
    public final
    I getResponse() {
        return parameter1;
    }

    @Override
    public final
    MessageType getType() {
        return type;
    }

    @Override
    public final
    int hashCode() {
        int result = address.hashCode();
        result = ( 31 * result ) + port;
        result = ( 31 * result ) + Arrays.hashCode(bytes);
        result = ( 31 * result ) + ( ( parameter1 != null ) ? parameter1.hashCode() : 0 );
        result = ( 31 * result ) + ( ( parameter2 != null ) ? parameter2.hashCode() : 0 );
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
        return "Message{" + "address=" + address + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", port=" + port + ", type=" + type +
               ", bytes=" + Arrays.toString(bytes) + '}';
    }

}
