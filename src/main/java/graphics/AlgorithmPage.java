package graphics;

import graphics.algorithms.SortAlgorithm;
import graphics.components.Board;
import graphics.components.Column;
import graphics.utilities.Dimensione;
import graphics.utilities.GestoreGrafico;
import graphics.utilities.GestorePagine;
import graphics.utilities.SoundManager;
import lombok.Getter;
import utilities.ArraysFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AlgorithmPage extends JPanel {
    private final GestorePagine gestorePagine;
    @Getter
    private final Board board;
    private final SortAlgorithm algorithm;
    private long[] arr;
    private final int min, max;

    public static final Dimensione dimensioneBoard = new Dimensione(5,10,90, 60);
    private static final Dimensione dimensioneText = new Dimensione(0,1,100, 5);
    private static final Dimensione dimensioneComandi = new Dimensione(0,1,30, 10);
    private static final Dimensione dimensioneDettagli = new Dimensione(0,70,50, 20);
    private static final Dimensione dimensioneAlgoritmoString = new Dimensione(50,70,45, 35);

    private String dettagli = "";

    public AlgorithmPage(GestorePagine gestorePagine, SortAlgorithm algorithm, long[] arr, int min, int max) {
        this.gestorePagine = gestorePagine;
        this.algorithm = algorithm;
        this.arr = arr;
        this.min = min;
        this.max = max;
        board = new Board(dimensioneBoard, arr);

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


        getActionMap().put("spacePressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dettagli = algorithm.sort(arr, AlgorithmPage.this);
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
                repaint();
            }
        });

        getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < 10; i++){
                    ArraysFactory.shuffle(arr);
                    board.deselect();

                    SoundManager.shuffle();
                    aggiorna(dimensioneBoard);
                }
                algorithm.reset();
            }
        });
    }

    public void aggiorna(Dimensione d){
        paintImmediately(d.ingrandisci(0.5,0.5).toRectangle(this));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        GestoreGrafico gestoreGrafico = new GestoreGrafico(this, (Graphics2D) g);
        gestoreGrafico.font(3);

        if(algorithm.isFinished()){
            board.select();
            algorithm.setFinished(false);
            SoundManager.fine();
        }

        gestoreGrafico.testo(dimensioneText, algorithm.getName(), Color.WHITE);
        gestoreGrafico.font(1);
        gestoreGrafico.disegnaTestoWrap(dettagli, dimensioneDettagli, Color.WHITE);
        gestoreGrafico.disegnaTestoWrap(algorithm.algoToString(), dimensioneAlgoritmoString, Color.WHITE);
        gestoreGrafico.disegnaTestoWrap(
                "R : Mischia\n" +
                "F : Ricrea array\n" +
                "Spazio : avvio\n" +
                "UP : aumento velocità\n" +
                "DOWN : diminuzione velocità\n"+
                "TICK : " + algorithm.getDelayMs(),
                dimensioneComandi, Color.WHITE
        );

        gestoreGrafico.draw(board);
    }
}
