package com.cgw;

/**
 * A Buffered Main class to launch the Application.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class Main {

    /**
     * A Buffer method to start the Application process, as JavaFX cannot launch straight from an Application class.
     * @param args Arguments passed.
     */
    public static void main(String[] args) {
        CGWApp.main(args);
    }
}
