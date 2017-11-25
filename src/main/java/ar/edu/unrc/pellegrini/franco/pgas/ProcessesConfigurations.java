package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Process;

import java.net.InetAddress;
import java.util.Map;

public
class ProcessesConfigurations< I extends Comparable< I > > {
    private String                                           pgasDataType;
    private Map< InetAddress, Map< Integer, Process< I > > > processByAddress;
    private Map< Integer, Process< I > >                     processByPid;
    private int                                              processQuantity;

    public
    String getPgasDataType() {
        return pgasDataType;
    }

    public
    void setPgasDataType( String pgasDataType ) {
        this.pgasDataType = pgasDataType;
    }

    public
    Map< InetAddress, Map< Integer, Process< I > > > getProcessByAddress() {
        return processByAddress;
    }

    public
    void setProcessByAddress( Map< InetAddress, Map< Integer, Process< I > > > processByAddress ) {
        this.processByAddress = processByAddress;
    }

    public
    Map< Integer, Process< I > > getProcessByPid() {
        return processByPid;
    }

    public
    void setProcessByPid( Map< Integer, Process< I > > processByPid ) {
        this.processByPid = processByPid;
    }

    public
    Process< I > getProcessConfig( final int pid ) {
        return processByPid.get(pid);
    }

    public
    Process< I > getProcessConfig(
            final InetAddress address,
            final int port
    ) {
        return processByAddress.get(address).get(port);
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }

    public
    void setProcessQuantity( int processQuantity ) {
        this.processQuantity = processQuantity;
    }

}