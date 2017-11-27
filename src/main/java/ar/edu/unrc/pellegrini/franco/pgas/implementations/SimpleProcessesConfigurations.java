package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.Process;
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
import java.util.regex.Pattern;

/**
 * used to store processes configurations, to distribute between PGAS processes.
 */
public
class SimpleProcessesConfigurations
        implements ProcessesConfigurations {
    public static final  String                                      JSON_DATA_TYPE          = "dataType";
    public static final  String                                      JSON_DISTRIBUTED_ARRAYS = "distributedArrays";
    public static final  String                                      JSON_INET_ADDRESS       = "inetAddress";
    public static final  String                                      JSON_NAME               = "name";
    public static final  String                                      JSON_PORT               = "port";
    public static final  String                                      JSON_PROCESS            = "processes";
    public static final  String                                      JSON_TO_SORT            = "toSort";
    private static final Pattern                                     VALID_DATA_TYPE_PATTERN = Pattern.compile("(Long)|(Double)");
    private              String                                      pgasDataType            = null;
    private              Map< InetAddress, Map< Integer, Process > > processByAddress        = null;
    private              Map< Integer, Process >                     processByPid            = null;
    private              int                                         processQuantity         = 0;

    /**
     * JSON file Format:
     * <pre>{@code
     * {
     *   "dataType":"<data type used in toSort (Long or Double supported in current implementation)>",
     *   "processes": [
     *     {"inetAddress":"<process 1 location>",
     *      "port": <port>,
     *      "distributedArrays": [
     *        {"name": <int name>,
     *         "toSort": toSort": [<data 1>, <data 2>, <data 3>, etc]  //optional
     *        }
     *      ]
     *     },
     *     {"inetAddress":"<process 2 location>",
     *      "port": <port>,
     *      "distributedArrays": [
     *        {"name": <int name>,
     *         "toSort": toSort": [<data 4>, <data 5>, <data 6>, etc] //optional
     *        }
     *      ]
     *     },
     *     etc
     *   ]
     * }
     * }</pre>
     * <p>
     * the field toSort is optional, to allow specific process initialization only
     *
     * @param configFilePath file to load.
     *
     * @return new {@link ProcessesConfigurations} parsed from configFilePath
     */
    public static
    ProcessesConfigurations parseFromFile(
            final File configFilePath
    ) {
        try ( InputStreamReader reader = new InputStreamReader(new FileInputStream(configFilePath), Charset.forName("UTF-8")) ) {
            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            final JSONArray processesInJSON = (JSONArray) jsonObject.get(JSON_PROCESS);
            if ( processesInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong processes quantity in config file \"" + configFilePath + "\".");
            }

            final String dataTypeToSort = (String) jsonObject.get(JSON_DATA_TYPE);
            validateDataType(configFilePath, dataTypeToSort);

            final ProcessesConfigurations parsedResults = new SimpleProcessesConfigurations();
            parsedResults.setPgasDataType(dataTypeToSort);
            parsedResults.setProcessQuantity(processesInJSON.size());
            final Map< InetAddress, Map< Integer, Process > > processByAddress = new HashMap<>(parsedResults.getProcessQuantity());
            parsedResults.setProcessByAddress(processByAddress);
            final Map< Integer, Process > processByPid = new HashMap<>(parsedResults.getProcessQuantity());
            parsedResults.setProcessByPid(processByPid);

            int pid = 1;
            for ( final Object processInJSON : processesInJSON ) {
                final JSONObject process     = (JSONObject) processInJSON;
                final String     inetAddress = (String) process.get(JSON_INET_ADDRESS);
                final Long       port        = (Long) process.get(JSON_PORT);

                final Map< Integer, List< Object > > distArraysValues        = new ConcurrentHashMap<>();
                final JSONArray                      distributedArraysInJSON = (JSONArray) process.get(JSON_DISTRIBUTED_ARRAYS);
                if ( distributedArraysInJSON != null ) {
                    if ( distributedArraysInJSON.isEmpty() ) {
                        throw new IllegalArgumentException("wrong distributed arrays quantity in config file \"" + configFilePath + "\".");
                    }
                    for ( final Object valueInJSON : distributedArraysInJSON ) {
                        final JSONObject     dArray       = (JSONObject) valueInJSON;
                        final Long           dArrayName   = (Long) dArray.get(JSON_NAME);
                        final JSONArray      toSortInJSON = (JSONArray) dArray.get(JSON_TO_SORT);
                        final List< Object > toSort       = parseToSort(configFilePath, dataTypeToSort, toSortInJSON);
                        if ( distArraysValues.put(dArrayName.intValue(), toSort) != null ) {
                            throw new IllegalStateException(
                                    "duplicated distributed array name: " + dArrayName + " in config file \"" + configFilePath + "\".");
                        }
                    }
                }
                final InetAddress processInetAddress = InetAddress.getByName(inetAddress);
                final Process     processConfig      = new DistributedProcess(pid, processInetAddress, port.intValue(), distArraysValues);
                //mapping from pid to Process.
                processByPid.put(pid, processConfig);

                //mapping from InetAddress+port to Process.
                final Map< Integer, Process > processByPorts =
                        processByAddress.computeIfAbsent(processInetAddress, address -> new ConcurrentHashMap<>());
                if ( processByPorts.put(port.intValue(), processConfig) != null ) {
                    throw new IllegalArgumentException(
                            "there's two processes with the same address : InetAddress=" + processInetAddress + " port=" + port);
                }

                pid++;
            }
            return parsedResults;
        } catch ( final FileNotFoundException e ) {
            throw new IllegalArgumentException("file not found: \"" + configFilePath + "\".", e);
        } catch ( final UnknownHostException e ) {
            throw new IllegalStateException("unknown host from config file: \"" + configFilePath + "\".", e);
        } catch ( final IOException e ) {
            throw new IllegalArgumentException("problems loading \"" + configFilePath + "\".", e);
        } catch ( final ParseException e ) {
            throw new IllegalArgumentException("wrong json format in config file \"" + configFilePath + "\".", e);
        }
    }

    private static
    < I > List< I > parseToSort(
            final File configFilePath,
            final String dataTypeToSort,
            final JSONArray toSortInJSON
    ) {
        final List< I > toSort = new ArrayList<>();
        if ( toSortInJSON != null ) {
            if ( toSortInJSON.isEmpty() ) {
                throw new IllegalArgumentException("wrong toSort quantity in config file \"" + configFilePath + "\".");
            }
            for ( final Object valueInJSON : toSortInJSON ) {
                //noinspection unchecked
                final I value = (I) valueInJSON;
                switch ( dataTypeToSort ) {
                    case "Long":
                        if ( !( value instanceof Long ) ) {
                            throw new IllegalArgumentException("the value " + value + " is not type Long");
                        }
                        break;
                    case "Double":
                        if ( !( value instanceof Double ) ) {
                            throw new IllegalArgumentException("the value " + value + " is not type Double");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("unknown data type");
                }
                toSort.add(value);
            }
        }
        return toSort;
    }

    private static
    void validateDataType(
            final File configFilePath,
            final String dataTypeToSort
    ) {
        if ( dataTypeToSort == null ) {
            throw new IllegalArgumentException("data type to sort not found in config file \"" + configFilePath + "\".");
        }

        if ( !VALID_DATA_TYPE_PATTERN.matcher(dataTypeToSort).matches() ) {
            throw new IllegalArgumentException(
                    "the only data type supported in this example implementation are Long and Double, and can be set in the config file \"" +
                    configFilePath + "\".");
        }
    }

    @Override
    public final
    String getPgasDataType() {
        return pgasDataType;
    }

    @Override
    public final
    void setPgasDataType( final String pgasDataType ) {
        this.pgasDataType = pgasDataType;
    }

    @Override
    public final
    Process getProcessConfig( final int pid ) {
        return processByPid.get(pid);
    }

    @Override
    public final
    Process getProcessConfig(
            final InetAddress address,
            final int port
    ) {
        return processByAddress.get(address).get(port);
    }

    @Override
    public final
    int getProcessQuantity() {
        return processQuantity;
    }

    @Override
    public final
    void setProcessQuantity( final int processQuantity ) {
        this.processQuantity = processQuantity;
    }

    @Override
    public final
    void setProcessByAddress( final Map< InetAddress, Map< Integer, Process > > processByAddress ) {
        this.processByAddress = processByAddress;
    }

    @Override
    public final
    void setProcessByPid( final Map< Integer, Process > processByPid ) {
        this.processByPid = processByPid;
    }
}
