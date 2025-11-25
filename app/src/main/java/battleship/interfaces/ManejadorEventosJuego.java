package battleship.interfaces;

import java.awt.event.*;

import battleship.logica.*;

public interface ManejadorEventosJuego {
    //guardar
    //modificacion
    void recibirResultadoDisparoPropio(int fila, int columna, String resultado);

    //
    void recibirDisparoRival(int fila, int columna, String resultado);

    //
    void actualizarTurno(boolean esMiTurno);

    //
    void finalizarJuego(boolean yoGane);
}
