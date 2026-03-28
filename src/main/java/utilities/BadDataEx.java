package utilities;

public class BadDataEx extends RuntimeException {
    public BadDataEx(String message) {
        super("ERRORE CREAZIONE ARRAY: "+message);
    }
}
