package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProtocoloBattleshipTest {

    @Test
    void construirMensajeDisparo_formatoCorrecto() {
        String mensaje = ProtocoloBattleship.construirMensajeDisparo(3, 5);
        assertEquals("DISPARAR|3,5", mensaje);
    }

    @Test
    void construirMensajeResultado_conTipoBarco() {
        String mensaje = ProtocoloBattleship.construirMensajeResultado(
                ProtocoloBattleship.HUNDIDO,
                4, 7,
                "ACORAZADO"
        );

        assertEquals("HUNDIDO|4,7|ACORAZADO", mensaje);
    }

    @Test
    void construirMensajeResultado_sinTipoBarco() {
        String mensaje = ProtocoloBattleship.construirMensajeResultado(
                ProtocoloBattleship.FALLO,
                2, 1,
                null
        );

        assertEquals("FALLO|2,1", mensaje);
    }

    @Test
    void parsearMensaje_disparar_valido() {
        ProtocoloBattleship.Mensaje m =
                ProtocoloBattleship.parsearMensaje("DISPARAR|3,5");

        assertEquals(ProtocoloBattleship.DISPARAR, m.comando);
        assertEquals(3, m.x);
        assertEquals(5, m.y);
        assertNull(m.tipoBarco);
    }

    @Test
    void parsearMensaje_impacto_conTipoBarco() {
        ProtocoloBattleship.Mensaje m =
                ProtocoloBattleship.parsearMensaje("HUNDIDO|4,7|CRUCERO");

        assertEquals(ProtocoloBattleship.HUNDIDO, m.comando);
        assertEquals(4, m.x);
        assertEquals(7, m.y);
        assertEquals("CRUCERO", m.tipoBarco);
    }

    @Test
    void parsearMensaje_comandoSimple() {
        ProtocoloBattleship.Mensaje m =
                ProtocoloBattleship.parsearMensaje("LISTO");

        assertEquals(ProtocoloBattleship.LISTO, m.comando);
        assertEquals(-1, m.x);
        assertEquals(-1, m.y);
        assertNull(m.tipoBarco);
    }

    
}
