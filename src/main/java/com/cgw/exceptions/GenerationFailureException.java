package com.cgw.exceptions;

/**
 * Custom Exception created to determine failures in generating a Feature.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class GenerationFailureException extends Exception {

    /**
     * Provides a message of where generation went wrong.
     * @param message Custom message at point of failure.
     */
    public GenerationFailureException(String message) {
        super(message);
    }

}
