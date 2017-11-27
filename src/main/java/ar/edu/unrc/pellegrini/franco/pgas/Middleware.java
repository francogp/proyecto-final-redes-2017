package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

/**
 * Abstraction layer that provide communication, synchronization tools, messages dispatching, etc, for the PGAS used.
 *
 * @param <I> value type carried by the Message.
 */
public
interface Middleware< I > {

    /**
     * Generates a reduction of the value local copy applying the logic operand AND. It also acts as synchronization point like a barrier.
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
    Process< I > getProcessConfiguration( final int pid );

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
     * @return
     *
     * @throws InterruptedException
     */
    Message< I > receiveFrom(
            final int pgasName,
            final int senderPid,
            final MessageType messageType
    )
            throws InterruptedException;

    /**
     * @param pgas to be registered in the middleware.
     */
    void registerPGAS( final PGAS< I > pgas );

    /**
     * Send a message to a specific distributed PGAS.
     *
     * @param pgasName    target pgas name.
     * @param targetPid   target pid.
     * @param messageType message type.
     * @param index       of the PGAS to be used.
     * @param value       to read or write in the target PGAS.
     *
     * @throws Exception
     */
    void sendTo(
            final int pgasName,
            final int targetPid,
            final MessageType messageType,
            final long index,
            final I value
    )
            throws Exception;

    /**
     * Send a message to a specific distributed PGAS.
     *
     * @param pgasName      target pgas name.
     * @param targetProcess target process.
     * @param msgType       message type.
     * @param index         of the PGAS to be used.
     * @param value         to read or write in the target PGAS.
     *
     * @throws Exception
     */
    void sendTo(
            final int pgasName,
            final Process< I > targetProcess,
            final MessageType msgType,
            final long index,
            final I value
    )
            throws Exception;

    /**
     * @param enabled true if must show debug log.
     */
    void setDebugMode( final boolean enabled );

    /**
     * Start a {@link ar.edu.unrc.pellegrini.franco.net.implementations.Listener} to wait for {@link Message}.
     */
    void startServer();
}
