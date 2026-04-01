package graphics.algorithms;

import graphics.AlgorithmPage;
import graphics.algorithms.components.CodeLine;
import graphics.algorithms.components.SortAlgorithm;
import graphics.algorithms.components.VisualAlgorithmCode;
import graphics.components.AlgoSoundManager;
import graphics.components.Column;
import core.utilities.Dimensione;
import lombok.Getter;
import lombok.Setter;
import utilities.ArraysFactory;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * <h3>Abstract base class for visual sorting algorithms.</h3>
 * Provides common functionality and properties for sorting algorithms.<br>
 * Provides methods for <b> sorting and measuring</b> the performance of the algorithm.
 *
 * @author Andrea Maruca
 */
@Getter
@Setter
public abstract class VisualSortAlgorithm extends SortAlgorithm {
    protected boolean finished = false;
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected int delayMs;
    protected long swaps;
    protected long compares;
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> currentTask;

    protected final Consumer<UpdateInfo> getVisualUpdate(AlgorithmPage page){
        return info -> {
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

    public VisualSortAlgorithm(String name, String description, String complexity, int delayMs) {
        super(name, description, complexity);
        this.delayMs = delayMs;
        executor = Executors.newSingleThreadScheduledExecutor();
        swaps = 0;
        compares = 0;
    }

    public void pause(){
        running.set(false);
    }

    public void addDelay(){
        delayMs += 5;
        if(delayMs == 6) delayMs = 5;
    }

    public void removeDelay(){
        delayMs -= 5;
        if(delayMs < 5) delayMs = 1;
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

        Runnable sortTask = new Runnable() {
            @Override
            public void run() {
                finished = false;
                if (!running.get()) {
                    SwingUtilities.invokeLater(() -> {
                        finished = true;
                        board.aggiorna(AlgorithmPage.DIMENSIONE_BOARD);
                    });
                    return;
                }

                boolean continueSorting = internSort(arr, p25, p50, p85, board, info -> {
                    SwingUtilities.invokeLater(() -> getVisualUpdate(board).accept(info));
                });

                if (!continueSorting) {
                    running.set(false);
                    SwingUtilities.invokeLater(() -> {
                        finished = true;
                        board.aggiorna(AlgorithmPage.DIMENSIONE_BOARD);
                    });
                } else {
                    currentTask = executor.schedule(this, delayMs, TimeUnit.MILLISECONDS);
                }
            }
        };

        if (executor.isShutdown()) {
            executor = Executors.newSingleThreadScheduledExecutor();
        }

        currentTask = executor.schedule(sortTask, 0, TimeUnit.MILLISECONDS);

        return res;
    }

    public final void reset(){
        internalReset();
        swaps = 0;
        compares = 0;
    }

    public abstract void internalReset();

    protected abstract boolean internSort(long[] arr, long p25, long p50, long p85, AlgorithmPage board, Consumer<UpdateInfo> update);

    protected final void scambioCompleto(Column c1, Column c2, Consumer<UpdateInfo> update, int suono) {
        Dimensione oldDim1 = c1.getDimensione();
        Dimensione oldDim2 = c2.getDimensione();

        select(update, c1, c2);
        scambio(c1, c2, oldDim1, oldDim2, suono);

        update.accept(new UpdateInfo(oldDim1, oldDim2, c1, c2));

        for (Column col : new Column[]{c1, c2}) {
            col.deselect();
        }
        update.accept(new UpdateInfo(oldDim1, oldDim2, c1, c2));
    }

    protected final void scambio(Column c1, Column c2) {
        Dimensione oldDim1 = c1.getDimensione();
        Dimensione oldDim2 = c2.getDimensione();
        scambio(c1, c2, oldDim1, oldDim2, core.utilities.SoundManager.NULL);
    }

    protected final void scambio(Column c1, Column c2, Dimensione oldDim1, Dimensione oldDim2, int suono) {
        Dimensione tempD = c1.getDimensione();
        c1.setDimensione(c2.getDimensione());
        c2.setDimensione(tempD);
        swaps++;
        AlgoSoundManager.scambio(suono);
    }

    protected final void selectAndDeselectLines(Consumer<UpdateInfo> update, VisualAlgorithmCode code, int... lines){
        for(int line : lines){
            selectCodeLines(update, code, line);
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
    }

    protected final void specialSelect(Consumer<UpdateInfo> update, Column... cols){
        for(Column col : cols) {
            col.specialSelect();
        }
        update.accept(new UpdateInfo(cols));
    }

    protected final void deselect(Consumer<UpdateInfo> update, Column... cols){
        for(Column col : cols) {
            col.deselect();
        }
        update.accept(new UpdateInfo(cols));
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
}
