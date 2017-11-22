package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.pgas.PGAS;

import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractDistributedBubbleSort< I extends Comparable< I > >
        implements DistributedBubbleSort< I > {

    private final PGAS< I > pgas;
    private       String    result;

    protected
    AbstractDistributedBubbleSort(
            final int pid,
            final String configFilePath
    ) {
        pgas = newPGAS(pid, configFilePath);
    }

    protected abstract
    PGAS< I > newPGAS(
            final int pid,
            final String configFilePath
    );

    @Override
    public final
    String result() {
        return result;
    }

    @Override
    public final
    void run() {
        try {
            boolean finish = false;

            while ( !finish ) {
                finish = true;
                final long upperIndex = pgas.upperIndex();
                final long lowerIndex = pgas.lowerIndex();

                // sort local block
                DistributedBubbleSort.bubbleSort(pgas, lowerIndex, upperIndex);

                pgas.barrier();

                if ( !pgas.imLast() ) {
                    final long lowerIndexRight = pgas.lowerIndex(pgas.getPid() + 1);
                    if ( pgas.read(upperIndex).compareTo(pgas.read(lowerIndexRight)) > 0 ) {
                        pgas.swap(upperIndex, lowerIndexRight);
                        finish = false;  // update local copy
                    }
                }
                // reduce finish by and, then replicate result
                finish = pgas.andReduce(finish);
            }

            if ( pgas.isCoordinator() ) {
                result = pgas.asString();
            }

            pgas.endService();
        } catch ( final Exception e ) {
            getLogger(AbstractDistributedBubbleSort.class.getName()).log(Level.SEVERE, "Unknown problem", e);
        }
    }
}
