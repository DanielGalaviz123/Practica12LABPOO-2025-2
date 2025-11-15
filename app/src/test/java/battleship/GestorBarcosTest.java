package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GestorBarcosTest {

    @Test
    void agregarBarco_sePuedeRecuperarPorNombreYSimbolo() {
        GestorBarcos flota = new GestorBarcos();

        Barco porta = new BarcoBasico("PORTAAVIONES",'P',5);
        flota.agregarBarco(porta);

        Barco recuperado = flota.obtenerPorSimbolo('P');
        assertNotNull(recuperado);
        assertEquals("PORTAAVIONES", recuperado.getNombre());
    }

    @Test
    void obtenerNombrePorSimbolo_desconocidoDaDESCONOCIDO() {
        GestorBarcos flota = new GestorBarcos();

        String nombre = flota.obtenerNombrePorSimbolo('Z');
        assertEquals("DESCONOCIDO", nombre);
    }

    @Test
    void estaHundido_y_todosHundidos() {
        GestorBarcos flota = new GestorBarcos();

        Barco b1 = new BarcoBasico("CRUCERO",'C',3);
        Barco b2 = new BarcoBasico("DESTRUCTOR",'D',2);

        flota.agregarBarco(b1);
        flota.agregarBarco(b2);

        assertFalse(flota.estaHundido("CRUCERO"));
        assertFalse(flota.estaHundido("DESTRUCTOR"));
        assertFalse(flota.todosHundidos());

        b1.registrarImpacto();
        b1.registrarImpacto();
        b1.registrarImpacto();
        assertTrue(flota.estaHundido("CRUCERO"));
        assertFalse(flota.todosHundidos());

        b2.registrarImpacto();
        b2.registrarImpacto();
        assertTrue(flota.estaHundido("DESTRUCTOR"));
        assertTrue(flota.todosHundidos());
    }
}
