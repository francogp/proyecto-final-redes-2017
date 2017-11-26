package ar.edu.unrc.pellegrini.franco.net;

import java.net.DatagramPacket;
import java.net.InetAddress;

public
interface Message< I > {

    InetAddress getAddress();

    byte[] getAsBytes();

    Long getIndexParameter();

    int getPgasName();

    int getPort();

    MessageType getType();

    int getValueByteLength();

    I getValueParameter();

    void initUsing( final DatagramPacket packet )
            throws InvalidValueParameterException;

    void initUsing(
            final int pgasName,
            final InetAddress address,
            final int port,
            final MessageType type,
            final long indexParameter,
            final I valueParameter
    )
            throws InvalidValueParameterException;

    boolean isEndMessage();
}
