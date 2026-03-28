package graphics.utilities;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * <h3>Utility class for managing sound effects in the application.</h3>
 */
public final class SoundManager {
    private SoundManager(){}

    public static void play(String nomeSuono){
        try {
            InputStream is = SoundManager.class.getResourceAsStream("/" + nomeSuono);
            if (is == null) {
                throw new IllegalArgumentException("Suono non trovato nelle resources: " + nomeSuono);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }catch (Exception e){
            IO.println("ERRORE PLAY SUONO: " + e.getMessage());
        }
    }

    public static void scambio(int num){
        String nomeSuono = "scambio" + num + ".wav";
        play(nomeSuono);
    }

    public static void fine(){
        play("complete.wav");
    }

    public static void shuffle(){
        play("shuffle.wav");
    }

}
