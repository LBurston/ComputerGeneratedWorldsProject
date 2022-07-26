package exceptions;

public class NoMoreNamesException extends GenerationFailureException {

    public NoMoreNamesException(String message) {
        super(message);
    }

}
