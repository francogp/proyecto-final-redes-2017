package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMessage
        extends AbstractMessage< Long > {
    public static final int LONG_VALUE_PARAMETER_BYTE_SIZE = 8;

    public
    LongMessage() {
        super();
    }

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
    void initValueFromBytes( final byte[] bytes ) {
        valueParameter = bytesToLong(bytes, VALUE_PARAMETER_BYTE_INDEX, VALUE_PARAMETER_BYTE_INDEX + LONG_VALUE_PARAMETER_BYTE_SIZE);
    }

    @Override
    protected
    void initValueInBytes() {
        if ( valueParameter == null ) {
            valueParameter = 0L;
        }
        final byte[] value = longToBytes(valueParameter);
        System.arraycopy(value, 0, bytes, VALUE_PARAMETER_BYTE_INDEX, LONG_VALUE_PARAMETER_BYTE_SIZE);
    }
}
