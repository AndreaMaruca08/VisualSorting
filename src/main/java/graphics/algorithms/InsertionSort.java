package graphics.algorithms;

import graphics.AlgorithmPage;
import graphics.components.Column;

import java.util.concurrent.atomic.AtomicInteger;

public class InsertionSort extends SortAlgorithm{

    public InsertionSort(int delayMs) {
        super(
                "Insertion Sort",
                "Algoritmo che costruisce l’array ordinato inserendo ogni " +
                        "elemento nella posizione corretta rispetto alla parte già ordinata.\n" +
                        "È analogo a come ordini le carte in mano: prendi una carta e la " +
                        "inserisci nel punto giusto.",
                "O(n²)",
                delayMs);
    }

    AtomicInteger i = new AtomicInteger(1);
    AtomicInteger ja = new AtomicInteger(1);

    @Override
    public void internalReset() {
        i.set(1);
        ja.set(1);
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p75, AlgorithmPage board, Runnable update) {
        int j = i.get();

        compares++;
        if(j >= arr.length) return false;
        compares++;
        if(i.get() >= arr.length) return false;

        var cols = board.getBoard().columns;


        while (j > 0 && arr[j-1] > arr[j] ) {
            Column c1 = cols.get(j);
            Column c2 = cols.get(j-1);

            scambioCompleto(c1, c2, update, getSuono(arr[j], p25, p50, p75));

            long temp = arr[j];
            arr[j] = arr[j-1];
            arr[j-1] = temp;
            j--;
            compares++;
        }

        ja.set(j);

        i.incrementAndGet();

        return true;
    }

    @Override
    public void actualSort(long[] arr) {
        for (int i = 1; i < arr.length; i++) {
             int j = i;
             while(j > 0 && arr[j-1] > arr[j]) {
                 long temp = arr[j];
                 arr[j] = arr[j-1];
                 arr[j-1] = temp;
                 j--;
             }
        }
    }

    @Override
    public String algoToString() {
        return """
                for (int i = 1; i < arr.length; i++) {
                     int j = i;
                     while(j > 0 && arr[j-1] > arr[j]) {
                         long temp = arr[j];
                         arr[j] = arr[j-1];
                         arr[j-1] = temp;
                         j--;
                     }
                }
                """;
    }
}
