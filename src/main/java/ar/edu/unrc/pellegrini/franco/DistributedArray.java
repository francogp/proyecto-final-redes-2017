package ar.edu.unrc.pellegrini.franco;

public
interface DistributedArray< I > {

    /**
     * Obtiene el valor en {@code index} del arreglo distribuido
     *
     * @param index del valor a obtener del arreglo distribuido
     *
     * @return valor ubicado en {@code index} en el arreglo distribuido
     */
    I get( final long index );

    /**
     * @return tamaño virtual del arreglo distribuido
     */
    long getRealSize();

    /**
     * @return tamaño real del arreglo en el proceso actual
     */
    int getSize();

    /**
     * @return true si el proceso actual es el último.
     */
    boolean imLast();

    /**
     * @return índice donde comienzan los valores del arreglo local del proceso actual.
     */
    long lowerIndex();

    /**
     * @return índice donde comienzan los valores del arreglo local del proceso {@code pid}.
     */
    long lowerIndex( final int pid );

    /**
     * Ubica {@code value} en el índice del arreglo {@code index}.
     *
     * @param index posición del arreglo distribuido.
     * @param value a ubicar.
     */
    void set(
            final long index,
            final I value
    );

    /**
     * Intercambia dos valores ubicados en {@code index1} e {@code index2} en el arreglo distribuido.
     *
     * @param index1 posición del valor 1.
     * @param index2 posición del valor 2.
     */
    void swap(
            final long index1,
            final long index2
    );

    /**
     * @return índice donde terminan los valores del arreglo local del proceso actual.
     */
    long upperIndex();

    /**
     * @return índice donde terminan los valores del arreglo local del proceso {@code pid}.
     */
    long upperIndex( final int pid );
}
