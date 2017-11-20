package ar.edu.unrc.pellegrini.franco.net;

import java.net.InetAddress;

import static ar.edu.unrc.pellegrini.franco.net.Server.MSG_TYPE_END;

public
class Message {
    private final InetAddress address;
    private final int         port;
    private final String      value;

    public
    Message(
            InetAddress address,
            int port,
            String value
    ) {
        this.address = address;
        this.port = port;
        this.value = value;
    }

    public static
    Message newEndMessage() {
        return new Message(null, 0, MSG_TYPE_END);
    }

    @Override
    public
    boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( !( o instanceof Message ) ) { return false; }

        Message message = (Message) o;

        if ( isEndMessage() && message.isEndMessage() ) {return true;}

        if ( port != message.port ) { return false; }
        if ( !address.equals(message.address) ) { return false; }
        return value.equals(message.value);
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
        result = 31 * result + port;
        result = 31 * result + value.hashCode();
        return result;
    }

    public
    boolean isEndMessage() {
        return value.equals(MSG_TYPE_END);
    }

    @Override
    public
    String toString() {
        return "Message{" + "address=" + address + ", port=" + port + ", value='" + value + '\'' + '}';
    }
}
