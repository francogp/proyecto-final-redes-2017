package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;
import ar.edu.unrc.pellegrini.franco.net.InvalidValueParameterException;

import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMessage
        extends AbstractMessage< Long > {
    public static final int LONG_VALUE_PARAMETER_BYTE_SIZE = 8;

    public static
    LongMessage getInstance() {
        return new LongMessage();
    }

    @Override
    public
    int getValueByteLength() {
        return LONG_VALUE_PARAMETER_BYTE_SIZE;
    }

    @Override
    protected
    void initValueFromBytes( final byte[] bytes )
            throws InvalidValueParameterException {
        try {
            valueParameter = bytesToLong(bytes, VALUE_PARAMETER_BYTE_INDEX, VALUE_PARAMETER_BYTE_INDEX + LONG_VALUE_PARAMETER_BYTE_SIZE);
        } catch ( final Exception e ) {
            throw new InvalidValueParameterException("Invalid bytesToLong conversion of " + Arrays.toString(bytes), e);
        }
    }

    @Override
    protected
    void initValueInBytes()
            throws InvalidValueParameterException {
        if ( valueParameter == null ) {
            valueParameter = 0L;
        }
        if ( !( valueParameter instanceof Long ) ) {
            throw new InvalidValueParameterException("valueParameter must be a Long value");
        }
        final byte[] value = longToBytes(valueParameter);
        System.arraycopy(value, 0, asBytes, VALUE_PARAMETER_BYTE_INDEX, LONG_VALUE_PARAMETER_BYTE_SIZE);
    }
}
