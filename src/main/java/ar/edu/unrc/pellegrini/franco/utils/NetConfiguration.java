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
class NetConfiguration< I extends Comparable< I > > {
    public static final String JSON_INET_ADDRESS = "inetAddress";
    public static final String JSON_PORT         = "port";
    public static final String JSON_TO_SORT      = "toSort";
    private final Map< InetAddress, Map< Integer, Host< I > > > hostsByAddress;
    private final Map< Integer, Host< I > >                     hostsByPid;
    private final int                                           processQuantity;

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
    NetConfiguration( final String configFilePath ) {
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
    NetConfiguration( final File configFilePath ) {
        try ( InputStreamReader reader = new InputStreamReader(new FileInputStream(configFilePath), Charset.forName("UTF-8")) ) {
            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            final JSONArray hostsInJSON = (JSONArray) jsonObject.get("hosts");
            if ( hostsInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong hosts quantity in config file \"" + configFilePath + "\".");
            }
            processQuantity = hostsInJSON.size();
            hostsByPid = new HashMap<>(processQuantity);
            hostsByAddress = new HashMap<>(processQuantity);

            int pid = 1;
            for ( final Object hostInJSON : hostsInJSON ) {
                final JSONObject host        = (JSONObject) hostInJSON;
                final String     inetAddress = (String) host.get(JSON_INET_ADDRESS);
                final Long       port        = (Long) host.get(JSON_PORT);

                final JSONArray toSortInJSON = (JSONArray) host.get(JSON_TO_SORT);
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
                final Host< I >   hostConfig      = new Host<>(pid, hostInetAddress, port.intValue(), toSort);
                //mapping from pid to Host.
                hostsByPid.put(pid, hostConfig);

                //mapping from InetAddress+port to Host.
                final Map< Integer, Host< I > > hostByPort = hostsByAddress.computeIfAbsent(hostInetAddress, address -> new ConcurrentHashMap<>());
                if ( hostByPort.get(port.intValue()) != null ) {
                    throw new IllegalArgumentException("there's two hosts with the same address : InetAddress=" + hostInetAddress + " port=" + port);
                }
                //FIXME si hay uno solo, es necesario ConcurrentHashMap?
                hostByPort.put(port.intValue(), hostConfig);

                pid++;
            }
        } catch ( final FileNotFoundException e ) {
            throw new IllegalArgumentException("file not found: \"" + configFilePath + "\".", e);
        } catch ( final UnknownHostException e ) {
            throw new IllegalStateException("unknown host from file: \"" + configFilePath + "\".", e);
        } catch ( final IOException e ) {
            throw new IllegalArgumentException("problems loading \"" + configFilePath + "\".", e);
        } catch ( final ParseException e ) {
            throw new IllegalArgumentException("wrong json format in config file \"" + configFilePath + "\".", e);
        }
    }

    public
    Host< I > getHostsConfig( final int pid ) {
        return hostsByPid.get(pid);
    }

    public
    Host< I > getHostsConfig(
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
