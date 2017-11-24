package ar.edu.unrc.pellegrini.franco.net;

import java.net.DatagramPacket;
import java.net.InetAddress;

public
interface Message< I extends Comparable< I > > {
    InetAddress getAddress();

    byte[] getBytes();

    Long getIndexParameter();

    int getPgasName();

    int getPort();

    MessageType getType();

    int getValueByteLength();

    I getValueParameter();

    void initUsing( final DatagramPacket packet );

    void initUsing(
            final int pgasName,
            final InetAddress address,
            final int port,
            final MessageType type,
            final long indexParameter,
            final I valueParameter
    );

    boolean isEndMessage();
}
