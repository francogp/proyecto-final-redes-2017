package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.net.implementations.Listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;
import static java.util.logging.Logger.getLogger;

/**
 * @param <I> value carried by the Message.
 */
public
class SimpleMiddleware< I >
        implements Middleware< I > {

    /**
     * Coordinator process identification.
     */
    public static final int  COORDINATOR_PID   = 1;
    /**
     * Index to be considered "ignored" in the message.
     */
    public static final long IGNORED_INDEX     = Long.MIN_VALUE;
    /**
     * PGAS name to be considered "ignored" in the message.
     */
    public static final int  IGNORED_PGAS_NAME = Integer.MIN_VALUE;
    private final Map< MessageType, BlockingQueue< Message< I > > > blockingQueueMap;
    private final boolean                                           coordinator;
    private final Supplier< Message< I > >                          newMessageSupplier;
    private final Map< Integer, PGAS< I > >                         pgasRegistryNameToPGAS;
    private final int                                               pid;
    private final int                                               port;
    private final ProcessesConfigurations< I >                      processesConfigurations;
    private final int                                               valueByteBufferSize;
    private       DatagramSocket                                    datagramSocket;
    private boolean debugMode = false;
    private Listener< I > listener;

    /**
     * @param pid                     of the current process.
     * @param processesConfigurations processes configurations.
     * @param newMessageSupplier      supplier for new {@link Message} instances.
     * @param valueByteBufferSize     byte size of the value type I.
     */
    public
    SimpleMiddleware(
            final int pid,
            final ProcessesConfigurations< I > processesConfigurations,
            final Supplier< Message< I > > newMessageSupplier,
            final int valueByteBufferSize
    ) {
        this.processesConfigurations = processesConfigurations;
        if ( pid <= 0 ) { throw new IllegalArgumentException("pid " + pid + " must be >= 0."); }
        if ( pid > processesConfigurations.getProcessQuantity() ) {
            throw new IllegalArgumentException("pid " + pid + " is greater than defined in config file.");
        }
        this.pid = pid;
        coordinator = pid == COORDINATOR_PID;
        pgasRegistryNameToPGAS = new HashMap<>();
        this.newMessageSupplier = newMessageSupplier;
        this.valueByteBufferSize = valueByteBufferSize;
        port = processesConfigurations.getProcessConfig(pid).getPort();
        listener = null;
        datagramSocket = null;

        final Set< MessageType > msgTypeList = MIDDLEWARE_MESSAGES;
        blockingQueueMap = new ConcurrentHashMap<>(msgTypeList.size());
        for ( final MessageType type : msgTypeList ) {
            if ( blockingQueueMap.put(type, new LinkedBlockingQueue<>()) != null ) {
                throw new IllegalStateException("duplicated MessageTypes (" + type + ')');
            }
        }
    }

    /**
     * @param value to be transformed as long.
     *
     * @return a long representation of a boolean value.
     */
    private static
    long parseBooleanAsResponse( final boolean value ) {
        return ( value ) ? 1L : 0L;
    }

    @Override
    public
    boolean andReduce( final boolean value )
            throws Exception {
        boolean andReduce = value;
        if ( coordinator ) {
            final int processQuantity = processesConfigurations.getProcessQuantity();
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                assert targetPid != COORDINATOR_PID;
                final Message< I > msg = receiveFrom(IGNORED_PGAS_NAME, targetPid, AND_REDUCE_MSG);
                andReduce = andReduce && parseResponseAsBoolean(msg);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                assert targetPid != COORDINATOR_PID;
                sendTo(IGNORED_PGAS_NAME, targetPid, CONTINUE_AND_REDUCE_MSG, parseBooleanAsResponse(andReduce), null);
            }
        } else {
            sendTo(IGNORED_PGAS_NAME, COORDINATOR_PID, AND_REDUCE_MSG, parseBooleanAsResponse(value), null);
            final Message< I > msg = receiveFrom(IGNORED_PGAS_NAME, COORDINATOR_PID, CONTINUE_AND_REDUCE_MSG);
            andReduce = parseResponseAsBoolean(msg);
        }
        return andReduce;
    }

    @Override
    public
    void barrier()
            throws Exception {
        if ( coordinator ) {
            assert pid == 1;
            final int processQuantity = processesConfigurations.getProcessQuantity();
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                assert targetPid != COORDINATOR_PID;
                receiveFrom(IGNORED_PGAS_NAME, targetPid, BARRIER_MSG);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                assert targetPid != COORDINATOR_PID;
                sendTo(IGNORED_PGAS_NAME, targetPid, CONTINUE_BARRIER_MSG, IGNORED_INDEX, null);
            }
        } else {
            assert pid >= 1;
            sendTo(IGNORED_PGAS_NAME, COORDINATOR_PID, BARRIER_MSG, IGNORED_INDEX, null);
            receiveFrom(IGNORED_PGAS_NAME, COORDINATOR_PID, CONTINUE_BARRIER_MSG);
        }
    }

    @Override
    public
    void closeListener()
            throws Exception {
        if ( coordinator ) {
            final int processQuantity = processesConfigurations.getProcessQuantity();
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                assert targetPid != COORDINATOR_PID;
                sendTo(IGNORED_PGAS_NAME, targetPid, END_MSG, IGNORED_INDEX, null);
            }
            sendTo(IGNORED_PGAS_NAME, COORDINATOR_PID, END_MSG, IGNORED_INDEX, null);
        }
    }

    @Override
    public
    Process< I > getProcessConfiguration( final int pid ) {
        return processesConfigurations.getProcessConfig(pid);
    }

    @Override
    public
    int getProcessQuantity() {
        return processesConfigurations.getProcessQuantity();
    }

    @Override
    public
    int getWhoAmI() {
        return pid;
    }

    @Override
    public
    boolean imLast() {
        return pid == processesConfigurations.getProcessQuantity();
    }

    @Override
    public
    boolean isCoordinator() {
        return coordinator;
    }

    private
    boolean parseResponseAsBoolean( final Message< I > message ) {
        return message.getIndex() != 0L;
    }

    private
    void processIncomingMessage( final Message< I > incomingMessage ) {
        try {
            final Process< I > targetProcess = processesConfigurations.getProcessConfig(incomingMessage.getAddress(), incomingMessage.getPort());
            final int          pgasName      = incomingMessage.getPgasName();
            switch ( incomingMessage.getType() ) {
                case AND_REDUCE_MSG:
                case BARRIER_MSG:
                    assert coordinator;
                    registerMsg(incomingMessage);
                    break;
                case CONTINUE_BARRIER_MSG:
                case CONTINUE_AND_REDUCE_MSG:
                    assert !coordinator;
                    registerMsg(incomingMessage);
                    break;
                case END_MSG:
                    break;
                case READ_MSG:
                    sendTo(pgasName,
                            targetProcess,
                            READ_RESPONSE_MSG,
                            incomingMessage.getIndex(),
                            pgasRegistryNameToPGAS.get(pgasName).read(incomingMessage.getIndex()));
                    break;
                case READ_RESPONSE_MSG:
                    targetProcess.registerMsg(incomingMessage);
                    break;
                case WRITE_MSG:
                    pgasRegistryNameToPGAS.get(pgasName).write(incomingMessage.getIndex(), incomingMessage.getValue());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Message<I> type = " + incomingMessage.getType());
                    //TODO add a panic!
            }
        } catch ( final Exception e ) {
            getLogger(Listener.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public
    Message< I > receiveFrom(
            final int pgasName,
            final int senderPid,
            final MessageType messageType
    )
            throws InterruptedException {
        final Process< I > hostsConfig = processesConfigurations.getProcessConfig(senderPid);
        if ( debugMode ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pid)
                    .append("] name(")
                    .append(pgasName)
                    .append(") -> receiveFrom pid[")
                    .append(senderPid)
                    .append("] ")
                    .append(messageType));
        }
        return messageType.isMiddlewareMessageType() ? blockingQueueMap.get(messageType).take() : hostsConfig.waitFor(pgasName, messageType);
    }

    private
    void registerMsg( final Message< I > message )
            throws InterruptedException {
        blockingQueueMap.get(message.getType()).put(message);
    }

    @Override
    public
    void registerPGAS( final PGAS< I > pgas ) {
        if ( pgasRegistryNameToPGAS.put(pgas.getName(), pgas) != null ) {
            throw new IllegalArgumentException("PGAS already registered with name: " + pgas.getName());
        }
    }

    private synchronized
    void sendMessage(
            final Message< I > message
    )
            throws IOException {
        datagramSocket.send(new DatagramPacket(message.getAsBytes(), message.getAsBytes().length, message.getAddress(), message.getPort()));
    }

    @Override
    public
    void sendTo(
            final int pgasName,
            final Process< I > targetProcess,
            final MessageType msgType,
            final long index,
            final I value
    )
            throws Exception {
        if ( listener == null ) {
            throw new IllegalStateException("Listener not started. Use startServer()");
        }
        final Message< I > msg = newMessageSupplier.get();
        msg.initUsing(pgasName, targetProcess.getInetAddress(), targetProcess.getPort(), msgType, index, value);
        if ( debugMode ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pid)
                    .append("] name(")
                    .append(pgasName)
                    .append(") -> sendTo pid[")
                    .append(processesConfigurations.getProcessConfig(targetProcess.getInetAddress(), targetProcess.getPort()).getPid())
                    .append("] ")
                    .append(msgType)
                    .append(" { index=")
                    .append(index)
                    .append(( value != null ) ? ( ", value=" + value ) : "")
                    .append(" }"));
        }
        sendMessage(msg);
    }

    @Override
    public
    void sendTo(
            final int pgasName,
            final int targetPid,
            final MessageType messageType,
            final long index,
            final I value
    )
            throws Exception {
        sendTo(pgasName, processesConfigurations.getProcessConfig(targetPid), messageType, index, value);
    }

    @Override
    public
    void setDebugMode( final boolean enabled ) {
        debugMode = enabled;
    }

    @Override
    public synchronized
    void startServer() {
        try {
            datagramSocket = new DatagramSocket(port);
            listener = new Listener<>(datagramSocket, this::processIncomingMessage, valueByteBufferSize, newMessageSupplier);
            final Thread serverThread = new Thread(listener);
            serverThread.start();
        } catch ( final SocketException e ) {
            throw new IllegalStateException("Cannot bind the port " + port + " to localhost pid " + pid, e);
        }
    }

}

