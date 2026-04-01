package graphics.algorithms;

import graphics.AlgorithmPage;
import graphics.components.AlgoSoundManager;
import graphics.components.Column;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CockTailShakerVisualSort extends VisualSortAlgorithm {
    public CockTailShakerVisualSort(int delayMs) {
        super(
                "CocktailShaker Sort",
                "A bidirectional bubble sort algorithm",
                "O(n^2)",
                delayMs
        );
    }

    private final AtomicInteger i = new AtomicInteger(0);
    private final AtomicInteger j = new AtomicInteger(0);
    private final AtomicInteger start = new AtomicInteger(0);
    private final AtomicInteger end = new AtomicInteger(-1);
    private volatile boolean forward = true;
    private volatile boolean swappedInPass = false;

    @Override
    public void internalReset() {
        i.set(0);
        j.set(0);
        start.set(0);
        end.set(-1);
        forward = true;
        swappedInPass = false;
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p85, AlgorithmPage board, Consumer<UpdateInfo> update) {
        var cols = board.getBoard().columns;
        var code = board.getCode();

        if (end.get() == -1) {
            end.set(arr.length - 1);
            i.set(start.get());
            j.set(end.get());
        }

        if (forward) {
            if (i.get() >= end.get()) {
                if (!swappedInPass) {
                    return false;
                }
                swappedInPass = false;
                end.decrementAndGet();
                j.set(end.get());
                forward = false;
                return true;
            }

            int currentI = i.get();
            Column c1 = cols.get(currentI);
            Column c2 = cols.get(currentI + 1);

            compares++;
            selectAndDeselectLines(update, code, 10);
            if (arr[currentI] > arr[currentI + 1]) {
                selectAndDeselectLines(update, code, 34);
                swap(arr, currentI, currentI + 1);
                selectAndDeselectLines(update, code, 35);
                scambioCompleto(c1, c2, update, getSuono(arr[currentI], p25, p50, p85));
                selectAndDeselectLines(update, code, 36);
                update.accept(new UpdateInfo(c1, c2));
                swappedInPass = true;
            }
            i.incrementAndGet();
        } else {
            if (j.get() <= start.get()) {
                start.incrementAndGet();
                i.set(start.get());
                forward = true;
                swappedInPass = false;
                return true;
            }

            int currentJ = j.get();
            Column c1 = cols.get(currentJ);
            Column c2 = cols.get(currentJ - 1);

            select(update, c1, c2);
            AlgoSoundManager.scambio(getSuono(arr[currentJ], p25, p50, p85));

            compares++;
            selectAndDeselectLines(update, code, 23);
            if (arr[currentJ] < arr[currentJ - 1]) {
                selectAndDeselectLines(update, code, 34);
                swap(arr, currentJ, currentJ - 1);
                selectAndDeselectLines(update, code, 35);
                scambio(c1, c2);
                selectAndDeselectLines(update, code, 36);
                update.accept(new UpdateInfo(c1, c2));
                swappedInPass = true;
            }

            deselect(update, c1, c2);
            j.decrementAndGet();
        }

        return true;
    }

    @Override
    public void actualSort(long[] arr) {
        boolean swapped = true;
        int start = 0;
        int end = arr.length - 1;

        while (swapped) {
            swapped = false;

            // forward pass
            for (int i = start; i < end; i++) {
                if (arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                    swapped = true;
                }
            }

            if (!swapped) break;

            swapped = false;
            end--;

            // backward pass
            for (int i = end; i > start; i--) {
                if (arr[i] < arr[i - 1]) {
                    swap(arr, i, i - 1);
                    swapped = true;
                }
            }

            start++;
        }
    }

    private void swap(long[] arr, int i, int j) {
        long temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    @Override
    public String algoToString() {
        return """
                public void cocktailShakerSort(int[] arr) {
                    boolean swapped = true;
                    int start = 0;
                    int end = arr.length - 1;
            
                    while (swapped) {
                        swapped = false;
            
                        // forward pass
                        for (int i = start; i < end; i++) {
                            if (arr[i] > arr[i + 1]) {
                                swap(arr, i, i + 1);
                                swapped = true;
                            }
                        }
            
                        if (!swapped) break;
            
                        swapped = false;
                        end--;
            
                        // backward pass
                        for (int i = end; i > start; i--) {
                            if (arr[i] < arr[i - 1]) {
                                swap(arr, i, i - 1);
                                swapped = true;
                            }
                        }
            
                        start++;
                    }
                }
            
                private void swap(int[] arr, int i, int j) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
                """;
    }
}
