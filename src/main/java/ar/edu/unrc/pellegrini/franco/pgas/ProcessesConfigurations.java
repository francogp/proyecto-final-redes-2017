package ar.edu.unrc.pellegrini.franco.pgas;

import java.net.InetAddress;
import java.util.Map;

/**
 * Represents a configuration file parsed from JSON or other standard, that initialize the PGAS.
 *
 * @param <I> data type to parse from a configuration file
 */
public
interface ProcessesConfigurations< I > {

    /**
     * @return data type supported by the PGAS.
     */
    String getPgasDataType();

    void setPgasDataType( final String pgasDataType );

    /**
     * @param pid of a process
     *
     * @return process configuration of a specific pid.
     */
    Process< I > getProcessConfig( final int pid );

    /**
     * @param address of a process.
     * @param port    of a process.
     *
     * @return process configuration from a process location.
     */
    Process< I > getProcessConfig(
            final InetAddress address,
            final int port
    );

    /**
     * @return total process number.
     */
    int getProcessQuantity();

    void setProcessQuantity( final int processQuantity );

    void setProcessByAddress( final Map< InetAddress, Map< Integer, Process< I > > > processByAddress );

    void setProcessByPid( final Map< Integer, Process< I > > processByPid );
}
