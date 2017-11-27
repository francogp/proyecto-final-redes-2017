package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.DistributedArray;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToDouble;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.doubleToBytes;

public
class DoubleDistributedArray
        extends DistributedArray< Double > {
    /**
     * @param name       PGAS unique name.
     * @param middleware to register this PGAS.
     */
    public
    DoubleDistributedArray(
            final int name,
            final Middleware middleware
    ) {
        super(name, middleware);
    }

    @Override
    public final
    int getDataTypeSize() {
        return 8;
    }

    @Override
    public final
    Double parseBytesToData(
            final byte[] valueAsBytes,
            final int valueBytesSize
    ) {
        return bytesToDouble(valueAsBytes, 0, valueBytesSize);
    }

    @Override
    public final
    byte[] valueToBytesArray( final Double value ) {
        return doubleToBytes(value);
    }
}
