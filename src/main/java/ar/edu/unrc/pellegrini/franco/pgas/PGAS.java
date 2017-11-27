package ar.edu.unrc.pellegrini.franco.pgas;

/**
 * In computer science, a partitioned global address space (PGAS) is a parallel programming model. It assumes a global memory address space that is
 * logically partitioned and a portion of it is local to each process, thread, or processing element. The novelty of PGAS is that the portions of the
 * shared memory space may have an affinity for a particular process, thereby exploiting locality of reference.
 *
 * @param <I> value type to be stored in the PGAS.
 */
public
interface PGAS< I > {

    /**
     * @return String representation of the complete PGAS (including all processes). null if the current process is not the coordinator.
     */
    String asString();

    /**
     * @return PGAS name.
     */
    int getName();

    /**
     * @return size of the current PGAS process.
     */
    int getSize();

    /**
     * @return lower PGAS index supported by this process.
     */
    long lowerIndex();

    /**
     * @param pid process id.
     *
     * @return lower PGAS index supported by the process id pid.
     */
    long lowerIndex( final int pid );

    /**
     * @param index of the value type I to read. May be located on other process.
     *
     * @return value located on the position index, inside the PGAS memory.
     *
     * @throws Exception
     */
    I read( final long index )
            throws Exception;

    /**
     * @param enable true if must show debug logs.
     */
    void setDebugMode( boolean enable );

    /**
     * Swap to values inside a PGAS. May not be local process indexes.
     *
     * @param index1
     * @param index2
     *
     * @throws Exception
     */
    void swap(
            final long index1,
            final long index2
    )
            throws Exception;

    /**
     * @return upper PGAS index supported by this process.
     */
    long upperIndex();

    /**
     * @param pid process id.
     *
     * @return upper PGAS index supported by the process id pid.
     */
    long upperIndex( final int pid );

    /**
     * Writes a value on the index position inside a PGAS. May not be local process indexes.
     *
     * @param index of the value type I to write. May be located on other process.
     * @param value to write.
     *
     * @throws Exception
     */
    void write(
            final long index,
            final I value
    )
            throws Exception;
}
