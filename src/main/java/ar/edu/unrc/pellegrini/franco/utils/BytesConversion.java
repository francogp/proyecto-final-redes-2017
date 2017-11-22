package ar.edu.unrc.pellegrini.franco.utils;

public final
class BytesConversion {

    private
    BytesConversion() {
    }

    public static
    double bytesToDouble( final byte[] bytes ) {
        return Double.longBitsToDouble(bytesToLong(bytes));
    }

    public static
    double bytesToDouble(
            final byte[] bytes,
            final int from,
            final int upTo
    ) {
        return Double.longBitsToDouble(bytesToLong(bytes, from, upTo));
    }

    public static
    long bytesToLong( final byte[] bytes ) {
        long result = 0L;
        for ( int i = 0; i < 8; i++ ) {
            result <<= 8L;
            result |= ( bytes[i] & 0xFF );
        }
        return result;
    }

    public static
    long bytesToLong(
            final byte[] bytes,
            final int from,
            final int upTo
    ) {
        long result = 0L;
        for ( int i = from; i < upTo; i++ ) {
            result <<= 8L;
            result |= ( bytes[i] & 0xFF );
        }
        return result;
    }

    public static
    byte[] doubleToBytes( final double d ) {
        return longToBytes(Double.doubleToLongBits(d));
    }

    @SuppressWarnings( "AssignmentToMethodParameter" )
    public static
    byte[] longToBytes( long l ) {
        final byte[] result = new byte[8];
        for ( int i = 7; i >= 0; i-- ) {
            result[i] = (byte) ( l & 0xFF );
            l >>= 8L;
        }
        return result;
    }
}
