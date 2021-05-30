package rifleRange.error;

public class NumOfLevelsException extends Exception {

    String message;

    public NumOfLevelsException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
