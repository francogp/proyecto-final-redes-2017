package ar.edu.unrc.pellegrini.franco.net;

import java.net.InetAddress;

public
interface Message< I extends Comparable< I > > {
    InetAddress getAddress();

    byte[] getBytes();

    Long getIndexParameter();

    int getPort();

    MessageType getType();

    I getValueParameter();

    boolean isEndMessage();
}
