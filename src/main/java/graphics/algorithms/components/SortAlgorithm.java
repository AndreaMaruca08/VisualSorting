package graphics.algorithms.components;

import graphics.AlgorithmPage;
import graphics.components.AlgoSoundManager;
import graphics.components.Column;
import core.utilities.Dimensione;
import lombok.Getter;
import lombok.Setter;
import utilities.ArraysFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * <h3>Abstract base class for sorting algorithms.</h3>
 * Provides common functionality and properties for sorting algorithms.<br>
 * Provides methods for <b> sorting and measuring</b> the performance of the algorithm.
 *
 * @author Andrea Maruca
 */
@Getter
@Setter
public abstract class SortAlgorithm {
    protected final String name;
    protected final String description;
    protected final String complexity;
    protected boolean finished = false;
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected int delayMs;
    protected long swaps;
    protected long compares;
    private final Timer timer;

    public SortAlgorithm(String name, String description, String complexity, int delayMs) {
        this.name = name;
        this.description = description;
        this.complexity = complexity;
        this.delayMs = delayMs;
        timer = new Timer(delayMs, null);
        swaps = 0;
        compares = 0;
    }

    public void pause(){
        running.set(false);
        timer.stop();
    }

    public void addDelay(){
        delayMs += 5;
    }

    public void removeDelay(){
        delayMs -= 5;
        if(delayMs < 5) delayMs = 1;
    }

    protected void sleep(){
        try {
            Thread.sleep(delayMs);
        }catch (Exception e){
            IO.println(e.getMessage());
        }
    }

    public String sort(long[] arr, AlgorithmPage board) {
        if(ArraysFactory.isSorted(arr))
            return "Array già ordinato";

        String res = measureSort(arr.clone());

        reset();

        long max = ArraysFactory.findHighest(arr);
        long p25 = max / 4;
        long p50 = max / 2;
        long p85 = (long) (max * 0.85);

        running = new AtomicBoolean(true);

        for (ActionListener listener : timer.getActionListeners()) {
            timer.removeActionListener(listener);
        }

        timer.addActionListener(_ -> {
            finished = false;
            if (!running.get()) {
                timer.stop();
                finished = true;
                board.aggiorna(AlgorithmPage.DIMENSIONE_BOARD);
                return;
            }

            if (!internSort(arr, p25, p50, p85, board, (aggiornaCode) -> {
                board.aggiorna(AlgorithmPage.DIMENSIONE_BOARD);
                if (aggiornaCode) {
                    board.aggiorna(AlgorithmPage.DIMENSIONE_ALGORITMO_STRING);
                }
            })) {
                running.set(false);
                timer.stop();
                finished = true;
                board.aggiorna(AlgorithmPage.DIMENSIONE_BOARD);
            }
        });

        timer.start();

        return res;
    }

    public final void reset(){
        internalReset();
        swaps = 0;
        compares = 0;
    }

    public abstract void internalReset();

    protected abstract boolean internSort(long[] arr, long p25, long p50, long p85, AlgorithmPage board, Consumer<Boolean> update);

    protected final void scambioCompleto(Column c1, Column c2, Consumer<Boolean> update, int suono){
        select(update, c1, c2);
        scambio(c1, c2, suono);
        deselect(update, c1, c2);
    }

    protected final void scambio(Column c1, Column c2, int suono){
        Dimensione tempD = c1.getDimensione();
        c1.setDimensione(c2.getDimensione());
        c2.setDimensione(tempD);
        swaps++;
        AlgoSoundManager.scambio(suono);
    }

    protected final void selectAndDeselectLines(Consumer<Boolean> update, VisualAlgorithmCode code, int... lines){
        for(int line : lines){
            selectCodeLines(update, code, line);
            sleep();
            deselectCodeLines(update, code, line);
        }
    }

    protected final void selectCodeLines(Consumer<Boolean> update, VisualAlgorithmCode code, int... lines){
        code.select(lines);
        update.accept(true);
    }

    protected final void deselectCodeLines(Consumer<Boolean> update, VisualAlgorithmCode code, int... lines){
        code.deselect(lines);
        update.accept(true);
    }

    protected final void select(Consumer<Boolean> update, Column... cols){
        for(Column col : cols) {
            col.select();
        }
        update.accept(false);
        sleep();
    }

    protected final void specialSelect(Consumer<Boolean> update, Column... cols){
        for(Column col : cols) {
            col.specialSelect();
        }
        update.accept(false);
        sleep();
    }

    protected final void deselect(Consumer<Boolean> update, Column... cols){
        for(Column col : cols) {
            col.deselect();
        }
        update.accept(false);
        sleep();
    }

    protected final int getSuono (long current, long p25, long p50, long p75){
        return current < p25 ? 1 : current < p50 ? 2 : current < p75 ? 3 : 4;
    }

    public abstract void actualSort(long[] arr);

    public abstract String algoToString();

    public VisualAlgorithmCode algoToVisual(Dimensione dim){
        String code = algoToString();
        String[] codeLines = code.split("\n");
        CodeLine[] lines = new CodeLine[codeLines.length];

        for(int i = 0; i < codeLines.length; i++){
            lines[i] = new CodeLine(codeLines[i]);
        }
        return new VisualAlgorithmCode(dim, lines);
    }

    private String measureSort(long[] arr){
        double start = System.nanoTime();

        actualSort(arr);

        double end = System.nanoTime() - start;

        if(!ArraysFactory.isSorted(arr))
            throw new ArrayNotSortedException(name, "Array non ordinato");

        return """
                Algorithm: %s
                Complexity: %s
                %s
                ------
                Time nano: %f
                Time ms: %f
                Time s: %f""".formatted(name, complexity, description, end, end / 1_000_000, end / 1_000_000_000);
    }
}