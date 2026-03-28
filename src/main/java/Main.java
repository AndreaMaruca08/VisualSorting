import graphics.AlgorithmPage;
import graphics.algorithms.BubbleSort;
import graphics.algorithms.SortAlgorithm;
import graphics.utilities.GestorePagine;
import utilities.ArraysFactory;

import javax.swing.*;
import java.awt.*;

public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

void main() {
    JFrame finestra = new JFrame("Algoritmi di sorting");
    finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    finestra.setSize(screenSize);

    GestorePagine gestorePagine = new GestorePagine();
    finestra.setContentPane(GestorePagine.container);

    long[] arr = ArraysFactory.createArrayRandom(10, 0, 70);

    SortAlgorithm algorithm = new BubbleSort(20);

    AlgorithmPage algPage = new AlgorithmPage(gestorePagine, algorithm, arr, 0, 70);

    gestorePagine.addPagina("algo", algPage);
    gestorePagine.cambiaPagina("algo");

    finestra.setVisible(true);
}