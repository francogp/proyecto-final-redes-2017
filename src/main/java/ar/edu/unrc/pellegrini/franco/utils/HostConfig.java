package ar.edu.unrc.pellegrini.franco.utils;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.MessageType;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class HostConfig< I extends Comparable< I > > {
    private final InetAddress                                        inetAddress;
    private final int                                                pid;
    private final Integer                                            port;
    private final Map< MessageType, LinkedBlockingQueue< Message > > queues;
    private final List< I >                                          toSort;

    public
    HostConfig(
            final int pid,
            final InetAddress inetAddress,
            final Integer port,
            final List< I > toSort
    ) {
        this.pid = pid;
        this.inetAddress = inetAddress;
        this.port = port;
        this.toSort = toSort;
        this.queues = new ConcurrentHashMap<>();
        final MessageType[] msgTypeList = MessageType.values();
        for ( MessageType type : msgTypeList ) {
            queues.put(type, new LinkedBlockingQueue<>());
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
    List< I > getToSort() {
        return toSort;
    }

    public
    void registerMsg( final Message message )
            throws InterruptedException {
        final LinkedBlockingQueue< Message > messages = queues.get(message.getType());
        messages.put(message);
    }

    public
    Message waitFor( MessageType msgType )
            throws InterruptedException {
        final LinkedBlockingQueue< Message > messages = queues.get(msgType);
        return messages.take();
    }
}
