package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;
import ar.edu.unrc.pellegrini.franco.net.InvalidValueParameterException;
import ar.edu.unrc.pellegrini.franco.net.Message;

import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToDouble;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.doubleToBytes;

public final
class DoubleMessage
        extends AbstractMessage< Double > {
    public static final int DOUBLE_VALUE_PARAMETER_BYTE_SIZE = 8;

    public static
    Message< Double > getInstance() {
        return new DoubleMessage();
    }

    @Override
    public
    int getValueByteLength() {
        return DOUBLE_VALUE_PARAMETER_BYTE_SIZE;
    }

    @Override
    public
    void initBytesFromValue()
            throws InvalidValueParameterException {
        if ( value == null ) {
            value = 0.0d;
        }
        if ( !( value instanceof Double ) ) {
            throw new InvalidValueParameterException("value must be a Long value");
        }
        final byte[] value = doubleToBytes(this.value);
        System.arraycopy(value, 0, asBytes, VALUE_PARAMETER_BYTE_INDEX, DOUBLE_VALUE_PARAMETER_BYTE_SIZE);
    }

    @Override
    public
    void initValueFromBytes( final byte[] bytes )
            throws InvalidValueParameterException {
        try {
            value = bytesToDouble(bytes, VALUE_PARAMETER_BYTE_INDEX, VALUE_PARAMETER_BYTE_INDEX + DOUBLE_VALUE_PARAMETER_BYTE_SIZE);
        } catch ( final RuntimeException e ) {
            throw new InvalidValueParameterException("Invalid bytesToLong conversion of " + Arrays.toString(bytes), e);
        }
    }
}
