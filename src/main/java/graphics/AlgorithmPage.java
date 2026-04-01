package graphics;

import graphics.algorithms.*;
import graphics.algorithms.components.ArrayNotSortedException;
import graphics.algorithms.components.SortAlgorithm;
import graphics.algorithms.components.VisualAlgorithmCode;
import graphics.components.AlgoSoundManager;
import graphics.components.Board;
import core.Pagina;
import core.utilities.Dimensione;
import core.utilities.Grafica;
import core.utilities.GestorePagine;
import lombok.Getter;
import utilities.ArraysFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Pagina che mostra l'algoritmo in esecuzione
 * @author Andrea Maruca
 */
public class AlgorithmPage extends Pagina {
    @Getter
    private final Board board;
    private final SortAlgorithm algorithm;
    @Getter
    private final VisualAlgorithmCode code;
    private long[] arr;
    private final int min, max;

    public  static final Dimensione DIMENSIONE_BOARD = new Dimensione(5,3,90, 60);
    private static final Dimensione DIMENSIONE_TEXT = new Dimensione(0,1,100, 5);
    private static final Dimensione DIMENSIONE_DIMENSIONE_ARRAY = new Dimensione(0,10,100, 5);
    private static final Dimensione DIMENSIONE_COMANDI = new Dimensione(0,1,50, 10);
    private static final Dimensione DIMENSIONE_AUTORE = new Dimensione(80,1,20, 5);
    private static final Dimensione DIMENSIONE_SWAPS_COMPARES = new Dimensione(80,3.5,20, 5);
    private static final Dimensione DIMENSIONE_DETTAGLI = new Dimensione(0,57,50, 40);
    public  static final Dimensione DIMENSIONE_ALGORITMO_STRING = new Dimensione(60,60,45, 39);

    private String dettagli = "";

    public static final SortAlgorithm[] ALGORITHMS = {
            new InsertionSort(20),
            new BubbleSort(20),
            new CockTailShakerSort(20),
            new MergeSort(20),
            new QuickSort(20),
            new BogoSort(5)
    };

    public AlgorithmPage(GestorePagine gestorePagine, SortAlgorithm algorithm, long[] arr, int min, int max) {
        super(gestorePagine);
        this.algorithm = algorithm;
        this.arr = arr;
        this.min = min;
        this.max = max;
        board = new Board(DIMENSIONE_BOARD, arr);
        code = algorithm.algoToVisual(DIMENSIONE_ALGORITMO_STRING);

        setKeys();

        setBackground(Color.BLACK);
    }

    private void setKeys(){
        creaComando("avvio", () -> {
            try {
                dettagli = algorithm.sort(arr, AlgorithmPage.this);
                aggiorna(DIMENSIONE_DETTAGLI);
            }catch (ArrayNotSortedException ex){
                JOptionPane.showMessageDialog(AlgorithmPage.this, ex.getMessage(), ex.algoName, JOptionPane.ERROR_MESSAGE);
            }
        }, "SPACE", "ENTER");

        creaComando("reset", () -> {
            board.deselect();
            for(int i = 0; i < 10; i++){
                ArraysFactory.shuffle(arr);
                AlgoSoundManager.shuffle();
                aggiorna(DIMENSIONE_BOARD);
            }
            algorithm.reset();
        }, "R");

        creaComando("delay+", algorithm::addDelay, "UP");

        creaComando("delay-", algorithm::removeDelay, "DOWN");

        creaComando("ricrea", () -> {
            arr = ArraysFactory.createArrayRandom(arr.length, min, max);
            board.setArr(arr);
            board.deselect();
            algorithm.reset();
            aggiorna(DIMENSIONE_BOARD);
        }, "F");

        creaComando("aumenta", () -> {
            if(algorithm.getRunning().get())
                return;
            arr = ArraysFactory.createArrayRandom(arr.length + 1, min, max);
            board.setArr(arr);
            board.deselect();
            algorithm.reset();
        }, "A");

        creaComando("diminuisci", () -> {
            if(algorithm.getRunning().get() || arr.length <= 2)
                return;
            arr = ArraysFactory.createArrayRandom(arr.length - 1, min, max);
            board.setArr(arr);
            board.deselect();
            algorithm.reset();
        }, "D");

        creaComando("pausa", algorithm::pause, "M");

        creaComando("aumentoVolume", AlgoSoundManager::aumentaVolume, "O");
        
        creaComando("downVolume", AlgoSoundManager::diminuisciVolume, "P");

        char[] options = {'1','2','3','4','5','6','7','8','9'};
        for(int i = 0; i < ALGORITHMS.length; i++){
            final int index = i;
            creaComando("algo"+options[i], () -> gestorePagine.cambiaPagina("algo" + options[index]), options[i]+"");
        }
    }

    @Override
    protected void draw(Grafica g) {
        g.font(3);

        if(algorithm.isFinished()){
            board.select();
            algorithm.setFinished(false);
            AlgoSoundManager.fine();
        }

        g.testo(DIMENSIONE_TEXT, algorithm.getName(), Color.WHITE);
        g.font(1);
        g.testo(DIMENSIONE_DIMENSIONE_ARRAY, "Dimensione: " + arr.length, Color.WHITE);
        g.testo(DIMENSIONE_AUTORE, "Autore: Andrea Maruca", Color.WHITE);
        g.disegnaTestoWrap(dettagli, DIMENSIONE_DETTAGLI, Color.WHITE);
        g.disegnaTestoWrap("""
                Scambi: %d
                Confronti: %d
                """.formatted(algorithm.getSwaps(), algorithm.getCompares()), DIMENSIONE_SWAPS_COMPARES, Color.WHITE);
        g.font(0.85);
        g.disegnaTestoWrap(
                """
                O : aumenta volume       P : diminuisci volume     VOLUME : %.2f
                A : aumenta array        D : diminuisce array
                F : Ricrea array         R : mischia
                Spazio : avvio           M : pausa
                UP : - velocità          DOWN : + velocità
                TICK : %d (durata tick in ms)
                1, 2, 3, 4, 5, 6, 7, 8, 9 : cambia algoritmo
                """.formatted(AlgoSoundManager.getVolume(), algorithm.getDelayMs()),
                DIMENSIONE_COMANDI, Color.WHITE
        );
        g.draw(board);
        g.draw(code);

    }
}
