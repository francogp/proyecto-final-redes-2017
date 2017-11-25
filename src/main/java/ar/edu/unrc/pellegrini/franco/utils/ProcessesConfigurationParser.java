package ar.edu.unrc.pellegrini.franco.utils;

import ar.edu.unrc.pellegrini.franco.net.Process;
import ar.edu.unrc.pellegrini.franco.pgas.ProcessesConfigurations;
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

public final
class ProcessesConfigurationParser {
    public static final String JSON_DATA_TYPE    = "dataType";
    public static final String JSON_INET_ADDRESS = "inetAddress";
    public static final String JSON_PORT         = "port";
    public static final String JSON_PROCESS      = "processes";
    public static final String JSON_TO_SORT      = "toSort";

    /**
     * JSON file Format:
     * <pre>{@code
     * {
     *   "dataType":"<data type used in toSort>",
     *   "processes": [
     *     {"inetAddress":"<process 1 location>", : "port": <port>, "toSort": [<data 1>, <data 2>, <data 3>, etc]} ,
     *     {"inetAddress":"<process 2 location>", : "port": <port>, "toSort": [<data 4>, <data 5>, <data 6>, etc]} ,
     *     etc
     *   ]
     * }
     * }</pre>
     * <p>
     * the field toSort is optional, to allow specific process initialization only
     *
     * @param configFilePath file to load.
     */
    public static
    < I extends Comparable< I > > ProcessesConfigurations< I > parseConfigFile(
            final File configFilePath
    ) {
        try ( InputStreamReader reader = new InputStreamReader(new FileInputStream(configFilePath), Charset.forName("UTF-8")) ) {
            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            final JSONArray hostsInJSON = (JSONArray) jsonObject.get(JSON_PROCESS);
            if ( hostsInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong hosts quantity in config file \"" + configFilePath + "\".");
            }

            String dataTypeToSort = (String) jsonObject.get(JSON_DATA_TYPE);
            if ( dataTypeToSort == null ) {
                throw new IllegalArgumentException("data type to sort not found in config file \"" + configFilePath + "\".");
            }

            if ( !dataTypeToSort.matches("(Long)|(Double)") ) {
                throw new IllegalArgumentException(
                        "the only data type supported in this example implementation are Long and Double, and can be setted in the config file \"" +
                        configFilePath + "\".");
            }

            ProcessesConfigurations< I > parsedResults;
            switch ( dataTypeToSort ) {
                case "Long": {
                    parsedResults = (ProcessesConfigurations< I >) new ProcessesConfigurations< Long >();
                    break;
                }
                case "Double": {
                    parsedResults = (ProcessesConfigurations< I >) new ProcessesConfigurations< Double >();
                    break;
                }
                default:
                    throw new IllegalArgumentException("unknown data type");
            }

            parsedResults.setPgasDataType(dataTypeToSort);
            parsedResults.setProcessQuantity(hostsInJSON.size());
            final Map< InetAddress, Map< Integer, Process< I > > > processByAddress = new HashMap<>(parsedResults.getProcessQuantity());
            parsedResults.setProcessByAddress(processByAddress);
            final Map< Integer, Process< I > > processByPid = new HashMap<>(parsedResults.getProcessQuantity());
            parsedResults.setProcessByPid(processByPid);

            int pid = 1;
            for ( final Object hostInJSON : hostsInJSON ) {
                //TODO enviar la configuracion a cada host, y NO TODO el archivo que puede ser muy grande
                final JSONObject host        = (JSONObject) hostInJSON;
                final String     inetAddress = (String) host.get(JSON_INET_ADDRESS);
                final Long       port        = (Long) host.get(JSON_PORT);

                final JSONArray toSortInJSON = (JSONArray) host.get(JSON_TO_SORT);
                List< I >       toSort       = null;
                if ( toSortInJSON != null ) {
                    if ( toSortInJSON.isEmpty() ) {
                        throw new IllegalArgumentException("wrong toSort quantity in config file \"" + configFilePath + "\".");
                    }
                    toSort = new ArrayList<>(toSortInJSON.size());
                    for ( final Object valueInJSON : toSortInJSON ) {
                        //noinspection unchecked
                        final I value = (I) valueInJSON;
                        switch ( dataTypeToSort ) {
                            case "Long": {
                                if ( !( value instanceof Long ) ) {
                                    throw new IllegalArgumentException("the value " + value + " is not type Long");
                                }
                                break;
                            }
                            case "Double": {
                                if ( !( value instanceof Double ) ) {
                                    throw new IllegalArgumentException("the value " + value + " is not type Double");
                                }
                                break;
                            }
                            default:
                                throw new IllegalArgumentException("unknown data type");
                        }
                        toSort.add(value);
                    }
                }

                final InetAddress  hostInetAddress = InetAddress.getByName(inetAddress);
                final Process< I > processConfig   = new Process<>(pid, hostInetAddress, port.intValue(), toSort);
                //mapping from pid to Process.
                processByPid.put(pid, processConfig);

                //mapping from InetAddress+port to Process.
                final Map< Integer, Process< I > > hostByPorts =
                        processByAddress.computeIfAbsent(hostInetAddress, address -> new ConcurrentHashMap<>());
                if ( hostByPorts.put(port.intValue(), processConfig) != null ) {
                    throw new IllegalArgumentException("there's two hosts with the same address : InetAddress=" + hostInetAddress + " port=" + port);
                }

                pid++;
            }
            return parsedResults;
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
}
