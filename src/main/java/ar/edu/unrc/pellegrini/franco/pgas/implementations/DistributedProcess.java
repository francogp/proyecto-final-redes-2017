package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.pgas.Process;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DistributedProcess
        implements Process {
    private final Map< Integer, Map< MessageType, BlockingQueue< Message > > > blockingQueueMapByName;
    private final InetAddress                                                  inetAddress;
    private final int                                                          pid;
    private final Integer                                                      port;
    private final Map< Integer, List< Object > >                               values;

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
            final Map< Integer, List< Object > > valuesByPgasName
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
            final Map< MessageType, BlockingQueue< Message > > blockingQueueMap = new ConcurrentHashMap<>(msgTypeList.size());
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
    List< Object > getValues( final int pgasName ) {
        return values.get(pgasName);
    }

    @Override
    public
    void registerMsg( final Message message )
            throws InterruptedException {
        final Map< MessageType, BlockingQueue< Message > > messageTypeBlockingQueueMap =
                blockingQueueMapByName.get(message.getPgasName());
        if ( messageTypeBlockingQueueMap == null ) {
            throw new IllegalArgumentException("unknown pgasName: " + message.getPgasName() + " in message: " + message);
        }
        messageTypeBlockingQueueMap.get(message.getType()).put(message);
    }

    @Override
    public
    Message waitFor(
            final int pgasName,
            final MessageType messageType
    )
            throws InterruptedException {
        assert !messageType.isMiddlewareMessageType();

        final Map< MessageType, BlockingQueue< Message > > messageTypeBlockingQueueMap = blockingQueueMapByName.get(pgasName);
        if ( messageTypeBlockingQueueMap == null ) {
            throw new IllegalArgumentException("unknown pgasName: " + pgasName + " msgType: " + messageType);
        }
        return messageTypeBlockingQueueMap.get(messageType).take();
    }
}
