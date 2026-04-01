package graphics.algorithms;

import graphics.AlgorithmPage;
import graphics.components.AlgoSoundManager;
import graphics.components.Column;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * <h3>Represents the Bubble Sort algorithm.</h3>
 * Implements the VisualSortAlgorithm abstract class and provides the logic for sorting arrays using the Bubble Sort algorithm.
 *
 * @author Andrea Maruca
 */
public class BubbleVisualSort extends VisualSortAlgorithm {

    public BubbleVisualSort(int delayMs) {
        super(
                "Bubble Sort",
                "Algoritmo di ordinamento semplice basato su confronti " +
                        "ripetuti tra elementi adiacenti. Scambia due elementi " +
                        "se sono nell’ordine sbagliato, “facendo salire” progressivamente" +
                        " i valori più grandi verso la fine dell’array.",
                "O(n²)",
                delayMs
        );
    }

    private final AtomicInteger i = new AtomicInteger(0);
    private final AtomicInteger j = new AtomicInteger(0);

    @Override
    public void internalReset() {
        i.set(0);
        j.set(0);
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p75, AlgorithmPage board, Consumer<UpdateInfo> update) {
        var cols = board.getBoard().columns;
        var code = board.getCode();

        if (i.get() >= arr.length - 1) {
            return false;
        }

        if (j.get() >= arr.length - i.get() - 1) {
            j.set(0);
            i.incrementAndGet();
            selectAndDeselectLines(update, code, 0);
            return i.get() < arr.length - 1;
        }

        int currentJ = j.get();

        Column c1 = cols.get(currentJ);
        Column c2 = cols.get(currentJ + 1);

        select(update, c1, c2);
        selectCodeLines(update, code, 2);
        AlgoSoundManager.scambio(getSuono(arr[currentJ], p25, p50, p75));

        compares++;
        if (arr[currentJ] > arr[currentJ + 1]) {
            deselectCodeLines(update, code, 2);
            long temp = arr[currentJ];
            selectAndDeselectLines(update, code, 3);
            arr[currentJ] = arr[currentJ + 1];
            selectAndDeselectLines(update, code, 4);
            arr[currentJ + 1] = temp;
            selectAndDeselectLines(update, code, 5);
            scambio(c1, c2);

            update.accept(new UpdateInfo(c1, c2));
        }

        deselect(update, c1, c2);

        j.incrementAndGet();
        selectAndDeselectLines(update, code, 1);
        return true;
    }

    @Override
    public void actualSort(long[] arr) {
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