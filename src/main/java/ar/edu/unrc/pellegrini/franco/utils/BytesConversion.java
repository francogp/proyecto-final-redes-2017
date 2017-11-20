package ar.edu.unrc.pellegrini.franco.utils;

import java.io.UnsupportedEncodingException;

public
class BytesConversion {

    public static
    long bytesToLong(
            final byte[] b,
            final int from,
            final int upTo
    ) {
        long result = 0;
        for ( int i = from; i < upTo; i++ ) {
            result <<= 8;
            result |= ( b[i] & 0xFF );
        }
        return result;
    }

    public static
    long bytesToLong( byte[] b ) {
        long result = 0;
        for ( int i = 0; i < 8; i++ ) {
            result <<= 8;
            result |= ( b[i] & 0xFF );
        }
        return result;
    }

    public static
    byte[] longToBytes( long l ) {
        byte[] result = new byte[8];
        for ( int i = 7; i >= 0; i-- ) {
            result[i] = (byte) ( l & 0xFF );
            l >>= 8;
        }
        return result;
    }

    public static
    void main( String[] args )
            throws UnsupportedEncodingException {
        System.out.println(new String("B").getBytes("UTF-8").length);
        System.out.println(longToBytes(Long.MIN_VALUE).length);
        System.out.println(longToBytes(Long.MAX_VALUE).length);
    }
}
