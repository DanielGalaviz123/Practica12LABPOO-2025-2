package battleship.gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.net.URL;

public class ReproductorSonido {

    private static Clip musicaFondo;

    // Inicia la musica de fondo en loop
    public static void iniciarMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isActive()) {
            return; // ya estaba sonando
        }

        AudioInputStream audio = null;
        try {
            URL url = ReproductorSonido.class.getResource(
                    "/battleship/sonidos/SonidoDeFondo.wav");
            if (url == null) {
                System.err.println("No se encontro el sonido de fondo.");
                return;
            }

            audio = AudioSystem.getAudioInputStream(url);
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audio);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.err.println("Error al iniciar musica de fondo: " + e.getMessage());
        } finally {
            if (audio != null) {
                try {
                    audio.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // Detiene la musica de fondo
    public static void detenerMusicaFondo() {
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.close();
            musicaFondo = null;
        }
    }
}
