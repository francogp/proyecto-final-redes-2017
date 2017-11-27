package ar.edu.unrc.pellegrini.franco.net;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * The PGAS communicate between processes by {@link Message}. A {@link ar.edu.unrc.pellegrini.franco.net.implementations.Listener} dispatch them.
 *
 * @param <I> value type carried by the Message.
 */
public
interface Message< I > {

    /**
     * @return source address of the message
     */
    InetAddress getAddress();

    /**
     * @return message as a byte[] representation
     */
    byte[] getAsBytes();

    /**
     * @return an index transported by the message, usually used by the value.  Not always used.
     */
    long getIndex();

    /**
     * @return the PGAS name.
     */
    int getPgasName();

    /**
     * @return source address port of the message
     */
    int getPort();

    /**
     * @return the type of the message.
     */
    MessageType getType();

    /**
     * @return a value transported by the message. Not always used.
     */
    I getValue();

    /**
     * @return byte[] representation length of the message.
     */
    int getValueByteLength();

    /**
     * @param packet initialize a message using a {@link DatagramPacket} data.
     *
     * @throws InvalidValueParameterException an error trying to parse the data bytes into a message.
     */
    void initUsing( final DatagramPacket packet )
            throws InvalidValueParameterException;

    /**
     * Initialize a message using custom values.
     *
     * @param pgasName PGAS name.
     * @param address  message source address.
     * @param port     message source port address.
     * @param type     message type.
     * @param index    a PGAS index value (not always used).
     * @param value    a message value to transport (not always used).
     *
     * @throws InvalidValueParameterException an error trying to parse the data bytes into a message.
     */
    void initUsing(
            final int pgasName,
            final InetAddress address,
            final int port,
            final MessageType type,
            final long index,
            final I value
    )
            throws InvalidValueParameterException;

    /**
     * @return true if this message must end the middleware listener.
     */
    boolean isEndMessage();
}
