package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.*;
import ar.edu.unrc.pellegrini.franco.net.Process;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;
import static java.util.logging.Logger.getLogger;

public
class Middleware< I extends Comparable< I > > {

    public static final int     COORDINATOR_PID = 1;
    private static      boolean debugMode       = false;
    private final boolean                   coordinator;
    private final NetConfiguration< I >     netConfiguration;
    private final Supplier< Message< I > >  newMessageSupplier;
    private final Map< Integer, PGAS< I > > pgasRegistryNameToPGAS;
    private final int                       pid;
    private final int                       port;
    private final int                       processQuantity;
    private final int                       valueByteBufferSize;
    private       Listener< I >             listener;


    public
    Middleware(
            final int pid,
            final NetConfiguration< I > netConfiguration,
            final Supplier< Message< I > > newMessageSupplier,
            final int valueByteBufferSize
    ) {
        processQuantity = netConfiguration.getProcessQuantity();
        if ( pid <= 0 ) { throw new IllegalArgumentException("pid " + pid + " must be >= 0."); }
        if ( pid > processQuantity ) { throw new IllegalArgumentException("pid " + pid + " is greater than defined in config file."); }
        this.pid = pid;
        coordinator = pid == COORDINATOR_PID;
        this.pgasRegistryNameToPGAS = new HashMap<>();
        this.netConfiguration = netConfiguration;
        this.newMessageSupplier = newMessageSupplier;
        this.valueByteBufferSize = valueByteBufferSize;
        this.port = netConfiguration.getProcessConfig(pid).getPort();
        listener = null;
    }

    public final
    boolean andReduce( final boolean value )
            throws Exception {
        boolean andReduce = value;
        if ( coordinator ) {
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                final Message< I > msg = waitFor(0, targetPid, AND_REDUCE_MSG);
                andReduce = andReduce && parseResponseAsBoolean(msg);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                sendTo(0, targetPid, CONTINUE_AND_REDUCE_MSG, ( andReduce ) ? 1L : 0L, null);
            }
        } else {
            sendTo(0, COORDINATOR_PID, AND_REDUCE_MSG, ( value ) ? 1L : 0L, null);
            final Message< I > msg = waitFor(0, COORDINATOR_PID, CONTINUE_AND_REDUCE_MSG);
            andReduce = parseResponseAsBoolean(msg);
        }
        return andReduce;
    }

    public final
    void barrier()
            throws Exception {
        if ( coordinator ) {
            assert pid == 1;
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                waitFor(0, targetPid, BARRIER_MSG);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                sendTo(0, targetPid, CONTINUE_BARRIER_MSG, 0L, null);
            }
        } else {
            assert pid >= 1;
            sendTo(0, COORDINATOR_PID, BARRIER_MSG, 0L, null);
            waitFor(0, COORDINATOR_PID, CONTINUE_BARRIER_MSG);
        }
    }

    public final
    void endService()
            throws Exception {
        if ( coordinator ) {
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                sendTo(0, targetPid, END_MSG, 0L, null);
            }
            sendTo(0, 1, END_MSG, 0L, null);
        }
    }

    public
    int getCoordinatorPid() {
        return COORDINATOR_PID;
    }

    public
    int getPid() {
        return pid;
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }

    public final
    boolean imLast() {
        return pid == processQuantity;
    }

    public
    boolean isCoordinator() {
        return coordinator;
    }

    private
    boolean parseResponseAsBoolean( final Message< I > message ) {
        return message.getIndexParameter() != 0L;
    }

    protected final
    void processIncomingMessage( final Message< I > incomingMessage ) {
        try {
            final Process< I > targetProcess = netConfiguration.getProcessConfig(incomingMessage.getAddress(), incomingMessage.getPort());
            final int          pgasName      = incomingMessage.getPgasName();
            switch ( incomingMessage.getType() ) {
                case AND_REDUCE_MSG:
                case BARRIER_MSG:
                    assert coordinator;
                    targetProcess.registerMsg(incomingMessage);
                    break;
                case CONTINUE_BARRIER_MSG:
                case CONTINUE_AND_REDUCE_MSG:
                    assert !coordinator;
                    targetProcess.registerMsg(incomingMessage);
                    break;
                case END_MSG:
                    break;
                case READ_MSG:
                    sendTo(pgasName,
                            targetProcess,
                            READ_RESPONSE_MSG,
                            incomingMessage.getIndexParameter(),
                            pgasRegistryNameToPGAS.get(pgasName).read(incomingMessage.getIndexParameter()));
                    break;
                case READ_RESPONSE_MSG:
                    targetProcess.registerMsg(incomingMessage);
                    break;
                case WRITE_MSG:
                    pgasRegistryNameToPGAS.get(pgasName).write(incomingMessage.getIndexParameter(), incomingMessage.getValueParameter());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Message<I> type = " + incomingMessage.getType());
            }
        } catch ( final Exception e ) {
            getLogger(Listener.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public
    void registerPGAS( final PGAS< I > pgas ) {
        pgasRegistryNameToPGAS.put(pgas.getName(), pgas);
    }

    private
    void sendTo(
            final int pgasName,
            final Process< I > targetProcess,
            final MessageType msgType,
            final long indexParameter,
            final I valueParameter
    )
            throws Exception {
        if ( listener == null ) {
            throw new IllegalStateException("Listener not started. Use startServer()");
        }
        final Message< I > msg = newMessageSupplier.get();
        msg.initUsing(pgasName, targetProcess.getInetAddress(), targetProcess.getPort(), msgType, indexParameter, valueParameter);
        if ( debugMode ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pid)
                    .append("] name(")
                    .append(pgasName)
                    .append(") -> sendTo pid[")
                    .append(netConfiguration.getProcessConfig(targetProcess.getInetAddress(), targetProcess.getPort()).getPid())
                    .append("] ")
                    .append(msgType)
                    .append(" { index=")
                    .append(indexParameter)
                    .append(( valueParameter != null ) ? ( ", value=" + valueParameter ) : "")
                    .append(" }"));
        }
        listener.send(msg);
    }

    public final
    void sendTo(
            final int pgasName,
            final int targetPid,
            final MessageType msgType,
            final long indexParameter,
            final I valueParameter
    )
            throws Exception {
        sendTo(pgasName, netConfiguration.getProcessConfig(targetPid), msgType, indexParameter, valueParameter);
    }

    public final
    void setDebugMode( final boolean mode ) {
        debugMode = mode;
    }

    public final
    void startServer() {
        try {
            listener = new Listener< I >(port, this::processIncomingMessage, Message::isEndMessage, valueByteBufferSize, newMessageSupplier);
            final Thread serverThread = new Thread(listener);
            serverThread.start();
        } catch ( final SocketException e ) {
            throw new IllegalArgumentException("Cannot bind the port " + port + " to localhost pid " + pid, e);
        }
    }

    public final
    Message< I > waitFor(
            final int pgasName,
            final int senderPid,
            final MessageType msgType
    )
            throws InterruptedException {
        final Process< I > hostsConfig = netConfiguration.getProcessConfig(senderPid);
        if ( debugMode ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[").append(pid).append("] name(").append(pgasName).append(") -> waitFor pid[")
                    .append(senderPid)
                    .append("] ")
                    .append(msgType));
        }
        return hostsConfig.waitFor(msgType);
    }


}

