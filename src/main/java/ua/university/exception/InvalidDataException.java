package ua.university.exception;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException(){
        super();
    }

    public InvalidDataException(String message){
        super(message);
    }

    public InvalidDataException(Throwable e){
        super(e);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
