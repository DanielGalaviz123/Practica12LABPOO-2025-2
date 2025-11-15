package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BarcoBasicoTest {

    @Test
    void constructor_inicializaDatos() {
        Barco barco = new BarcoBasico("CRUCERO",'C',3);

        assertEquals("CRUCERO", barco.getNombre());
        assertEquals('C',       barco.getSimbolo());
        assertEquals(3,         barco.getTamanio());
        assertEquals(0,         barco.getImpactos());
        assertFalse(barco.estaHundido());
    }

    @Test
    void registrarImpacto_incrementaImpactos() {
        Barco barco = new BarcoBasico("DESTRUCTOR",'D',2);

        barco.registrarImpacto();
        assertEquals(1, barco.getImpactos());
        assertFalse(barco.estaHundido());

        barco.registrarImpacto();
        assertEquals(2, barco.getImpactos());
        assertTrue(barco.estaHundido());
    }

    @Test
    void registrarImpacto_noSobrepasaElTamanio() {
        Barco barco = new BarcoBasico("SUBMARINO",'S',1);

        barco.registrarImpacto();
        barco.registrarImpacto();   
        assertEquals(1, barco.getImpactos());
        assertTrue(barco.estaHundido());
    }
}
