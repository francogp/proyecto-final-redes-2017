package ar.edu.unrc.pellegrini.franco.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public
class Configs< I extends Comparable< I > > {
    private final Map< Long, HostConfig< I > > hosts;
    private final int                          processQuantity;

    public
    Configs(
            int processQuantity
    ) {
        this.processQuantity = processQuantity;
        hosts = new HashMap<>();
    }

    /**
     * JSON file Format:
     * <pre>{@code
     * {
     *   "pgasSize": <size (Long)>,
     *   "hosts": ["<host 1 uri>:<port>", "<host 2 uri>:<port>", "<host 3 uri>:<port>", etc],
     *   "toSort": [<Integer 1>, <Integer 2>, <Integer 3>, etc]
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
     *   "pgasSize": <size (Long)>,
     *   "hosts": ["<host 1 uri>:<port>", "<host 2 uri>:<port>", "<host 3 uri>:<port>", etc],
     *   "toSort": [<Integer 1>, <Integer 2>, <Integer 3>, etc]
     * }
     * }</pre>
     *
     * @param configFilePath file to load.
     */
    public
    Configs( final File configFilePath ) {
        try ( FileReader reader = new FileReader(configFilePath) ) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            JSONArray hostsInJSON = (JSONArray) jsonObject.get("hosts");
            if ( hostsInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong hosts quantity in config file \"" + configFilePath + "\".");
            }
            processQuantity = hostsInJSON.size();
            hosts = new HashMap<>(processQuantity);

            long     pid          = 1;
            Iterator hostIterator = hostsInJSON.iterator();
            while ( hostIterator.hasNext() ) {
                JSONObject host     = (JSONObject) hostIterator.next();
                String     location = (String) host.get("location");

                JSONArray toSortInJSON = (JSONArray) host.get("toSort");
                if ( toSortInJSON.isEmpty() ) {
                    throw new IllegalArgumentException("wrong toSort quantity in config file \"" + configFilePath + "\".");
                }
                List< I > toSort         = new ArrayList<>();
                Iterator  toSortIterator = toSortInJSON.iterator();
                while ( toSortIterator.hasNext() ) {
                    I value = (I) toSortIterator.next();
                    toSort.add(value);
                }
                hosts.put(pid, new HostConfig<>(location, toSort));
                pid++;
            }
        } catch ( IOException e ) {
            throw new IllegalArgumentException("problems loading \"" + configFilePath + "\".", e);
        } catch ( ParseException e ) {
            throw new IllegalArgumentException("wrong json format in config file \"" + configFilePath + "\".", e);
        }
    }

    public
    HostConfig< I > getHostsConfig( long pid ) {
        return hosts.get(pid);
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }

    public
    int size() {
        return hosts.size();
    }

    public static
    class HostConfig< I extends Comparable< I > > {
        private final String    location;
        private final List< I > toSort;

        public
        HostConfig(
                final String location,
                final List< I > toSort
        ) {
            this.location = location;
            this.toSort = toSort;
        }

        public
        String getLocation() {
            return location;
        }

        public
        List< I > getToSort() {
            return toSort;
        }
    }
}
