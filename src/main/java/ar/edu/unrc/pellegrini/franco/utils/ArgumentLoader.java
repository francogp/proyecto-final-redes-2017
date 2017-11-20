package ar.edu.unrc.pellegrini.franco.utils;

import java.util.*;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

/**
 * Parsea argumentos de un programa (main) en una estructura amigable para su uso. Los argumentos deben tener el estilo:
 * <p>
 * arg1=2 arg44=[0.0,55.8]
 *
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public
class ArgumentLoader {

    public static final String ARRAY_STRING_FORMAT_UNKNOWN = "ArrayString format unknown: ";
    private final Set< String >         activeFlags;
    private final Map< String, String > argMap;
    private final boolean               exitIfNotValid;
    private final Set< String >         validArgs;
    private final Set< String >         validFlags;

    @SuppressWarnings( "CollectionWithoutInitialCapacity" )
    public
    ArgumentLoader( final boolean exitIfNotValid ) {
        this.exitIfNotValid = exitIfNotValid;
        argMap = new HashMap<>();
        activeFlags = new HashSet<>();
        validFlags = new HashSet<>();
        validArgs = new HashSet<>();
    }

    public
    void addValidArg( final String validArg ) {
        validArgs.add(validArg);
    }

    public
    void addValidFlag( final String validFlag ) {
        validFlags.add(validFlag);
    }

    public
    boolean existsFlag( final String id ) {
        return activeFlags.contains(id);
    }

    /**
     * @param id identificador del argumento.
     *
     * @return valor que contiene argumento.
     */
    private
    String getArg( final String id ) {
        final String value = argMap.get(id);
        if ( value == null ) {
            throw new IllegalArgumentException("Argument not present: " + id);
        }
        return argMap.get(id);
    }

    public
    void loadArguments( final String... args ) {
        for ( final String arg : args ) {
            if ( arg.charAt(0) == '-' ) {
                final String flag = arg.substring(1);
                if ( !validFlags.contains(flag) ) {
                    if ( exitIfNotValid ) {
                        printUsage();
                        System.exit(1);
                    }
                    throw new IllegalArgumentException("Flag \"" + arg + "\" is not present as a valid flag set. Args = " + Arrays.toString(args));
                }
                activeFlags.add(flag);
            } else {
                final int index = arg.indexOf('=');
                if ( index == -1 ) {
                    if ( exitIfNotValid ) {
                        printUsage();
                        System.exit(1);
                    }
                    throw new IllegalArgumentException("Argument not recognized: " + arg + ". Args = " + Arrays.toString(args));
                }
                final String id = arg.substring(0, index).trim();
                if ( !validArgs.contains(id) ) {
                    if ( exitIfNotValid ) {
                        printUsage();
                        System.exit(1);
                    }
                    throw new IllegalArgumentException(
                            "Argument \"" + arg + "\" is not present as a valid argument set" + ". Args = " + Arrays.toString(args));
                }
                final String value = arg.substring(index + 1).trim();
                argMap.put(id, value);
            }
        }
    }

    public
    boolean parseBoolean( final String id ) {
        return Boolean.parseBoolean(getArg(id));
    }

    /**
     * Parsea arreglos de Boolean. Formato del estilo [true,false,false]
     *
     * @return lista de Boolean.
     */
    public
    boolean[] parseBooleanArray( final String id ) {
        final String arrayString = getArg(id);
        if ( ( arrayString == null ) || !( !arrayString.isEmpty() && ( arrayString.charAt(0) == '[' ) ) ||
             !( !arrayString.isEmpty() && ( arrayString.charAt(arrayString.length() - 1) == ']' ) ) ) {
            throw new IllegalArgumentException(ARRAY_STRING_FORMAT_UNKNOWN + arrayString);
        }

        final String[]  list = arrayString.substring(1, arrayString.length() - 1).split(",");
        final boolean[] out  = new boolean[list.length];
        int             i    = 0;
        for ( final String boolString : list ) {
            out[i] = Boolean.parseBoolean(boolString.trim());
            i++;
        }
        return out;
    }

    public
    double parseDouble( final String id ) {
        return Double.parseDouble(getArg(id));
    }

    /**
     * Parsea arreglos de Double. Formato del estilo [0.0,0.1,0.2,5547]
     *
     * @return lista de Double.
     */
    public
    List< Double > parseDoubleArray( final String id ) {
        final String arrayString = getArg(id);
        if ( ( arrayString == null ) || !( !arrayString.isEmpty() && ( arrayString.charAt(0) == '[' ) ) ||
             !( !arrayString.isEmpty() && ( arrayString.charAt(arrayString.length() - 1) == ']' ) ) ) {
            throw new IllegalArgumentException(ARRAY_STRING_FORMAT_UNKNOWN + arrayString);
        }

        final String[]       list = arrayString.substring(1, arrayString.length() - 1).split(",");
        final List< Double > out  = new ArrayList<>(list.length);
        for ( final String number : list ) {
            out.add(Double.parseDouble(number.trim()));
        }
        return out;
    }

    public
    float parseFloat( final String id ) {
        return Float.parseFloat(getArg(id));
    }

    public
    int parseInteger( final String id ) {
        return Integer.parseInt(getArg(id));
    }

    /**
     * Parsea arreglos de Integer. Formato del estilo [0,1,2,5547]
     *
     * @return lista de Integer.
     */
    public
    List< Integer > parseIntegerArray( final String id ) {
        final String arrayString = getArg(id);
        if ( ( arrayString == null ) || !( !arrayString.isEmpty() && ( arrayString.charAt(0) == '[' ) ) ||
             !( !arrayString.isEmpty() && ( arrayString.charAt(arrayString.length() - 1) == ']' ) ) ) {
            throw new IllegalArgumentException(ARRAY_STRING_FORMAT_UNKNOWN + arrayString);
        }

        final String[]        list = arrayString.substring(1, arrayString.length() - 1).split(",");
        final List< Integer > out  = new ArrayList<>(list.length);
        for ( final String number : list ) {
            out.add(Integer.parseInt(number.trim()));
        }
        return out;
    }

    public
    long parseLong( final String id ) {
        return Long.parseLong(getArg(id));
    }

    public
    String parseString( final String id ) {
        return getArg(id);
    }

    /**
     * Parsea arreglos de Strings. Formato del estilo [clase1,clase-2,clase_3]
     *
     * @return lista de Strings.
     */
    public
    List< String > parseStringArray( final String id ) {
        final String arrayString = getArg(id);
        if ( ( arrayString == null ) || !( !arrayString.isEmpty() && ( arrayString.charAt(0) == '[' ) ) ||
             !( !arrayString.isEmpty() && ( arrayString.charAt(arrayString.length() - 1) == ']' ) ) ) {
            throw new IllegalArgumentException(ARRAY_STRING_FORMAT_UNKNOWN + arrayString);
        }
        return Arrays.asList(arrayString.substring(1, arrayString.length() - 1).split(","));
    }

    private
    void printUsage() {
        if ( validFlags.isEmpty() && validArgs.isEmpty() ) {
            getLogger(ArgumentLoader.class.getName()).log(Level.SEVERE, "No valid flags or arguments configured");
        } else {
            if ( !validFlags.isEmpty() ) {
                getLogger(ArgumentLoader.class.getName()).log(Level.SEVERE, "Valid flags " + validFlags);
            }
            if ( !validArgs.isEmpty() ) {
                getLogger(ArgumentLoader.class.getName()).log(Level.SEVERE, "Valid arguments " + validArgs);
            }
        }
    }
}
