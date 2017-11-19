package ar.edu.unrc.pellegrini.franco.utils;

import java.io.File;

public
class Configs {
    private final int distributedArraySize;
    private final int processQuantity;

    public
    Configs(
            int distributedArraySize,
            int processQuantity
    ) {
        this.distributedArraySize = distributedArraySize;
        this.processQuantity = processQuantity;
    }

    public
    Configs( final String configFilePath ) {
        File configFile = new File(configFilePath);
        if ( !configFile.exists() ) {
            throw new IllegalArgumentException("<config file path>.txt does not exists.");
        }
        this.distributedArraySize = 0;
        this.processQuantity = 0;
    }

    public
    int getDistributedArraySize() {
        return distributedArraySize;
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }
}
