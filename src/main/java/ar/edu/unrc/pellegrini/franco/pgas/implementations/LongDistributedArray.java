package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.DistributedArray;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;

public
class LongDistributedArray
        extends DistributedArray< Long > {
    /**
     * @param name       PGAS unique name.
     * @param middleware to register this PGAS.
     */
    public
    LongDistributedArray(
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
    Long parseBytesToData(
            final byte[] valueAsBytes,
            final int valueBytesSize
    ) {
        return bytesToLong(valueAsBytes, 0, valueBytesSize);
    }

    @Override
    public final
    byte[] valueToBytesArray( final Long value ) {
        return longToBytes(value);
    }
}
