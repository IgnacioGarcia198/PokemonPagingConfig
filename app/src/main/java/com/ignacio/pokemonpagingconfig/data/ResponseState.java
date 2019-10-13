package com.ignacio.pokemonpagingconfig.data;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
/*public enum ResponseState {
    NO_NETWORK,NO_NEED_TO_DOWNLOAD,DOWNLOAD_SUCCESSFUL,DOWNLOAD_UNSUCCESSFUL,EMPTY_DATA,DOWNLOAD_FAILURE;
    private boolean fatal; // False by default...
    private String message;

    public boolean isFatal() {
        return fatal;
    }

    public ResponseState setFatal(boolean fatal) {
        this.fatal = fatal;
        return this;
    }

    //public ResponseState
}*/

public class ResponseState {
    // ... type definitions
    public final int value;
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({NO_NETWORK,NO_NEED_TO_DOWNLOAD,DOWNLOAD_SUCCESSFUL,DOWNLOAD_UNSUCCESSFUL,EMPTY_DATA,DOWNLOAD_FAILURE})
    // Create an interface for validating int types
    public @interface ResponseStateDef {}
    // Declare the constants
    public static final int NO_NEED_TO_DOWNLOAD = 0;
    public static final int DOWNLOAD_SUCCESSFUL = 1;
    public static final int NO_NETWORK = -1;
    public static final int DOWNLOAD_UNSUCCESSFUL = -2;
    public static final int EMPTY_DATA = -3;
    public static final int DOWNLOAD_FAILURE = -4;

    // Mark the argument as restricted to these enumerated types
    public ResponseState(@ResponseStateDef int responseState) {
        this.value = responseState;
        this.fatal = fatal;
    }
    private boolean fatal;

    public ResponseState setFatal(boolean fatal) {
        this.fatal = fatal;
        return this;
    }

    public boolean isFatal() {
        return fatal;
    }
}
