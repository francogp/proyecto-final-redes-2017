package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * {@link Process} implementation for a Distributed Architecture.
 *
 * @param <I> value type carried by the Message.
 */
@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DistributedProcess< I >
        implements Process< I > {
    private final Map< Integer, Map< MessageType, BlockingQueue< Message< I > > > > blockingQueueMapByName;
    private final InetAddress                                                       inetAddress;
    private final int                                                               pid;
    private final Integer                                                           port;
    private final Map< Integer, List< I > >                                         values;

    /**
     * @param pid              process identification. Pid = 1 is considered a coordinator.
     * @param inetAddress      process location.
     * @param port             process location port.
     * @param valuesByPgasName values to be used by this process, by PGAS name.
     */
    public
    DistributedProcess(
            final int pid,
            final InetAddress inetAddress,
            final Integer port,
            final Map< Integer, List< I > > valuesByPgasName
    ) {
        this.pid = pid;
        this.inetAddress = inetAddress;
        this.port = port;
        if ( ( valuesByPgasName == null ) || valuesByPgasName.isEmpty() ) {
            throw new IllegalArgumentException("valuesByPgasName cannot be empty or null");
        }
        values = new ConcurrentHashMap<>(valuesByPgasName);
        final Set< MessageType > msgTypeList = MessageType.PROCESS_MESSAGES;
        blockingQueueMapByName = new ConcurrentHashMap<>(valuesByPgasName.size());
        final Set< Integer > pgasNames = valuesByPgasName.keySet();
        for ( final Integer pgasName : pgasNames ) {
            final Map< MessageType, BlockingQueue< Message< I > > > blockingQueueMap = new ConcurrentHashMap<>(msgTypeList.size());
            for ( final MessageType type : msgTypeList ) {
                if ( blockingQueueMap.put(type, new LinkedBlockingQueue<>()) != null ) {
                    throw new IllegalStateException("duplicated MessageTypes (" + type + ')');
                }
            }
            if ( blockingQueueMapByName.put(pgasName, blockingQueueMap) != null ) {
                throw new IllegalStateException("duplicated PGAS name: " + pgasName);
            }
        }
    }

    @Override
    public
    InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public
    int getPid() {
        return pid;
    }

    @Override
    public
    Integer getPort() {
        return port;
    }

    @Override
    public
    List< I > getValues( final int pgasName ) {
        return values.get(pgasName);
    }

    @Override
    public
    void registerMsg( final Message< I > message )
            throws InterruptedException {
        final Map< MessageType, BlockingQueue< Message< I > > > messageTypeBlockingQueueMap = blockingQueueMapByName.get(message.getPgasName());
        if ( messageTypeBlockingQueueMap == null ) {
            throw new IllegalArgumentException("unknown pgasName: " + message.getPgasName() + " in message: " + message);
        }
        messageTypeBlockingQueueMap.get(message.getType()).put(message);
    }

    @Override
    public
    Message< I > waitFor(
            final int pgasName,
            final MessageType messageType
    )
            throws InterruptedException {
        assert !messageType.isMiddlewareMessageType();

        final Map< MessageType, BlockingQueue< Message< I > > > messageTypeBlockingQueueMap = blockingQueueMapByName.get(pgasName);
        if ( messageTypeBlockingQueueMap == null ) {
            throw new IllegalArgumentException("unknown pgasName: " + pgasName + " msgType: " + messageType);
        }
        return messageTypeBlockingQueueMap.get(messageType).take();
    }
}
