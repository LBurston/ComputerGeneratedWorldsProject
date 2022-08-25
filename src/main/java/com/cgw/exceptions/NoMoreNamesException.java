package com.cgw.exceptions;

/**
 * Custom Subclass Exception from Generation Failure to determine an issue in generating a unique new Name.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class NoMoreNamesException extends GenerationFailureException {

    /**
     * Provides a message of where no more names could be found.
     * @param message Custom message at point of failure.
     */
    public NoMoreNamesException(String message) {
        super(message);
    }

}
