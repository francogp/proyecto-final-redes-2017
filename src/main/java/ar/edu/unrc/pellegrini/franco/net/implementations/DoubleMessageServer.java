package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractServer;
import ar.edu.unrc.pellegrini.franco.net.Message;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.function.Consumer;
import java.util.function.Function;

import static ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage.DOUBLE_MSG_BYTES_LENGTH;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DoubleMessageServer
        extends AbstractServer< Double > {


    public
    DoubleMessageServer(
            final int port,
            final Consumer< Message< Double > > messageConsumer
    )
            throws SocketException {
        super(port, messageConsumer);
    }

    public
    DoubleMessageServer(
            final int port,
            final Consumer< Message< Double > > messageConsumer,
            final Function< Message< Double >, Boolean > isFinalMsgFunction
    )
            throws SocketException {
        super(port, messageConsumer, isFinalMsgFunction);
    }

    @Override
    protected
    DatagramPacket newDatagramPacket() {
        return new DatagramPacket(new byte[DOUBLE_MSG_BYTES_LENGTH], DOUBLE_MSG_BYTES_LENGTH);
    }

    @Override
    protected
    Message< Double > newMessage( final DatagramPacket packet ) {
        return new DoubleMessage(packet.getAddress(), packet.getPort(), packet.getData());
    }


}
