package graphics.algorithms;

import graphics.AlgorithmPage;
import lombok.Getter;
import lombok.Setter;
import utilities.ArraysFactory;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public abstract class SortAlgorithm {
    protected final String name;
    protected final String description;
    protected final String complexity;
    protected boolean finished = false;
    protected int delayMs;
    public SortAlgorithm(String name, String description, String complexity, int delayMs) {
        this.name = name;
        this.description = description;
        this.complexity = complexity;
        this.delayMs = delayMs;
    }

    public void addDelay(){
        delayMs += 5;
    }
    public void removeDelay(){
        if(delayMs < 5) delayMs = 1;
        delayMs -= 5;
    }

    protected void sleep(){
        try {
            Thread.sleep(delayMs * 2L);
        }catch (Exception e){
            IO.println(e.getMessage());
        }
    }

    public String sort(long[] arr, AlgorithmPage board) {
        if(ArraysFactory.isSorted(arr))
            return "Array già ordinato";

        String res = measureSort(arr.clone());

        long max = ArraysFactory.findHighest(arr);
        long p25 = max / 4;
        long p50 = max / 2;
        long p85 = (long) (max * 0.85);

        Timer timer = new Timer(delayMs, null);
        AtomicBoolean running = new AtomicBoolean(true);

        timer.addActionListener(e -> {
            finished = false;
            if (!running.get()) {
                timer.stop();
                finished = true;
                board.aggiorna(AlgorithmPage.dimensioneBoard);
                return;
            }

            if (!internSort(arr, p25, p50, p85, board, () -> board.aggiorna(AlgorithmPage.dimensioneBoard))) {
                running.set(false);
                timer.stop();
                finished = true;
                board.aggiorna(AlgorithmPage.dimensioneBoard);
            }
        });

        timer.start();

        return res;
    }

    public abstract void reset();
    protected abstract boolean internSort(long[] arr, long p25, long p50, long p75, AlgorithmPage board, Runnable update);

    protected int getSuono (long current, long p25, long p50, long p75){
        return current < p25 ? 1 : current < p50 ? 2 : current < p75 ? 3 : 4;
    }

    abstract void actualSort(long[] arr);

    public abstract String algoToString();

    private String measureSort(long[] arr){
        double start = System.nanoTime();

        actualSort(arr);

        double end = System.nanoTime() - start;

        return """
                Algorithm: %s
                Complexity: %s
                %s
                Dimensione array: %d
                ------
                Time nano: %f
                Time ms: %f
                Time s: %f""".formatted(name, complexity, description, arr.length, end, end / 1_000_000, end / 1_000_000_000);
    }
}