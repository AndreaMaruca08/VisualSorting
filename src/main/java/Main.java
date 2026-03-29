import graphics.AlgorithmPage;
import graphics.utilities.GestorePagine;
import utilities.ArraysFactory;

import javax.swing.*;
import java.awt.*;

import static graphics.AlgorithmPage.ALGORITHMS;

public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

public static final AlgorithmPage[] pages = new AlgorithmPage[ALGORITHMS.length];

void main() {
    JFrame finestra = new JFrame("Algoritmi di sorting");
    finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    finestra.setSize(screenSize);

    GestorePagine gestorePagine = new GestorePagine();
    finestra.setContentPane(GestorePagine.container);

    final int min = 0;
    final int max = 70;

    long[] arr = ArraysFactory.createArrayRandom(50, min, max);

    for(int i = 1; i <= pages.length; i++){
        pages[i-1] = new AlgorithmPage(gestorePagine, ALGORITHMS[i-1], arr, min, max);
        gestorePagine.addPagina("algo"+i, pages[i-1]);
    }

    gestorePagine.cambiaPagina("algo1");

    finestra.setVisible(true);
}