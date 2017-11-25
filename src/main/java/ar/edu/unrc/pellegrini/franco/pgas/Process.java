package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class Process< I extends Comparable< I > > {
    private final Map< Integer, Map< MessageType, BlockingQueue< Message< I > > > > blockingQueueMapByName;
    private final InetAddress                                                       inetAddress;
    private final int                                                               pid;
    private final Integer                                                           port;
    private final Map< Integer, List< I > >                                         values;

    public
    Process(
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
        blockingQueueMapByName = new ConcurrentHashMap<>(valuesByPgasName.size()); //msgTypeList.length
        final Set< Entry< Integer, List< I > > > pgasNames = valuesByPgasName.entrySet();
        for ( final Entry< Integer, List< I > > entry : pgasNames ) {
            final Integer                                           pgasName         = entry.getKey();
            final List< I >                                         processValues    = entry.getValue();
            final Map< MessageType, BlockingQueue< Message< I > > > blockingQueueMap =
                    new ConcurrentHashMap<>(msgTypeList.size()); //msgTypeList.lengt
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

    public
    InetAddress getInetAddress() {
        return inetAddress;
    }

    public
    int getPid() {
        return pid;
    }

    public
    Integer getPort() {
        return port;
    }

    public
    List< I > getValues( final int pgasName ) {
        return values.get(pgasName);
    }

    public
    void registerMsg( final Message< I > message )
            throws InterruptedException {
        final BlockingQueue< Message< I > > messages = blockingQueueMapByName.get(message.getPgasName()).get(message.getType());
        messages.put(message);
    }

    public
    Message< I > waitFor(
            final int pgasName,
            final MessageType msgType
    )
            throws InterruptedException {
        assert !msgType.isMiddlewareMessageType();

        final Map< MessageType, BlockingQueue< Message< I > > > messageTypeBlockingQueueMap = blockingQueueMapByName.get(pgasName);
        if ( messageTypeBlockingQueueMap == null ) {
            throw new IllegalArgumentException("unknown pgasName: " + pgasName + " msgType: " + msgType);
        }
        final BlockingQueue< Message< I > > messages = messageTypeBlockingQueueMap.get(msgType);
        return messages.take();
    }
}
