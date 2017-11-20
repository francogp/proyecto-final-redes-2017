package ar.edu.unrc.pellegrini.franco.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
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
        try ( InputStreamReader reader = new InputStreamReader(new FileInputStream(configFilePath), Charset.forName("UTF-8")) ) {
            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            final JSONArray hostsInJSON = (JSONArray) jsonObject.get("hosts");
            if ( hostsInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong hosts quantity in config file \"" + configFilePath + "\".");
            }
            processQuantity = hostsInJSON.size();
            hosts = new HashMap<>(processQuantity);

            long pid = 1L;
            for ( final Object hostInJSON : hostsInJSON ) {
                final JSONObject host     = (JSONObject) hostInJSON;
                final String     location = (String) host.get("location");

                final JSONArray toSortInJSON = (JSONArray) host.get("toSort");
                if ( toSortInJSON.isEmpty() ) {
                    throw new IllegalArgumentException("wrong toSort quantity in config file \"" + configFilePath + "\".");
                }
                final List< I > toSort = new ArrayList<>(toSortInJSON.size());
                for ( final Object valueInJSON : toSortInJSON ) {
                    //noinspection unchecked
                    final I value = (I) valueInJSON;
                    toSort.add(value);
                }
                hosts.put(pid, new HostConfig<>(location, toSort));
                pid++;
            }
        } catch ( final FileNotFoundException e ) {
            throw new IllegalArgumentException("file not found: \"" + configFilePath + "\".", e);
        } catch ( final IOException e ) {
            throw new IllegalArgumentException("problems loading \"" + configFilePath + "\".", e);
        } catch ( final ParseException e ) {
            throw new IllegalArgumentException("wrong json format in config file \"" + configFilePath + "\".", e);
        }
    }

    public
    HostConfig< I > getHostsConfig( final long pid ) {
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

    @SuppressWarnings( { "ClassWithoutNoArgConstructor", "PublicInnerClass" } )
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
