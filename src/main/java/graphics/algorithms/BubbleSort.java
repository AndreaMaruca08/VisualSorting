package graphics.algorithms;

import graphics.AlgorithmPage;
import graphics.components.Column;
import graphics.utilities.Dimensione;
import graphics.utilities.SoundManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h3>Represents the Bubble Sort algorithm.</h3>
 * Implements the SortAlgorithm abstract class and provides the logic for sorting arrays using the Bubble Sort algorithm.
 *
 * @author Andrea Maruca
 */
public class BubbleSort extends SortAlgorithm {

    public BubbleSort(int delayMs) {
        super(
                "Bubble sort",
                "Algoritmo di ordinamento semplice basato su confronti " +
                        "ripetuti tra elementi adiacenti. Scambia due elementi " +
                        "se sono nell’ordine sbagliato, “facendo salire” progressivamente" +
                        " i valori più grandi verso la fine dell’array.",
                "O(n^2)",
                delayMs
        );
    }

    private final AtomicInteger i = new AtomicInteger(0);
    private final AtomicInteger j = new AtomicInteger(0);

    @Override
    public void reset() {
        i.set(0);
        j.set(0);
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p75, AlgorithmPage board, Runnable update) {
        var cols = board.getBoard().columns;

        if (i.get() >= arr.length - 1) {
            return false;
        }

        if (j.get() >= arr.length - i.get() - 1) {
            j.set(0);
            i.incrementAndGet();
            return i.get() < arr.length - 1;
        }

        int currentJ = j.get();

        Column c1 = cols.get(currentJ);
        Column c2 = cols.get(currentJ + 1);

        c1.select();
        c2.select();
        update.run();
        SoundManager.scambio(getSuono(arr[currentJ], p25, p50, p75));
        sleep();

        if (arr[currentJ] > arr[currentJ + 1]) {
            Dimensione tempDim = c1.getDimensione();
            c1.setDimensione(c2.getDimensione());
            c2.setDimensione(tempDim);

            long temp = arr[currentJ];
            arr[currentJ] = arr[currentJ + 1];
            arr[currentJ + 1] = temp;

            update.run();
        }

        c1.deselect();
        c2.deselect();
        update.run();

        j.incrementAndGet();
        return true;
    }

    @Override
    void actualSort(long[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    long temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    @Override
    public String algoToString() {
        return """
                for (int i = 0; i < arr.length; i++) {
                    for (int j = 0; j < arr.length - i - 1; j++) {
                        if (arr[j] > arr[j + 1]) {
                            long temp = arr[j];
                            arr[j] = arr[j + 1];
                            arr[j + 1] = temp;
                        }
                    }
                }
                """;
    }
}