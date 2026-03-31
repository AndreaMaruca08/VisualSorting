package core.utilities;

import lombok.Getter;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h3>Utility class for managing sound effects in the application.</h3>
 */
public class SoundManager {
    protected SoundManager(){}

    private static float min = 0f;
    private static float max = 1f;

    public static final int NULL = -1111;
    private static final int MAX_CLIPS = 15;

    @Getter
    private static float volume = 1f;

    private static final AtomicInteger NEXT_CLIP = new AtomicInteger(0);
    private static final boolean[] BUSY = new boolean[MAX_CLIPS];
    private static final Object LOCK = new Object();

    protected static void setLimits(float min, float max){
        SoundManager.min = min;
        SoundManager.max = max;
    }

    public static void aumentaVolume() {
        if(volume < max)
            volume += 0.1f;
        else
            volume = max;
    }

    public static void diminuisciVolume() {
        if(volume > min)
            volume -= 0.1f;
        else
            volume = min;
    }

    public static void play(String nomeSuono){
        try {
            InputStream is = SoundManager.class.getResourceAsStream("/" + nomeSuono);
            if (is == null) {
                throw new IllegalArgumentException("Suono non trovato nelle resources: " + nomeSuono);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            byte[] audioBytes = audioInputStream.readAllBytes();
            AudioFormat format = audioInputStream.getFormat();
            audioInputStream.close();

            int index = acquireSlot();
            if (index == -1) {
                return;
            }

            Clip clip = AudioSystem.getClip();
            clip.open(format, audioBytes, 0, audioBytes.length);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                gainControl.setValue(min + (max - min) * volume);
            }

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                    clip.close();
                    releaseSlot(index);
                }
            });

            clip.start();
        } catch (Exception e){
            IO.println("ERRORE PLAY SUONO: " + e.getMessage());
        }
    }

    private static int acquireSlot() {
        synchronized (LOCK) {
            for (int i = 0; i < MAX_CLIPS; i++) {
                int idx = (NEXT_CLIP.getAndIncrement() % MAX_CLIPS);
                if (!BUSY[idx]) {
                    BUSY[idx] = true;
                    return idx;
                }
            }
            return -1;
        }
    }

    private static void releaseSlot(int index) {
        synchronized (LOCK) {
            BUSY[index] = false;
        }
    }
}