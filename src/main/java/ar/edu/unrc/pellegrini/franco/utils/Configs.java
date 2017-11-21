package ar.edu.unrc.pellegrini.franco.utils;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
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
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class Configs< I extends Comparable< I > > {
    public static final String INET_ADDRESS = "inetAddress";
    public static final String PORT         = "port";
    public static final String TO_SORT      = "toSort";
    private final Map< InetAddress, HostConfig< I > > hostsByAddress;
    private final Map< Integer, HostConfig< I > >     hostsByPid;
    private final int                                 processQuantity;

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
            hostsByAddress = new HashMap<>(processQuantity);

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

                HostConfig< I > hostConfig = new HostConfig<>(pid, inetAddress, port.intValue(), toSort);
                hostsByPid.put(pid, hostConfig);
                hostsByAddress.put(hostConfig.getInetAddress(), hostConfig);
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
    HostConfig< I > getHostsConfig( final InetAddress address ) {
        return hostsByAddress.get(address);
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }

    public
    int size() {
        return hostsByPid.size();
    }

    @SuppressWarnings( { "ClassWithoutNoArgConstructor", "PublicInnerClass" } )
    public static
    class HostConfig< I extends Comparable< I > > {
        private final InetAddress                                      inetAddress;
        private final int                                              pid;
        private final Integer                                          port;
        private final Map< Character, LinkedBlockingQueue< Message > > queues;
        private final List< I >                                        toSort;

        public
        HostConfig(
                final int pid,
                final String inetAddress,
                final Integer port,
                final List< I > toSort
        )
                throws UnknownHostException {
            this(pid, InetAddress.getByName(inetAddress), port, toSort);
        }

        public
        HostConfig(
                final int pid,
                final InetAddress inetAddress,
                final Integer port,
                final List< I > toSort
        ) {
            this.pid = pid;
            this.inetAddress = inetAddress;
            this.port = port;
            this.toSort = toSort;
            this.queues = new HashMap<>();
            final List< Character > msgTypeList = Message.getMsgTypeList();
            for ( Character type : msgTypeList ) {
                queues.put(type, new LinkedBlockingQueue<>());
            }
        }

        public
        InetAddress getInetAddress() {
            return inetAddress;
        }

        public
        int getPid() {
            return pid;
        }

        public
        Integer getPort() {
            return port;
        }

        public
        List< I > getToSort() {
            return toSort;
        }

        public
        void registerMsg( final Message message )
                throws InterruptedException {
            LinkedBlockingQueue< Message > messages = queues.get(message.getType());
            messages.put(message);
        }

        public
        Message waitFor( char msgType )
                throws InterruptedException {
            return queues.get(msgType).take();
        }
    }
}
