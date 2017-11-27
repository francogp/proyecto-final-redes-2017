package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class ListenerTest {

    @SuppressWarnings( "OverlyLongLambda" )
    @Test
    final
    void runTest() {
        try {
            final int              port              = 10001;
            final DatagramSocket   datagramSocket    = new DatagramSocket(port);
            final Queue< Message > receivedMessages  = new ConcurrentLinkedQueue<>();
            final Listener         longMessageServer = new Listener(datagramSocket, receivedMessages::add, 8);
            final Thread           serverThread      = new Thread(longMessageServer);
            serverThread.start();
            Thread.sleep(100L);
            final String      destAddress = "127.0.0.1";
            final InetAddress localHost   = InetAddress.getByName(destAddress);
            final Message     msg1        = new SimpleMessage();
            msg1.initUsing(10, localHost, port, READ_MSG, 1000L, 8, longToBytes(0L));
            sendMessage(msg1, datagramSocket);
            final Message msg2 = new SimpleMessage();
            msg2.initUsing(10, localHost, port, WRITE_MSG, 9876L, 8, longToBytes(-9998881000L));
            sendMessage(msg2, datagramSocket);
            final Message msg3 = new SimpleMessage();
            msg3.initUsing(10, localHost, port, END_MSG, 0L, 8, longToBytes(0L));
            sendMessage(msg3, datagramSocket);
            serverThread.join();
            if ( receivedMessages.isEmpty() ) { throw new AssertionError("server output is empty"); }
            final List< Message > expected = Arrays.asList(msg1, msg2);
            assertThat(receivedMessages.containsAll(expected), is(true));
            assertThat(receivedMessages.size(), is(expected.size()));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

    public final synchronized
    void sendMessage(
            final Message msg,
            final DatagramSocket datagramSocket
    )
            throws IOException {
        final DatagramPacket packet = new DatagramPacket(msg.getMessageAsBytes(), msg.getMessageAsBytes().length, msg.getAddress(), msg.getPort());
        datagramSocket.send(packet);
    }

}
