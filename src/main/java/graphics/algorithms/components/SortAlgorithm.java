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

    protected final Consumer<UpdateInfo> getVisualUpdate(AlgorithmPage page){
        return info -> {
            // Aggiorna le vecchie posizioni se presenti
            if(info.oldDims != null) {
                for(Dimensione d : info.oldDims) {
                    page.aggiorna(d.ingrandisci(1, 1));
                }
            }

            if(info.cols.length == 0 || info.cols.length > 2 && !info.aggiornaCode)
                page.aggiorna(AlgorithmPage.DIMENSIONE_BOARD);
            else for(Column c : info.cols)
                page.aggiorna(c.getDimensione().ingrandisci(1,1));
            if (info.aggiornaCode) {
                page.aggiorna(AlgorithmPage.DIMENSIONE_ALGORITMO_STRING);
            }
        };
    }

    protected record UpdateInfo(boolean aggiornaCode, Dimensione[] oldDims, Column... cols){
        public UpdateInfo(){
            this(true, null);
        }
        public UpdateInfo(Column... cols){
            this(false, null, cols);
        }
        public UpdateInfo(Dimensione oldDim1, Dimensione oldDim2, Column... cols){
            this(false, new Dimensione[]{oldDim1, oldDim2}, cols);
        }
    }

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
        if(delayMs < 5) delayMs = 0;
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

            if (!internSort(arr, p25, p50, p85, board, getVisualUpdate(board))){
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

    protected abstract boolean internSort(long[] arr, long p25, long p50, long p85, AlgorithmPage board, Consumer<UpdateInfo> update);

    protected final void scambioCompleto(Column c1, Column c2, Consumer<UpdateInfo> update, int suono){
        // Salva le vecchie dimensioni prima dello scambio
        Dimensione oldDim1 = c1.getDimensione();
        Dimensione oldDim2 = c2.getDimensione();
        
        select(update, c1, c2);
        scambio(c1, c2, suono);
        
        // Aggiorna sia le vecchie che le nuove posizioni
        update.accept(new UpdateInfo(oldDim1, oldDim2, c1, c2));
        sleep();
        
        for(Column col : new Column[]{c1, c2}) {
            col.deselect();
        }
        update.accept(new UpdateInfo(oldDim1, oldDim2, c1, c2));
        sleep();
    }

    protected final void scambio(Column c1, Column c2, int suono){
        Dimensione tempD = c1.getDimensione();
        c1.setDimensione(c2.getDimensione());
        c2.setDimensione(tempD);
        swaps++;
        AlgoSoundManager.scambio(suono);
    }

    protected final void selectAndDeselectLines(Consumer<UpdateInfo> update, VisualAlgorithmCode code, int... lines){
        for(int line : lines){
            selectCodeLines(update, code, line);
            sleep();
            deselectCodeLines(update, code, line);
        }
    }

    protected final void selectCodeLines(Consumer<UpdateInfo> update, VisualAlgorithmCode code, int... lines){
        code.select(lines);
        update.accept(new UpdateInfo());
    }

    protected final void deselectCodeLines(Consumer<UpdateInfo> update, VisualAlgorithmCode code, int... lines){
        code.deselect(lines);
        update.accept(new UpdateInfo());
    }

    protected final void select(Consumer<UpdateInfo> update, Column... cols){
        for(Column col : cols) {
            col.select();
        }
        update.accept(new UpdateInfo(cols));
        sleep();
    }

    protected final void specialSelect(Consumer<UpdateInfo> update, Column... cols){
        for(Column col : cols) {
            col.specialSelect();
        }
        update.accept(new UpdateInfo(cols));
        sleep();
    }

    protected final void deselect(Consumer<UpdateInfo> update, Column... cols){
        for(Column col : cols) {
            col.deselect();
        }
        update.accept(new UpdateInfo(cols));
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
