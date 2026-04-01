package graphics.algorithms;

import graphics.AlgorithmPage;

import java.util.function.Consumer;

import static utilities.ArraysFactory.*;

public class BogoVisualSort extends VisualSortAlgorithm {

    public BogoVisualSort(int delayMs) {
        super(
                "Bogo Sort",
                "Mischia l'array fino a che non è in ordine",
                "0(1) - 0(∞)",
                delayMs
        );
    }

    private int shuffleIndex = 0;
    private boolean shuffling = false;

    @Override
    public void internalReset() {
        shuffleIndex = 0;
        shuffling = false;
    }

    @Override
    protected boolean internSort(long[] arr, long p25, long p50, long p85, AlgorithmPage board, Consumer<UpdateInfo> update) {
        var code = board.getCode();

        if (!shuffling) {
            selectAndDeselectLines(update, code, 0);
            if (isSorted(arr)) {
                return false;
            }
            selectAndDeselectLines(update, code, 1);
            shuffling = true;
            shuffleIndex = 0;
            return true;
        }

        if (shuffleIndex < arr.length) {
            int randomIndex = (int) (Math.random() * arr.length);
            long temp = arr[shuffleIndex];
            arr[shuffleIndex] = arr[randomIndex];
            arr[randomIndex] = temp;

            shuffleIndex++;
            return true;
        }

        shuffling = false;
        return true;
    }

    @Override
    public void actualSort(long[] arr) {
        while(!isSorted(arr)) {
            shuffle(arr);
        }
    }

    @Override
    public String algoToString() {
        return """
                while(!isSorted(arr)) {
                    shuffle(arr);
                }
                """;
    }
}
