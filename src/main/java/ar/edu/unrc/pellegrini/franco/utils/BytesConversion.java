package ar.edu.unrc.pellegrini.franco.utils;

public final
class BytesConversion {

    private
    BytesConversion() {
    }

    public static
    double bytesToDouble( final byte[] bytes ) {
        long result = 0L;
        for ( int i = 0; i < 8; i++ ) {
            result <<= 8L;
            result |= ( bytes[i] & 0xFFL );
        }
        return Double.longBitsToDouble(result);
    }

    public static
    double bytesToDouble(
            final byte[] bytes,
            final int from,
            final int upTo
    ) {
        long result = 0L;
        for ( int i = from; i < upTo; i++ ) {
            result <<= 8L;
            result |= ( bytes[i] & 0xFFL );
        }
        return Double.longBitsToDouble(result);
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
        final byte[] output = new byte[8];
        final long   lng    = Double.doubleToLongBits(d);
        for ( int i = 0; i < 8; i++ ) {
            output[i] = (byte) ( ( lng >> ( ( 7 - i ) << 3 ) ) & 0xff );
        }
        return output;
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
