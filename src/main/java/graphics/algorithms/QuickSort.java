package graphics.algorithms;

import graphics.AlgorithmPage;
import graphics.algorithms.components.SortAlgorithm;
import graphics.components.Column;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.function.Consumer;

public class QuickSort extends SortAlgorithm {
    public QuickSort(int delayMs) {
        super(
                "QuickSort",
                """
                        Algoritmo di ordinamento basato su divide et impera.
                        Seleziona un elemento detto pivot e riorganizza l’array in modo che:
                        tutti gli elementi minori del pivot siano a sinistra
                        tutti i maggiori a destra
                        Poi applica ricorsivamente lo stesso processo alle due parti.""",
                "O(n log n)",
                delayMs
        );
    }

    private static final class Frame {
        final int low;
        final int high;
        int stage;
        int pivotIndex;

        Frame(int low, int high) {
            this.low = low;
            this.high = high;
            this.stage = 0;
            this.pivotIndex = -1;
        }
    }

    private final ArrayDeque<Frame> stack = new ArrayDeque<>();

    private boolean partitioning = false;
    private int partHigh;
    private int i, j;
    private long pivot;
    
    private final HashMap<Integer, int[]> activePivots = new HashMap<>();

    @Override
    public void internalReset() {
        stack.clear();
        partitioning = false;
        partHigh = 0;
        i = 0;
        j = 0;
        pivot = 0;
        activePivots.clear();
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p75, AlgorithmPage board, Consumer<UpdateInfo> update) {
        if (arr.length < 2) return false;

        if (stack.isEmpty() && !partitioning) {
            stack.push(new Frame(0, arr.length - 1));
        }

        var cols = board.getBoard().columns;

        if (partitioning) {
            if (j < partHigh) {
                compares++;
                Column c1 = cols.get(j);

                if (arr[j] < pivot) {
                    i++;
                    if (i != j) {
                        swaps++;
                        swap(arr, i, j);
                        scambioCompleto(cols.get(i), c1, update, getSuono(arr[i], p25, p50, p75));
                    }
                }
                j++;
                return true;
            }

            swaps++;
            swap(arr, i + 1, partHigh);
            scambioCompleto(cols.get(i + 1), cols.get(partHigh), update, getSuono(arr[i + 1], p25, p50, p75));

            int pivotIndex = i + 1;
            partitioning = false;

            Frame current = stack.peek();
            if (current != null) {
                current.pivotIndex = pivotIndex;
                current.stage = 1;
            }
            return true;
        }

        if (stack.isEmpty()) return false;

        Frame f = stack.peek();

        if (f.low >= f.high) {
            stack.pop();
            return !stack.isEmpty();
        }

        if (f.stage == 0) {
            partitioning = true;
            partHigh = f.high;
            pivot = arr[f.high];
            i = f.low - 1;
            j = f.low;
            return true;
        }

        if (f.stage == 1) {
            stack.pop();
            int pivotIndex = f.pivotIndex;

            activePivots.put(pivotIndex, new int[]{f.low, f.high});
            specialSelect(update, cols.get(pivotIndex));

            boolean hasLeft = f.low < pivotIndex - 1;
            boolean hasRight = pivotIndex + 1 < f.high;

            if (hasRight) {
                stack.push(new Frame(pivotIndex + 1, f.high));
            }
            if (hasLeft) {
                stack.push(new Frame(f.low, pivotIndex - 1));
            }

            var iterator = activePivots.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                int pIdx = entry.getKey();
                int[] bounds = entry.getValue();
                
                boolean stillActive = false;
                for (Frame frame : stack) {
                    if (frame.low >= bounds[0] && frame.high <= bounds[1]) {
                        stillActive = true;
                        break;
                    }
                }
                
                if (!stillActive) {
                    deselect(update, cols.get(pIdx));
                    iterator.remove();
                }
            }

            return !stack.isEmpty();
        }

        return false;
    }

    @Override
    public void actualSort(long[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public void quickSort(long[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);

            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private int partition(long[] arr, int low, int high) {
        long pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(long[] arr, int i, int j) {
        long temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    @Override
    public String algoToString() {
        return """
                    public void quickSort(long[] arr, int low, int high) {
                        if (low < high) {
                            int pivotIndex = partition(arr, low, high);
                
                            quickSort(arr, low, pivotIndex - 1);
                            quickSort(arr, pivotIndex + 1, high);
                        }
                    }
                    private int partition(long[] arr, int low, int high) {
                        long pivot = arr[high];
                        int i = low - 1;
                
                        for (int j = low; j < high; j++) {
                            if (arr[j] < pivot) {
                                i++;
                                swap(arr, i, j);
                            }
                        }
                
                        swap(arr, i + 1, high);
                        return i + 1;
                    }
                    private void swap(long[] arr, int i, int j) {
                        long temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;
                    }
                """;
    }
}
