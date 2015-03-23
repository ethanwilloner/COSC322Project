package ai;

/**
 * Created by ethan on 22/03/15.
 */
public class IllegalMoveException extends Exception{
    public IllegalMoveException() {
    }

    public IllegalMoveException(String message) {
        super(message);
    }
}
