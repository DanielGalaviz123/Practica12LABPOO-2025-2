package battleship;

import org.junit.jupiter.api.Test;

import battleship.logica.IntegradorBattleship;
import battleship.logica.JuegoBattleship;

import static org.junit.jupiter.api.Assertions.*;

class IntegradorBattleshipTest {

    @Test
    void manejarClickEnEnemigo_registraDisparoCuandoEsTurno() {
        JuegoBattleship juego = new JuegoBattleship();
        IntegradorBattleship integrador =new IntegradorBattleship(juego, "Jugador", true, null);

        
        integrador.setTurnoJugador(true);

        integrador.manejarClickEnEnemigo(3, 5);

        assertEquals(3, integrador.getDisparoFila());
        assertEquals(5, integrador.getDisparoColumna());
    }

    @Test
    void manejarClickEnEnemigo_ignoraClickSiNoEsTurno() {
        JuegoBattleship juego = new JuegoBattleship();
        IntegradorBattleship integrador =new IntegradorBattleship(juego, "Jugador", true, null);

        
        integrador.setTurnoJugador(false);

        integrador.manejarClickEnEnemigo(2, 4);

        
        assertEquals(-1, integrador.getDisparoFila());
        assertEquals(-1, integrador.getDisparoColumna());
    }

    @Test
    void manejarClickEnEnemigo_noSobrescribeSiYaHayDisparoPendiente() {
        JuegoBattleship juego = new JuegoBattleship();
        IntegradorBattleship integrador =new IntegradorBattleship(juego, "Jugador", true, null);

        integrador.setTurnoJugador(true);

        integrador.manejarClickEnEnemigo(1, 1); 
        integrador.manejarClickEnEnemigo(4, 7); 

        assertEquals(1, integrador.getDisparoFila());
        assertEquals(1, integrador.getDisparoColumna());
    }
}
