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
            int name,
            Middleware middleware
    ) {
        super(name, middleware);
    }

    @Override
    public
    int getDataTypeSize() {
        return 8;
    }

    @Override
    public
    Double parseBytesToData(
            byte[] valueAsBytes,
            int valueBytesSize
    ) {
        return bytesToDouble(valueAsBytes, 0, valueBytesSize);
    }

    @Override
    public
    byte[] valueToBytesArray( Double value ) {
        return doubleToBytes(value);
    }
}
