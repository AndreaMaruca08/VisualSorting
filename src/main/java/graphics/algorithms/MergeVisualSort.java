package graphics.algorithms;

import core.utilities.Dimensione;
import graphics.AlgorithmPage;
import graphics.components.AlgoSoundManager;
import graphics.components.Column;
import utilities.ArraysFactory;

import java.util.ArrayDeque;
import java.util.function.Consumer;

public class MergeVisualSort extends VisualSortAlgorithm {

    public MergeVisualSort(int delayMs) {
        super(
                "Merge Sort",
                "Algoritmo di ordinamento basato sul paradigma divide et impera:\n" +
                        "divide l’array in parti sempre più piccole, le ordina e poi le ricombina" +
                        " (merge) in modo ordinato.",
                "O(n log n)",
                delayMs
        );
    }

    private static final class Frame {
        final int left;
        final int right;
        int stage;

        Frame(int left, int right, int stage) {
            this.left = left;
            this.right = right;
            this.stage = stage;
        }
    }

    private final ArrayDeque<Frame> stack = new ArrayDeque<>();

    private int mergeLeft = -1;
    private int mergeMid = -1;
    private int mergeRight = -1;

    private long[] temp = null;
    private int i = 0;
    private int j = 0;
    private int k = 0;
    private int tempIndex = 0;
    private boolean merging = false;
    private boolean copyingBack = false;

    @Override
    public void internalReset() {
        stack.clear();
        mergeLeft = -1;
        mergeMid = -1;
        mergeRight = -1;
        temp = null;
        i = 0;
        j = 0;
        k = 0;
        tempIndex = 0;
        merging = false;
        copyingBack = false;
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p75, AlgorithmPage board, Consumer<UpdateInfo> update) {
        if (arr.length < 2) return false;

        if (stack.isEmpty() && !merging && !copyingBack) {
            stack.push(new Frame(0, arr.length - 1, 0));
        }

        var cols = board.getBoard().columns;

        if (merging) {
            if(ArraysFactory.isSorted(arr))
                return false;
            if (i <= mergeMid && j <= mergeRight) {
                compares++;
                Column c1 = cols.get(i);
                Column c2 = cols.get(j);
                AlgoSoundManager.scambio(getSuono(arr[i], p25, p50, p75));

                if (arr[i] <= arr[j]) {
                    temp[tempIndex++] = arr[i++];
                } else {
                    temp[tempIndex++] = arr[j++];
                }
                update.accept(new UpdateInfo(c1, c2));
                return true;
            }


            while (i <= mergeMid) {
                temp[tempIndex++] = arr[i++];
                return true;
            }

            while (j <= mergeRight) {
                temp[tempIndex++] = arr[j++];
                return true;
            }

            copyingBack = true;
            merging = false;
            k = mergeLeft;
            tempIndex = 0;
            return true;
        }

        if (copyingBack) {
            if (k > mergeRight) {
                copyingBack = false;
                mergeLeft = -1;
                mergeMid = -1;
                mergeRight = -1;
                temp = null;
                return true;
            }

            long value = temp[tempIndex++];
            arr[k] = value;

            Column col = cols.get(k);
            Dimensione originalDim = col.getDimensione();
            col.setDimensione(originalDim.ingrandisci(30, 30));

            select(update, col);
            update.accept(new UpdateInfo(col));

            col.setDimensione(originalDim);
            deselect(update, col);
            update.accept(new UpdateInfo(col));

            k++;
            return true;
        }

        if (stack.isEmpty()) return false;

        Frame f = stack.pop();

        if (f.left >= f.right) {
            return true;
        }

        int mid = f.left + (f.right - f.left) / 2;

        if (f.stage == 0) {
            stack.push(new Frame(f.left, f.right, 1));
            stack.push(new Frame(mid + 1, f.right, 0));
            stack.push(new Frame(f.left, mid, 0));
            return true;
        }

        mergeLeft = f.left;
        mergeMid = mid;
        mergeRight = f.right;

        temp = new long[mergeRight - mergeLeft + 1];
        i = mergeLeft;
        j = mergeMid + 1;
        tempIndex = 0;
        merging = true;

        return true;
    }

    @Override
    public void actualSort(long[] arr) {
        mergeSort(arr, 0, arr.length - 1);
    }

    public void mergeSort(long[] arr, long left, long right) {
        if (left >= right) return;

        long mid = left + (right - left) / 2;

        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);

        merge(arr, left, mid, right);
    }

    private void merge(long[] arr, long left, long mid, long right) {
        long[] temp = new long[Math.toIntExact(right - left + 1)];

        int i = (int) left;
        int j = (int) mid + 1;
        int k = 0;

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];

        System.arraycopy(temp, 0, arr, (int) left, temp.length);
    }

    @Override
    public String algoToString() {
        return """
                public void mergeSort(long[] arr, long left, long right) {
                    if (left >= right) return;

                    long mid = left + (right - left) / 2;

                    mergeSort(arr, left, mid);
                    mergeSort(arr, mid + 1, right);

                    merge(arr, left, mid, right);
                }
                 private void merge(long[] arr, long left, long mid, long right) {
                    long[] temp = new long[Math.toIntExact(right - left + 1)];
            
                    int i = (int) left;
                    int j = (int) mid + 1;
                    int k = 0;
            
                    while (i <= mid && j <= right) {
                        if (arr[i] <= arr[j]) {
                            temp[k++] = arr[i++];
                        } else {
                            temp[k++] = arr[j++];
                        }
                    }
            
                    while (i <= mid) temp[k++] = arr[i++];
                    while (j <= right) temp[k++] = arr[j++];
            
                    System.arraycopy(temp, 0, arr, (int) left, temp.length);
                }
                """;
    }
}