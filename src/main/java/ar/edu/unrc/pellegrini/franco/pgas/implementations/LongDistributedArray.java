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
    Long parseBytesToData(
            byte[] valueAsBytes,
            int valueBytesSize
    ) {
        return bytesToLong(valueAsBytes, 0, valueBytesSize);
    }

    @Override
    public
    byte[] valueToBytesArray( Long value ) {
        return longToBytes(value);
    }
}
