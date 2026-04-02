package core.utilities;

import core.BaseComponent;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>Classe helper per disegnare le figure</h1>
 * <p>Contiene metodi per aiutare nel disegno senza componenti</p>
 *
 * @author Andrea Maruca
 */
@Log
public record Grafica(
        JPanel component,
        Graphics2D g2
) {
    public void draw(BaseComponent component) {
        component.draw(this);
    }

    /**
     * Cambia il colore del disegno
     * @param newColor nuovo colore
     */
    public void colore(Color newColor){
        g2().setColor(newColor);
    }

    /**
     * Cambia lo spessore del disegno
     * @param spessore nuovo spessore
     */
    public void spessoreDisegno(int spessore){
        g2().setStroke(new BasicStroke(spessore));
    }

    /**
     * Cambia il font a quello custom
     * @param size grandezza del font
     */
    public void font(double size){
        g2().setFont(FontLoader.getCustomFont(getX(size)));
    }




    /**
     * Disegna il testo passato nella posizione passata e in automatico va a capo<br>
     * (Più costosa di {@link #testo(Dimensione, String)})
     * @param testo testo da disegnare
     * @param dimensioneLabel posizione e dimensione del testo
     */
    public void disegnaTestoWrap(String testo, Dimensione dimensioneLabel) {
        Graphics2D g = g2();
        int x = getX(dimensioneLabel.x() + 2);
        int y = getY(dimensioneLabel.y() + 2);
        int width = getX(dimensioneLabel.width() - 4);

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();

        int currentY = y + fm.getAscent();
        StringBuilder line = new StringBuilder();

    for (int i = 0; i < testo.length(); i++) {
        char ch = testo.charAt(i);

        if (ch == '\n') {
            g.drawString(line.toString(), x, currentY);
            line.setLength(0);
            currentY += lineHeight;
            continue;
        }

        line.append(ch);

        if (fm.stringWidth(line.toString()) > width) {
            line.deleteCharAt(line.length() - 1);
            g.drawString(line.toString(), x, currentY);
            currentY += lineHeight;

            line.setLength(0);

            if (ch != ' ') {
                line.append(ch);
            }
        }
    }

    if (!line.isEmpty()) {
        g.drawString(line.toString(), x, currentY);
    }
}

    public void disegnaTestoWrap(String testo, Dimensione dimensioneLabel, Color colore) {
        g2().setColor(colore);
        disegnaTestoWrap(testo, dimensioneLabel);
    }
    

    public void testo(Dimensione dim, String testo) {
        g2().drawString(testo, getX(dim.x() + 1), getY(dim.y() + 1));
    }

    /**
     * Disegna il testo nella posizione specificata con il colore specificato<br>
     * @param dim posizione e dimensione di dove disegnare il testo
     * @param testo testo da disegnare
     * @param colore colore del testo
     */
    public void testo(Dimensione dim, String testo, Color colore) {
        Color oldColor = g2().getColor();
        g2().setColor(colore);

        FontMetrics fm = g2().getFontMetrics();
        int larghezzaTesto = fm.stringWidth(testo);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();

        int x = getX(dim.x()) + (getX(dim.width()) - larghezzaTesto) / 2;
        int yBase = getY(dim.y());
        int hPix = getY(dim.height());
        int y = yBase + (hPix + ascent - descent) / 2;

        g2().drawString(testo, x, y);
        g2().setColor(oldColor);
    }

    /**
     * Disegna un cerchio<br>
     * @param dim dimensione del cerchio
     * @param colore colore del cerchio
     */
    public void cerchio(Dimensione dim, Color colore) {
        g2().setColor(colore);
        oval(dim, true);
    }

    /**
     * Disegna il bordo di un cerchio<br>
     * @param dim dimensione del cerchio
     * @param colore colore del bordo
     */
    public void bordoCerchio(Dimensione dim, Color colore) {
        g2().setColor(colore);
        oval(dim, false);
    }

    /**
     * Disegna un rettangolo arrotondato<br>
     * @param dim dimensione del rettangolo
     * @param arc curvatura del rettangolo
     */
    public void rettangoloRound(Dimensione dim, int arc){
        roundRect(dim, arc, true);
    }

    /**
     * Disegna un rettangolo arrotondato con il colore specificato<br>
     * @param dim dimensione del rettangolo
     * @param arc curvatura del rettangolo
     * @param colore colore del rettangolo
     */
    public void rettangoloRound(Dimensione dim, int arc, Color colore){
        g2().setColor(colore);
        roundRect(dim, arc, true);
    }

    /**
     * Disegna il bordo di un rettangolo arrotondato<br>
     * @param dim dimensione del rettangolo
     * @param arc curvatura del rettangolo
     */
    public void bordoRettangoloRound(Dimensione dim, int arc){
        roundRect(dim, arc, false);
    }

    /**
     * Disegna il bordo di un rettangolo arrotondato con il colore specificato<br>
     * @param dim dimensione del rettangolo
     * @param arc curvatura del rettangolo
     * @param colore colore del bordo
     */
    public void bordoRettangoloRound(Dimensione dim, int arc, Color colore){
        g2().setColor(colore);
        roundRect(dim, arc, false);
    }

    /**
     * Disegna un rettangolo arrotondato<br>
     * @param dim dimensione del rettangolo
     * @param arc curvatura del rettangolo
     * @param riempi se true riempie il rettangolo, altrimenti disegna solo il bordo
     */
    private void roundRect(Dimensione dim, int arc, boolean riempi){
        int x = getX(dim.x());
        int y = getY(dim.y());
        int w = getX(dim.width());
        int h = getY(dim.height());
        if (riempi)
            g2().fillRoundRect(x, y, w, h, arc, arc);
        else
            g2().drawRoundRect(x, y, w, h, arc, arc);
    }

    /**
     * Disegna un rettangolo<br>
     * @param dim dimensione del rettangolo
     * @param colore colore del rettangolo
     */
    public void rettangolo(Dimensione dim, Color colore) {
        var old = g2().getColor();
        g2().setColor(colore);
        rect(dim, true);
        g2().setColor(old);
    }

    /**
     * Disegna il bordo di un rettangolo<br>
     * @param dim dimensione del rettangolo
     * @param colore colore del bordo
     */
    public void bordoRettangolo(Dimensione dim, Color colore) {
        g2().setColor(colore);
        rect(dim, false);
    }

    private void oval(Dimensione dim, boolean riempi) {
        int x = getX(dim.x());
        int y = getY(dim.y());
        int w = getX(dim.width());
        int h = getY(dim.height());
        if (riempi)
            g2().fillOval(x, y, w, h);
        else
            g2().drawOval(x, y, w, h);
    }


    private void rect(Dimensione dim, boolean riempi) {
        int x = getX(dim.x());
        int y = getY(dim.y());
        int w = getX(dim.width());
        int h = getY(dim.height());
        if (riempi)
            g2().fillRect(x, y, w, h);
        else
            g2().drawRect(x, y, w, h);
    }

    /**
     * Disegna una linea<br>
     * @param x di partenza
     * @param y di partenza
     * @param x2 di arrivo
     * @param y2 di arrivo
     */
    public void linea(double x, double y, double x2, double y2){
        g2().drawLine(getX(x), getY(y), getX(x2), getY(y2));
    }

    /**
     * Disegna una linea con il colore specificato<br>
     * @param x di partenza
     * @param y di partenza
     * @param x2 di arrivo
     * @param y2 di arrivo
     * @param colore colore della linea
     */
    public void linea(double x, double y, double x2, double y2, Color colore){
        g2().setColor(colore);
        linea(x, y, x2, y2);
    }

    public int getX(double x) {
        return (int) (component.getWidth() * (x / 100f));
    }

    public static int getX(double x, Component component) {
        return (int) (component.getWidth() * (x / 100f));
    }

    public int getY(double y) {
        return (int) (component.getHeight() * (y / 100f));
    }

    public static int getY(double y, Component component) {
        return (int) (component.getHeight() * (y / 100f));
    }
}