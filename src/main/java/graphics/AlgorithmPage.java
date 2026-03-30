package graphics;

import graphics.algorithms.*;
import graphics.components.Board;
import graphics.utilities.Dimensione;
import graphics.utilities.GestoreGrafico;
import graphics.utilities.GestorePagine;
import graphics.utilities.SoundManager;
import lombok.Getter;
import utilities.ArraysFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Pagina che mostra l'algoritmo in esecuzione
 * @author Andrea Maruca
 */
public class AlgorithmPage extends JPanel {
    private final GestorePagine gestorePagine;
    @Getter
    private final Board board;
    private final SortAlgorithm algorithm;
    private long[] arr;
    private final int min, max;

    public static final Dimensione DIMENSIONE_BOARD = new Dimensione(5,10,90, 60);
    private static final Dimensione DIMENSIONE_TEXT = new Dimensione(0,1,100, 5);
    private static final Dimensione DIMENSIONE_DIMENSIONE_ARRAY = new Dimensione(0,10,100, 5);
    private static final Dimensione DIMENSIONE_COMANDI = new Dimensione(0,1,50, 10);
    private static final Dimensione DIMENSIONE_AUTORE = new Dimensione(80,1,20, 10);
    private static final Dimensione DIMENSIONE_SWAPS_COMPARES = new Dimensione(0,15,20, 10);
    private static final Dimensione DIMENSIONE_DETTAGLI = new Dimensione(0,70,50, 20);
    private static final Dimensione DIMENSIONE_ALGORITMO_STRING = new Dimensione(60,70,45, 35);

    private String dettagli = "";

    public static final SortAlgorithm[] ALGORITHMS = {
            new InsertionSort(20),
            new BubbleSort(20),
            new MergeSort(20)
    };

    public AlgorithmPage(GestorePagine gestorePagine, SortAlgorithm algorithm, long[] arr, int min, int max) {
        this.gestorePagine = gestorePagine;
        this.algorithm = algorithm;
        this.arr = arr;
        this.min = min;
        this.max = max;
        board = new Board(DIMENSIONE_BOARD, arr);

        setKeys();

        setBackground(Color.BLACK);
        setLayout(null);
    }

    private void setKeys(){
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "spacePressed");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "reset");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "delay+");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "delay-");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "ricrea");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "aumenta");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "diminuisci");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("M"), "pausa");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("O"), "aumentoVolume");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "downVolume");

        getActionMap().put("spacePressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dettagli = algorithm.sort(arr, AlgorithmPage.this);
                }catch (ArrayNotSortedException ex){
                    JOptionPane.showMessageDialog(AlgorithmPage.this, ex.getMessage(), ex.algoName, JOptionPane.ERROR_MESSAGE);
                }
                repaint();
            }
        });
        getActionMap().put("aumentoVolume", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.aumentaVolume();
                repaint();
            }
        });
        getActionMap().put("downVolume", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.diminuisciVolume();
                repaint();
            }
        });
        getActionMap().put("pausa", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithm.pause();
                repaint();
            }
        });
        getActionMap().put("aumenta", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(algorithm.getRunning().get())
                    return;
                arr = ArraysFactory.createArrayRandom(arr.length + 1, min, max);
                board.setArr(arr);
                board.deselect();
                algorithm.reset();
                repaint();
            }
        });
        getActionMap().put("diminuisci", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(algorithm.getRunning().get() || arr.length <= 2)
                    return;
                arr = ArraysFactory.createArrayRandom(arr.length - 1, min, max);
                board.setArr(arr);
                board.deselect();
                algorithm.reset();
                repaint();
            }
        });
        getActionMap().put("delay+", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithm.addDelay();
                repaint();
            }
        });
        getActionMap().put("delay-", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithm.removeDelay();
                repaint();
            }
        });
        getActionMap().put("ricrea", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                arr = ArraysFactory.createArrayRandom(arr.length, min, max);
                board.setArr(arr);
                board.deselect();
                algorithm.reset();
                aggiorna(DIMENSIONE_BOARD);
            }
        });

        getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.deselect();
                for(int i = 0; i < 10; i++){
                    ArraysFactory.shuffle(arr);
                    SoundManager.shuffle();
                    aggiorna(DIMENSIONE_BOARD);
                }
                algorithm.reset();
            }
        });

        char[] options = {'1','2','3','4','5','6','7','8','9'};
        for(int i = 0; i < ALGORITHMS.length; i++){
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(options[i]), options[i]+"");
            getActionMap().put(options[i]+"", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gestorePagine.cambiaPagina("algo"+e.getActionCommand());
                    repaint();
                }
            });
        }

    }

    public void aggiorna(Dimensione d){
        paintImmediately(d.ingrandisci(0.5,0.5).toRectangle(this));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        GestoreGrafico ge = new GestoreGrafico(this, (Graphics2D) g);
        ge.font(3);

        if(algorithm.isFinished()){
            board.select();
            algorithm.setFinished(false);
            SoundManager.fine();
        }

        ge.testo(DIMENSIONE_TEXT, algorithm.getName(), Color.WHITE);
        ge.font(1);
        ge.testo(DIMENSIONE_DIMENSIONE_ARRAY, "Dimensione: " + arr.length, Color.WHITE);
        ge.testo(DIMENSIONE_AUTORE, "Autore: Andrea Maruca", Color.WHITE);
        ge.disegnaTestoWrap(dettagli, DIMENSIONE_DETTAGLI, Color.WHITE);
        ge.disegnaTestoWrap("\nScambi: " + algorithm.getSwaps() + "\n\nConfronti: " + algorithm.getCompares(), DIMENSIONE_SWAPS_COMPARES, Color.WHITE);
        ge.font(0.85);
        ge.disegnaTestoWrap(
                        """
                        O : aumenta volume       P : diminuisci volume     VOLUME : %.2f
                        A : aumenta array        D : diminuisce array 
                        F : Ricrea array         R : mischia
                        Spazio : avvio           M : pausa
                        UP : - velocità          DOWN : + velocità
                        TICK : %d (durata tick in ms)
                        1, 2, 3, 4, 5, 6, 7, 8, 9 : cambia algoritmo
                        """.formatted(SoundManager.getVolume(), algorithm.getDelayMs()),
                DIMENSIONE_COMANDI, Color.WHITE
        );
        if(algorithm.algoToString().length() > 400)
            ge.font(0.5);
        ge.disegnaTestoWrap(algorithm.algoToString(), DIMENSIONE_ALGORITMO_STRING, Color.WHITE);

        ge.draw(board);
    }
}
