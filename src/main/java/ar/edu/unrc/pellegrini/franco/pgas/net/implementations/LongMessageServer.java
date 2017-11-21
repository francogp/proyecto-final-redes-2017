package ar.edu.unrc.pellegrini.franco.pgas.net.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.Server;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.function.Consumer;
import java.util.function.Function;

import static ar.edu.unrc.pellegrini.franco.pgas.net.implementations.LongMessage.MSG_BYTES_LENGHT;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMessageServer
        extends Server< Long > {


    public
    LongMessageServer(
            int port,
            Consumer< Message< Long > > messageConsumer
    )
            throws SocketException {
        super(port, messageConsumer);
    }

    public
    LongMessageServer(
            int port,
            Consumer< Message< Long > > messageConsumer,
            Function< Message< Long >, Boolean > isQueueFinalizationMsg
    )
            throws SocketException {
        super(port, messageConsumer, isQueueFinalizationMsg);
    }

    @Override
    protected
    DatagramPacket newDatagramPacket() {
        return new DatagramPacket(new byte[MSG_BYTES_LENGHT], MSG_BYTES_LENGHT);
    }

    @Override
    protected
    Message< Long > newMessage( final DatagramPacket packet ) {
        return new LongMessage(packet.getAddress(), packet.getPort(), packet.getData());
    }


}
