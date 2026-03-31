package graphics.algorithms.components;

public class ArrayNotSortedException extends RuntimeException {
    public String algoName;
    public ArrayNotSortedException(String algoName, String message) {
        super("ALGORITMO NON HA MESSO IN ORDINE: " + algoName + "\n" + message); this.algoName = algoName;
    }
}
