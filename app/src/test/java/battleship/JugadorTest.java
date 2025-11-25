package battleship;

import battleship.logica.Jugador;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JugadorTest {

    @Test
    void constructor_guardaNombre_yCreaJuego() {
        Jugador j = new Jugador("Daniel");

        assertEquals("Daniel", j.getNombre());
        assertNotNull(j.getJuego());
    }
}
