package ar.edu.unrc.pellegrini.franco.pgas.net;

import java.net.InetAddress;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class Message {
    public static final String MSG_TYPE_END = "end";
    private final InetAddress address;
    private final String[]    arguments;
    private final int         port;
    private final String      value;

    public
    Message(
            final InetAddress address,
            final int port,
            final String value
    ) {
        this.address = address;
        this.port = port;
        this.value = value;
        arguments = value.split(":");
    }

    public static
    Message newEndMessage() {
        return new Message(null, 0, MSG_TYPE_END);
    }

    @Override
    public
    boolean equals( final Object o ) {
        if ( this == o ) { return true; }
        if ( !( o instanceof Message ) ) { return false; }

        final Message message = (Message) o;

        return ( isEndMessage() && message.isEndMessage() ) ||
               ( ( port == message.port ) && address.equals(message.address) && value.equals(message.value) );

    }

    public
    InetAddress getAddress() {
        return address;
    }

    public
    int getPort() {
        return port;
    }

    public
    String getValue() {
        return value;
    }

    @Override
    public
    int hashCode() {
        int result = address.hashCode();
        result = ( 31 * result ) + port;
        result = ( 31 * result ) + value.hashCode();
        return result;
    }

    public
    boolean isEndMessage() {
        return value.equals(MSG_TYPE_END);
    }

    public
    Long returnArgumentAsLong( final int argument ) {
        return Long.parseLong(arguments[argument]);
    }

    public
    String returnArgumentAsString( final int argument ) {
        return arguments[argument];
    }

    @Override
    public
    String toString() {
        return "Message{" + "address=" + address + ", port=" + port + ", value='" + value + '\'' + '}';
    }
}
