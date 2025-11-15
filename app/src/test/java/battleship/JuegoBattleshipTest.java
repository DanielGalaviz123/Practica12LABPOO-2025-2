package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JuegoBattleshipTest {

    @Test
    void constructor_estadoInicial() {
        JuegoBattleship juego = new JuegoBattleship();

        assertFalse(juego.todosBarcosHundidos());
        assertFalse(juego.yaDisparado(0,0));
        assertEquals("DESCONOCIDO", juego.obtenerTipoBarcoEn(0,0));
    }

    @Test
    void registrarImpacto_marcaComoDisparado() {
        JuegoBattleship juego = new JuegoBattleship();

        juego.registrarImpacto(3,4);
        assertTrue(juego.yaDisparado(3,4));
    }

    @Test
    void registrarFallo_marcaComoDisparado() {
        JuegoBattleship juego = new JuegoBattleship();

        juego.registrarFallo(2,1);
        assertTrue(juego.yaDisparado(2,1));
    }

    @Test
    void recibirDisparo_enAguaDevuelveFalse_yRepetidoFalse() {
        JuegoBattleship juego = new JuegoBattleship();

        boolean primero = juego.recibirDisparo(0,0);
        boolean segundo = juego.recibirDisparo(0,0);

        assertFalse(primero);
        assertFalse(segundo);
    }
}
