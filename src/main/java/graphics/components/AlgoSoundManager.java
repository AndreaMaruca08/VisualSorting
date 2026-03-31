package graphics.components;

import core.utilities.SoundManager;

public final class AlgoSoundManager extends SoundManager {
    private AlgoSoundManager(){}

    static {setLimits(0.4f, 1f);}

    public static void aumentaVolume() {
        SoundManager.aumentaVolume();
    }

    public static void diminuisciVolume() {
        SoundManager.diminuisciVolume();
    }

    public static void scambio(int num){
        if(num == NULL)
            return;
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
