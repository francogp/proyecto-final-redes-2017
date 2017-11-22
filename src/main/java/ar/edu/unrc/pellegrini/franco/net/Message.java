package ar.edu.unrc.pellegrini.franco.net;

import java.net.InetAddress;

public
interface Message< I extends Comparable< I > > {
    InetAddress getAddress();

    byte[] getBytes();

    I getParameter1();

    I getParameter2();

    int getPort();

    I getResponse();

    MessageType getType();

    boolean isEndMessage();
}
