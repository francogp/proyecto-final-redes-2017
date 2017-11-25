package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;
import ar.edu.unrc.pellegrini.franco.net.InvalidValueParameterException;

import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToDouble;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.doubleToBytes;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DoubleMessage
        extends AbstractMessage< Double > {
    public static final int DOUBLE_VALUE_PARAMETER_BYTE_SIZE = 8;

    public static
    DoubleMessage getInstance() {
        return new DoubleMessage();
    }

    @Override
    public
    int getValueByteLength() {
        return DOUBLE_VALUE_PARAMETER_BYTE_SIZE;
    }

    @Override
    protected
    void initValueFromBytes( final byte[] bytes )
            throws InvalidValueParameterException {
        try {
            valueParameter = bytesToDouble(bytes, VALUE_PARAMETER_BYTE_INDEX, VALUE_PARAMETER_BYTE_INDEX + DOUBLE_VALUE_PARAMETER_BYTE_SIZE);
        } catch ( final Exception e ) {
            throw new InvalidValueParameterException("Invalid bytesToLong conversion of " + Arrays.toString(bytes), e);
        }
    }

    @Override
    protected
    void initValueInBytes()
            throws InvalidValueParameterException {
        if ( valueParameter == null ) {
            valueParameter = 0.0d;
        }
        if ( !( valueParameter instanceof Double ) ) {
            throw new InvalidValueParameterException("valueParameter must be a Long value");
        }
        final byte[] value = doubleToBytes(valueParameter);
        System.arraycopy(value, 0, asBytes, VALUE_PARAMETER_BYTE_INDEX, DOUBLE_VALUE_PARAMETER_BYTE_SIZE);
    }
}
