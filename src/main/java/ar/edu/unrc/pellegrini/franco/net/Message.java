package ar.edu.unrc.pellegrini.franco.net;

import java.net.InetAddress;
import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.END_MSG;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class Message< I extends Comparable< I > > {
    protected final InetAddress address;
    protected final int         port;
    protected       byte[]      bytes;
    protected       I           parameter1;
    protected       I           parameter2;
    protected       MessageType type;

    protected
    Message(

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
    Message(
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
    public
    boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( !( o instanceof Message ) ) { return false; }

        Message< ? > message = (Message< ? >) o;

        if ( port != message.port ) { return false; }
        if ( !address.equals(message.address) ) { return false; }
        if ( !Arrays.equals(bytes, message.bytes) ) { return false; }
        if ( parameter1 != null ? !parameter1.equals(message.parameter1) : message.parameter1 != null ) { return false; }
        if ( parameter2 != null ? !parameter2.equals(message.parameter2) : message.parameter2 != null ) { return false; }
        if ( type != message.type ) { return false; }

        return true;
    }

    @Override
    public
    int hashCode() {
        int result = address.hashCode();
        result = 31 * result + Arrays.hashCode(bytes);
        result = 31 * result + ( parameter1 != null ? parameter1.hashCode() : 0 );
        result = 31 * result + ( parameter2 != null ? parameter2.hashCode() : 0 );
        result = 31 * result + port;
        result = 31 * result + ( type != null ? type.hashCode() : 0 );
        return result;
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
    I getParameter1() {return parameter1;}

    public
    I getParameter2() {return parameter2;}

    public
    int getPort() {
        return port;
    }

    public
    I getResponse() {
        return parameter1;
    }

    public
    MessageType getType() {
        return type;
    }

    protected abstract
    void initBytes();

    protected abstract
    void initFromBytes( final byte[] bytes );

    public
    boolean isEndMessage() {
        return type == END_MSG;
    }

    @Override
    public
    String toString() {
        return "Message{" + "address=" + address + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", port=" + port +
               ", type=" + type + ", bytes=" + Arrays.toString(bytes) + '}';
    }

}
