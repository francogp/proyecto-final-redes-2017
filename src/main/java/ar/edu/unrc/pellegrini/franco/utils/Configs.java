package ar.edu.unrc.pellegrini.franco.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public
class Configs {
    private final Map< Long, String > hosts;
    private final long                pgasSize;
    private final int                 processQuantity;

    public
    Configs(
            int pgasSize,
            int processQuantity
    ) {
        this.pgasSize = pgasSize;
        this.processQuantity = processQuantity;
        hosts = new HashMap<>();
    }

    /**
     * JSON file Format:
     * <pre>{@code
     * {
     *   "pgasSize": <size (Long)>,
     *   "hosts": ["<host 1 uri>:<port>", "<host 2 uri>:<port>", "<host 3 uri>:<port>", etc]
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
     *   "hosts": ["<host 1 uri>:<port>", "<host 2 uri>:<port>", "<host 3 uri>:<port>", etc]
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

            pgasSize = (long) jsonObject.get("pgasSize");
            JSONArray hostsInJSON = (JSONArray) jsonObject.get("hosts");
            if ( hostsInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong host quantity in config file \"" + configFilePath + "\".");
            }
            processQuantity = hostsInJSON.size();
            hosts = new HashMap<>(processQuantity);
            long pid = 1;
            for ( Object host : hostsInJSON ) {
                hosts.put(pid, host.toString());
                pid++;
            }
        } catch ( IOException e ) {
            throw new IllegalArgumentException("problems loading \"" + configFilePath + "\".", e);
        } catch ( ParseException e ) {
            throw new IllegalArgumentException("wrong json format in config file \"" + configFilePath + "\".", e);
        }
    }

    public
    Map< Long, String > getHosts() {
        return hosts;
    }

    public
    long getPgasSize() {
        return pgasSize;
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }
}
