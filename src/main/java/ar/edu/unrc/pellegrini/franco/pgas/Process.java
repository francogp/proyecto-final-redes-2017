package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.InetAddress;
import java.util.List;

/**
 * @param <I> value type carried by the Message.
 */
public
interface Process< I > {

    /**
     * @return process location.
     */
    InetAddress getInetAddress();

    /**
     * @return process identification.
     */
    int getPid();

    /**
     * @return process port location.
     */
    Integer getPort();

    /**
     * @param pgasName PGAS name.
     *
     * @return value list to be used to initialize the PGAS named pgasName.
     */
    List< I > getValues( int pgasName );

    /**
     * Register an incoming message from the Listener in a waiting queue, to be processed using waitFor and receiveFrom.
     *
     * @param message to register.
     *
     * @throws InterruptedException
     */
    void registerMsg( Message< I > message )
            throws InterruptedException;

    /**
     * Wait for a message type to be received in a specified pgasName.
     *
     * @param pgasName    name of the PGAS to wait.
     * @param messageType type of message to wait.
     *
     * @return the message received.
     *
     * @throws InterruptedException
     */
    Message< I > waitFor(
            int pgasName,
            MessageType messageType
    )
            throws InterruptedException;
}
