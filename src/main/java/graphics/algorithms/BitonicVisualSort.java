package graphics.algorithms;

import graphics.AlgorithmPage;
import graphics.components.Column;

import java.util.function.Consumer;

public class BitonicVisualSort extends VisualSortAlgorithm{

    private int currentK = 2;
    private int currentJ = 1;
    private int currentI = 0;

    public BitonicVisualSort(int delayMs) {
        super(
                "Bitonic Sort",
                """
                        Algoritmo di ordinamento progettato da Ken Batcher, basato su reti di confronto (sorting networks).
                         Ordina costruendo sequenze bitoniche (prima crescenti poi decrescenti, o viceversa) e poi le fonde.
                        È particolarmente rilevante in contesti paralleli (GPU, hardware), perché la sequenza di operazioni è fissa e indipendente dai dati.""",
                "O(n log² n)",
                delayMs
        );
    }

    @Override
    public void internalReset() {
        currentK = 2;
        currentJ = 1;
        currentI = 0;
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p85, AlgorithmPage board, Consumer<UpdateInfo> update) {
        if (currentK > arr.length) {
            return false;
        }
        if (currentJ == 0) {
            currentK <<= 1;
            currentJ = currentK >> 1;
            currentI = 0;
            return currentK <= arr.length;
        }
        if (currentI >= arr.length) {
            currentJ >>= 1;
            currentI = 0;
            return true;
        }
        int ij = currentI ^ currentJ;
        if (ij > currentI && ij < arr.length) {
            var cols = board.getBoard().columns;
            Column c1 = cols.get(currentI);
            Column c2 = cols.get(ij);
            select(update, c1, c2);
            compares++;
            boolean doSwap = false;
            if ((currentI & currentK) == 0) {
                if (arr[currentI] > arr[ij]) doSwap = true;
            } else {
                if (arr[currentI] < arr[ij]) doSwap = true;
            }
            if (doSwap) {
                scambioCompleto(c1, c2, update, getSuono(arr[currentI], p25, p50, p85));
                long temp = arr[currentI];
                arr[currentI] = arr[ij];
                arr[ij] = temp;
                swaps++;
            } else {
                deselect(update, c1, c2);
            }
        }
        currentI++;
        return true;
    }

    @Override
    public void actualSort(long[] arr) {
        int n = arr.length;
        int p = 1;
        while (p < n) p <<= 1;
        long[] padded = new long[p];
        System.arraycopy(arr, 0, padded, 0, n);
        for (int i = n; i < p; i++) padded[i] = Long.MAX_VALUE;
        bitonicSort(padded, 0, p, true);
        System.arraycopy(padded, 0, arr, 0, n);
    }

    public void bitonicSort(long[] arr, int low, int count, boolean ascending) {
        if (count > 1) {
            int k = count / 2;

            bitonicSort(arr, low, k, true);
            bitonicSort(arr, low + k, k, false);
            bitonicMerge(arr, low, count, ascending);
        }
    }

    private void bitonicMerge(long[] arr, int low, int count, boolean ascending) {
        if (count > 1) {
            int k = count / 2;

            for (int i = low; i < low + k; i++) {
                compares++;
                if ((arr[i] > arr[i + k]) == ascending) {
                    swap(arr, i, i + k);
                }
            }

            bitonicMerge(arr, low, k, ascending);
            bitonicMerge(arr, low + k, k, ascending);
        }
    }

    private void swap(long[] arr, int i, int j) {
        long temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        swaps++;
    }

    @Override
    public String algoToString() {
        return """
                public void bitonicSort(long[] arr, int low, int count, boolean ascending) {
                    if (count > 1) {
                        int k = count / 2;
            
                        bitonicSort(arr, low, k, true);
                        bitonicSort(arr, low + k, k, false);
            
                        // merge
                        bitonicMerge(arr, low, count, ascending);
                    }
                }
            
                private void bitonicMerge(long[] arr, int low, int count, boolean ascending) {
                    if (count > 1) {
                        int k = count / 2;
            
                        for (int i = low; i < low + k; i++) {
                            compares++;
                            if ((arr[i] > arr[i + k]) == ascending) {
                                swap(arr, i, i + k);
                            }
                        }
            
                        bitonicMerge(arr, low, k, ascending);
                        bitonicMerge(arr, low + k, k, ascending);
                    }
                }
            
                private void swap(long[] arr, int i, int j) {
                    long temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                    swaps++;
                }
                """;
    }
}
