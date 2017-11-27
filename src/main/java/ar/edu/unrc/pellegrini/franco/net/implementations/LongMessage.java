package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;
import ar.edu.unrc.pellegrini.franco.net.InvalidValueParameterException;
import ar.edu.unrc.pellegrini.franco.net.Message;

import java.util.Arrays;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

public final
class LongMessage
        extends AbstractMessage< Long > {
    public static final int LONG_VALUE_PARAMETER_BYTE_SIZE = 8;

    public static
    Message< Long > getInstance() {
        return new LongMessage();
    }

    @Override
    public
    int getValueByteLength() {
        return LONG_VALUE_PARAMETER_BYTE_SIZE;
    }

    @Override
    public
    void initBytesFromValue()
            throws InvalidValueParameterException {
        if ( value == null ) {
            value = 0L;
        }
        if ( !( value instanceof Long ) ) {
            throw new InvalidValueParameterException("value must be a Long value");
        }
        final byte[] value = longToBytes(this.value);
        System.arraycopy(value, 0, asBytes, VALUE_PARAMETER_BYTE_INDEX, LONG_VALUE_PARAMETER_BYTE_SIZE);
    }

    @Override
    public
    void initValueFromBytes( final byte[] bytes )
            throws InvalidValueParameterException {
        try {
            value = bytesToLong(bytes, VALUE_PARAMETER_BYTE_INDEX, VALUE_PARAMETER_BYTE_INDEX + LONG_VALUE_PARAMETER_BYTE_SIZE);
        } catch ( final RuntimeException e ) {
            throw new InvalidValueParameterException("Invalid bytesToLong conversion of " + Arrays.toString(bytes), e);
        }
    }
}
