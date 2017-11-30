package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.net.implementations.Listener;

/**
 * Abstraction layer that provide communication, synchronization tools, messages dispatching, etc, for the PGAS used.
 */
public
interface Middleware {

    /**
     * Value used to ignore the message value content.
     */
    int IGNORED_VALUE_BYTE_SIZE = 0;

    /**
     * Generates a reduction of the value local copy applying the logic operand AND. It also acts as synchronization point like
     * a barrier.
     *
     * @param value to apply the AND reduction.
     *
     * @return AND reduction applied to local value from all processes.
     *
     * @throws Exception
     */
    boolean andReduce( final boolean value )
            throws Exception;

    /**
     * Synchronization point between processes.
     *
     * @throws Exception
     */
    void barrier()
            throws Exception;

    /**
     * Close the {@link Message} listener.
     *
     * @throws Exception
     */
    void closeListener()
            throws Exception;

    /**
     * @param pid of a process.
     *
     * @return the process configurations.
     */
    Process getProcessConfiguration( final int pid );

    /**
     * @return process quantity managed by the middleware.
     */
    int getProcessQuantity();

    /**
     * @return what process pid is using the middleware.
     */
    int getWhoAmI();

    /**
     * @return true if the middleware has the last pid.
     */
    boolean imLast();

    /**
     * @return true if the middleware is the coordinator
     */
    boolean isCoordinator();

    /**
     * Wait for a specific {@link Message} to arrive.
     *
     * @param pgasName    target PGAS name to wait.
     * @param senderPid   sender pid.
     * @param messageType message type.
     *
     * @return message received
     *
     * @throws InterruptedException
     */
    Message receiveFrom(
            final int pgasName,
            final int senderPid,
            final MessageType messageType
    )
            throws InterruptedException;

    /**
     * @param pgas to be registered in the middleware.
     */
    void registerPGAS( final PGAS< ? > pgas );

    /**
     * Send a message to a specific distributed PGAS.
     *
     * @param pgasName       target pgas name.
     * @param targetPid      target pid.
     * @param messageType    message type.
     * @param index          of the PGAS to be used.
     * @param valueBytesSize effective size of valueAsByte
     * @param valueAsBytes   to read or write in the target PGAS.
     *
     * @throws Exception
     */
    void sendTo(
            final int pgasName,
            final int targetPid,
            final MessageType messageType,
            final long index,
            final int valueBytesSize,
            final byte[] valueAsBytes
    )
            throws Exception;

    /**
     * Send a message to a specific distributed PGAS.
     *
     * @param pgasName       target pgas name.
     * @param targetProcess  target process.
     * @param msgType        message type.
     * @param index          of the PGAS to be used.
     * @param valueBytesSize effective size of valueAsByte
     * @param valueAsBytes   to read or write in the target PGAS.
     *
     * @throws Exception
     */
    void sendTo(
            final int pgasName,
            final Process targetProcess,
            final MessageType msgType,
            final long index,
            final int valueBytesSize,
            final byte[] valueAsBytes
    )
            throws Exception;

    /**
     * @param enabled true if must show debug log.
     */
    void setDebugMode( final boolean enabled );

    /**
     * Start a {@link Listener} to wait for {@link Message}.
     */
    void startServer();
}
