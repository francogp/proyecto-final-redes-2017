package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractServer;
import ar.edu.unrc.pellegrini.franco.net.Message;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.function.Consumer;
import java.util.function.Function;

import static ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage.MSG_BYTES_LENGTH;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMessageServer
        extends AbstractServer< Long > {


    public
    LongMessageServer(
            final int port,
            final Consumer< Message< Long > > messageConsumer
    )
            throws SocketException {
        super(port, messageConsumer);
    }

    public
    LongMessageServer(
            final int port,
            final Consumer< Message< Long > > messageConsumer,
            final Function< Message< Long >, Boolean > isFinalMsgFunction
    )
            throws SocketException {
        super(port, messageConsumer, isFinalMsgFunction);
    }

    @Override
    protected
    DatagramPacket newDatagramPacket() {
        return new DatagramPacket(new byte[MSG_BYTES_LENGTH], MSG_BYTES_LENGTH);
    }

    @Override
    protected
    Message< Long > newMessage( final DatagramPacket packet ) {
        return new LongMessage(packet.getAddress(), packet.getPort(), packet.getData());
    }


}
