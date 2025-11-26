package battleship;

import org.junit.jupiter.api.Test;

import battleship.gui.VentanaDeEsperaGUI;

import static org.junit.jupiter.api.Assertions.*;

class VentanaDeEsperaGUITest {

    @Test
    void constructor_iniciaSinCancelarYConMensaje() {
        VentanaDeEsperaGUI ventana =new VentanaDeEsperaGUI(null, "Esperando rival...");

        assertFalse(ventana.fueCancelado());
        assertEquals("Esperando rival...", ventana.getTextoMensaje());

        ventana.dispose();
    }

    @Test
    void actualizarMensaje_cambiaElTextoMostrado() {
        VentanaDeEsperaGUI ventana =new VentanaDeEsperaGUI(null, "Mensaje inicial");

        ventana.actualizarMensaje("Nuevo mensaje");

        assertEquals("Nuevo mensaje", ventana.getTextoMensaje());

        ventana.dispose();
    }

    @Test
    void botonCancelar_marcaVentanaComoCancelada() {
        VentanaDeEsperaGUI ventana =new VentanaDeEsperaGUI(null, "Esperando...");

        ventana.getBotonCancelar().doClick();

        assertTrue(ventana.fueCancelado());

        ventana.dispose();
    }
}
