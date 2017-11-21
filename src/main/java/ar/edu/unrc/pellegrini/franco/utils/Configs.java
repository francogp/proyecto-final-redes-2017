package ar.edu.unrc.pellegrini.franco.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class Configs< I extends Comparable< I > > {
    public static final String INET_ADDRESS = "inetAddress";
    public static final String PORT         = "port";
    public static final String TO_SORT      = "toSort";
    private final Map< InetAddress, Map< Integer, HostConfig< I > > > hostsByAddress;
    private final Map< Integer, HostConfig< I > >                     hostsByPid;
    private final int                                                 processQuantity;

    /**
     * JSON file Format:
     * <pre>{@code
     * {
     *   "hosts": [
     *     {"inetAddress":"<host 1 uri>", : "port": <port>, "toSort": [<Long 1>, <Long 2>, <Long 3>, etc]} ,
     *     {"inetAddress":"<host 2 uri>", : "port": <port>, "toSort": [<Long 4>, <Long 5>, <Long 6>, etc]} ,
     *     etc
     *   ]
     * }
     * }</pre>
     *
     * @param configFilePath file path to load.
     */
    public
    Configs( final String configFilePath ) {
        this(new File(configFilePath));
    }

    /**
     * JSON file Format:
     * <pre>{@code
     * {
     *   "hosts": [
     *     {"inetAddress":"<host 1 uri>", : "port": <port>, "toSort": [<Long 1>, <Long 2>, <Long 3>, etc]} ,
     *     {"inetAddress":"<host 2 uri>", : "port": <port>, "toSort": [<Long 4>, <Long 5>, <Long 6>, etc]} ,
     *     etc
     *   ]
     * }
     * }</pre>
     *
     * @param configFilePath file to load.
     */
    public
    Configs( final File configFilePath ) {
        try ( InputStreamReader reader = new InputStreamReader(new FileInputStream(configFilePath), Charset.forName("UTF-8")) ) {
            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            final JSONArray hostsInJSON = (JSONArray) jsonObject.get("hosts");
            if ( hostsInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong hosts quantity in config file \"" + configFilePath + "\".");
            }
            processQuantity = hostsInJSON.size();
            hostsByPid = new HashMap<>(processQuantity);
            hostsByAddress = new HashMap<>();

            int pid = 1;
            for ( final Object hostInJSON : hostsInJSON ) {
                final JSONObject host        = (JSONObject) hostInJSON;
                final String     inetAddress = (String) host.get(INET_ADDRESS);
                final Long       port        = (Long) host.get(PORT);

                final JSONArray toSortInJSON = (JSONArray) host.get(TO_SORT);
                if ( toSortInJSON.isEmpty() ) {
                    throw new IllegalArgumentException("wrong toSort quantity in config file \"" + configFilePath + "\".");
                }
                final List< I > toSort = new ArrayList<>(toSortInJSON.size());
                for ( final Object valueInJSON : toSortInJSON ) {
                    //noinspection unchecked
                    final I value = (I) valueInJSON;
                    toSort.add(value);
                }

                final InetAddress hostInetAddress = InetAddress.getByName(inetAddress);
                HostConfig< I >   hostConfig      = new HostConfig<>(pid, hostInetAddress, port.intValue(), toSort);
                //mapping from pid to HostConfig.
                hostsByPid.put(pid, hostConfig);

                //mapping from InetAdress+port to HostConfig.
                Map< Integer, HostConfig< I > > hostByPort = hostsByAddress.get(hostInetAddress);
                if ( hostByPort == null ) {
                    hostByPort = new ConcurrentHashMap<>();
                    hostsByAddress.put(hostInetAddress, hostByPort);
                }
                final HostConfig< I > newHostByPort = hostByPort.get(port.intValue());
                if ( newHostByPort != null ) {
                    throw new IllegalArgumentException("there's two hosts with the same adress : InetAddress=" + hostInetAddress + " port=" + port);
                }
                hostByPort.put(port.intValue(), hostConfig);

                pid++;
            }
        } catch ( final FileNotFoundException e ) {
            throw new IllegalArgumentException("file not found: \"" + configFilePath + "\".", e);
        } catch ( UnknownHostException e ) {
            throw new IllegalStateException("unknown host from file: \"" + configFilePath + "\".", e);
        } catch ( final IOException e ) {
            throw new IllegalArgumentException("problems loading \"" + configFilePath + "\".", e);
        } catch ( final ParseException e ) {
            throw new IllegalArgumentException("wrong json format in config file \"" + configFilePath + "\".", e);
        }
    }

    public
    HostConfig< I > getHostsConfig( final int pid ) {
        return hostsByPid.get(pid);
    }

    public
    HostConfig< I > getHostsConfig(
            final InetAddress address,
            final int port
    ) {
        return hostsByAddress.get(address).get(port);
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }

    public
    int size() {
        return hostsByPid.size();
    }
}
