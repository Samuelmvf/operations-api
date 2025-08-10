package br.com.astro.operations.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException() {
        super("Record not found");
    }

}
