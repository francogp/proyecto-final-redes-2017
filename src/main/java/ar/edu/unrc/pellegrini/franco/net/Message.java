package ar.edu.unrc.pellegrini.franco.net;

import ar.edu.unrc.pellegrini.franco.net.exceptions.InvalidValueParameterException;
import ar.edu.unrc.pellegrini.franco.net.implementations.Listener;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * The PGAS communicate between processes by {@link Message}. A {@link Listener} dispatch them.
 */
public
interface Message {

    /**
     * @return source address of the message
     */
    InetAddress getAddress();

    /**
     * @return an index transported by the message, usually used by the value.  Not always used.
     */
    long getIndex();

    /**
     * @return message as a byte[] representation
     */
    byte[] getMessageAsBytes();

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
    byte[] getValueAsBytes();

    /**
     * @return byte[] representation length of the message.
     */
    int getValueBytesSize();

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
     * @param pgasName     PGAS name.
     * @param address      message source address.
     * @param port         message source port address.
     * @param type         message type.
     * @param index        a PGAS index value (not always used).
     * @param valueAsBytes a message value to transport (not always used).
     *
     * @throws InvalidValueParameterException an error trying to parse the data bytes into a message.
     */
    void initUsing(
            final int pgasName,
            final InetAddress address,
            final int port,
            final MessageType type,
            final long index,
            final int valueBytesSize,
            final byte[] valueAsBytes
    )
            throws InvalidValueParameterException;

    /**
     * @return true if this message must end the middleware listener.
     */
    boolean isEndMessage();
}
