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
    private final long                distributedArraySize;
    private final Map< Long, String > hosts;
    private final int                 processQuantity;

    public
    Configs(
            int distributedArraySize,
            int processQuantity
    ) {
        this.distributedArraySize = distributedArraySize;
        this.processQuantity = processQuantity;
        hosts = new HashMap<>();
    }

    public
    Configs( final String configFilePath ) {
        this(new File(configFilePath));
    }

    public
    Configs( final File configFilePath ) {
        try {
            // read the json file
            FileReader reader = new FileReader(configFilePath);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            distributedArraySize = (long) jsonObject.get("distributedArraySize");
            JSONArray hostsInJSON = (JSONArray) jsonObject.get("hosts");
            if ( hostsInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong host quantity in config file \"" + configFilePath + "\".");
            }
            processQuantity = hostsInJSON.size();
            hosts = new HashMap<>(processQuantity);
            // take each value from the json array separately
            for ( Object host : hostsInJSON ) {
                JSONObject innerObj = (JSONObject) host;
                hosts.put((Long) innerObj.get("pid"), (String) innerObj.get("address"));
            }
        } catch ( IOException e ) {
            throw new IllegalArgumentException("problems loading \"" + configFilePath + "\".", e);
        } catch ( ParseException e ) {
            throw new IllegalArgumentException("wrong json format in config file \"" + configFilePath + "\".", e);
        }
    }

    public
    long getDistributedArraySize() {
        return distributedArraySize;
    }

    public
    Map< Long, String > getHosts() {
        return hosts;
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }
}
