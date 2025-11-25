package battleship;

import battleship.logica.Tablero;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TableroTest {

    @Test
    void constructor_y_getTamanio() {
        Tablero t = new Tablero(5);
        assertEquals(5, t.getTamanio());
    }

    @Test
    void llenar_colocaMismoCaracterEnTodasLasCeldas() {
        Tablero t = new Tablero(4);
        t.llenar('~');

        for (int i = 0; i < t.getTamanio(); i++) {
            for (int j = 0; j < t.getTamanio(); j++) {
                assertEquals('~', t.getCelda(i,j));
            }
        }
    }

    @Test
    void setCelda_y_getCelda_afectanSoloEsaPosicion() {
        Tablero t = new Tablero(3);
        t.llenar('?');

        t.setCelda(1,2,'X');

        assertEquals('X', t.getCelda(1,2));
        assertEquals('?', t.getCelda(0,0));
        assertEquals('?', t.getCelda(2,1));
    }
}
