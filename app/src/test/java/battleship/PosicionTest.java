package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PosicionTest {

    @Test
    void equals_mismasCoordenadas() {
        Posicion p1 = new Posicion(2,3);
        Posicion p2 = new Posicion(2,3);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void equals_coordenadasDiferentes() {
        Posicion p1 = new Posicion(1,1);
        Posicion p2 = new Posicion(1,2);
        Posicion p3 = new Posicion(0,1);

        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);
    }
}
