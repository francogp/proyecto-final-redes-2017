package ar.edu.unrc.pellegrini.franco.pgas;

import java.net.InetAddress;
import java.util.Map;

public
interface ProcessesConfigurations< I > {

    String getPgasDataType();

    void setPgasDataType( final String pgasDataType );

    Map< InetAddress, Map< Integer, Process< I > > > getProcessByAddress();

    void setProcessByAddress( final Map< InetAddress, Map< Integer, Process< I > > > processByAddress );

    Map< Integer, Process< I > > getProcessByPid();

    void setProcessByPid( final Map< Integer, Process< I > > processByPid );

    Process< I > getProcessConfig( final int pid );

    Process< I > getProcessConfig(
            final InetAddress address,
            final int port
    );

    int getProcessQuantity();

    void setProcessQuantity( final int processQuantity );
}
