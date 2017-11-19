package ar.edu.unrc.pellegrini.franco.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public
class Configs< I extends Comparable< I > > {
    private final Map< Long, HostConfig< I > > hosts;
    private final int                          processQuantity;

    /**
     * JSON file Format:
     * <pre>{@code
     * {
     *   "hosts": [
     *     {"location":"<host 1 uri>:<port>", "toSort": [<Long 1>, <Long 2>, <Long 3>, etc]} ,
     *     {"location":"<host 2 uri>:<port>", "toSort": [<Long 4>, <Long 5>, <Long 6>, etc]} ,
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
     *     {"location":"<host 1 uri>:<port>", "toSort": [<Long 1>, <Long 2>, <Long 3>, etc]} ,
     *     {"location":"<host 2 uri>:<port>", "toSort": [<Long 4>, <Long 5>, <Long 6>, etc]} ,
     *     etc
     *   ]
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

            long pid = 1;
            for ( Object hostInJSON : hostsInJSON ) {
                JSONObject host     = (JSONObject) hostInJSON;
                String     location = (String) host.get("location");

                JSONArray toSortInJSON = (JSONArray) host.get("toSort");
                if ( toSortInJSON.isEmpty() ) {
                    throw new IllegalArgumentException("wrong toSort quantity in config file \"" + configFilePath + "\".");
                }
                List< I > toSort = new ArrayList<>();
                for ( Object valueInJSON : toSortInJSON ) {
                    I value = (I) valueInJSON;
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
