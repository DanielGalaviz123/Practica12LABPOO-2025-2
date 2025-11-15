package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JuegoBattleshipTest {

    @Test
    void constructor_estadoInicial() {
        JuegoBattleship juego = new JuegoBattleship();
        assertFalse( juego.todosBarcosHundidos() );
        assertFalse(juego.yaDisparado(0,0));
        assertEquals("DESCONOCIDO" , juego.obtenerTipoBarcoEn(0, 0));
    }

    @Test
    void registrarImpacto_marcaComoDisparado() {
        JuegoBattleship juego = new JuegoBattleship();

        juego.registrarImpacto(3 ,4);
        assertTrue( juego.yaDisparado(3, 4) );
    }

    @Test
    void registrarFallo_marcaComoDisparado() {
        JuegoBattleship juego = new JuegoBattleship();
        juego.registrarFallo( 2,1 );
        assertTrue(juego.yaDisparado(2 , 1));
    }

    @Test
    void registrarImpacto_noAfectaOtrasPosiciones() {
        JuegoBattleship juego = new JuegoBattleship();

        juego.registrarImpacto(1 , 1);
        assertFalse( juego.yaDisparado(1,2) );
        assertFalse( juego.yaDisparado( 2 , 1 ) );
    }

    @Test
    void recibirDisparo_enAguaDevuelveFalse_yRepetidoFalse() {
        JuegoBattleship juego = new JuegoBattleship();

        boolean primeraVez  = juego.recibirDisparo(0, 0);
        boolean segundaVez=juego.recibirDisparo(0 ,0);

        assertFalse(primeraVez );
        assertFalse( segundaVez);
    }

    @Test
    void todosBarcosHundidos_alInicioEsFalse() {
        JuegoBattleship juego=new JuegoBattleship();
        assertFalse( juego.todosBarcosHundidos() );
    }

    @Test
    void estaBarcoHundido_tipoInvalidoEsFalse() {
        JuegoBattleship juego = new JuegoBattleship();

        assertFalse( juego.estaBarcoHundido("BARCO_INVENTADO") );
    }

    @Test
    void colocarBarcosAutomaticamente_noLanzaExcepcion_yBarcosSiguenSinHundirse() {
        JuegoBattleship juego = new JuegoBattleship();

        juego.colocarBarcosAutomaticamente( );
        assertFalse( juego.todosBarcosHundidos());
    }
}
