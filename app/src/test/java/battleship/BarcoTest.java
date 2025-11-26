package battleship;

import battleship.abstractas.Barco;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BarcoTest {

    //.
    private static class BarcoPrueba extends Barco {
        public BarcoPrueba(int tamanio) {
            super("PRUEBA", 'P', tamanio);
        }
    }

    @Test
    void barcoNoEstaHundidoAlCrearse() {
        Barco barco = new BarcoPrueba(3);

        assertFalse(barco.estaHundido(),
                "Un barco nuevo no deberia estar hundido");
    }

    @Test
    void barcoSeHundeAlAlcanzarNumeroDeImpactos() {
        Barco barco = new BarcoPrueba(2);

        // Primer impacto: aun no se hunde
        barco.registrarImpacto();
        assertFalse(barco.estaHundido(),
                "Con un impacto y tamanio 2 no debe hundirse");

        // Segundo impacto: ahora si
        barco.registrarImpacto();
        assertTrue(barco.estaHundido(),
                "Con impactos == tamanio debe estar hundido");

        // Impacto extra: no deberia "deshundirse" ni fallar
        barco.registrarImpacto();
        assertTrue(barco.estaHundido(),
                "Aunque se llamen mas impactos, sigue hundido");
    }
}
