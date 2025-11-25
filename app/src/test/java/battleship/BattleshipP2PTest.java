package battleship;

import battleship.logica.BattleshipP2P;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BattleshipP2PTest {

    @Test
    void constructor_noLanzaExcepcion() {
     assertDoesNotThrow(() -> new BattleshipP2P());
    }
}
