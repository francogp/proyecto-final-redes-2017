package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.AbstractMessage;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToDouble;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.doubleToBytes;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DoubleMessage
        extends AbstractMessage< Double > {
    public static final int DOUBLE_VALUE_PARAMETER_BYTE_SIZE = 8;

    public
    DoubleMessage() {
        super();
    }

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
    void initValueFromBytes( final byte[] bytes ) {
        valueParameter = bytesToDouble(bytes, VALUE_PARAMETER_BYTE_INDEX, VALUE_PARAMETER_BYTE_INDEX + DOUBLE_VALUE_PARAMETER_BYTE_SIZE);
    }

    @Override
    protected
    void initValueInBytes() {
        if ( valueParameter == null ) {
            valueParameter = 0.0d;
        }
        final byte[] value = doubleToBytes(valueParameter);
        System.arraycopy(value, 0, bytes, VALUE_PARAMETER_BYTE_INDEX, DOUBLE_VALUE_PARAMETER_BYTE_SIZE);
    }
}
