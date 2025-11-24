package battleship;

/**
 * Interfaz que la logica de red usa para avisar a la interfaz grafica
 * lo que pasa en el juego.
 */
public interface ManejadorEventosJuego {

    /**
     * Llamado cuando recibo la respuesta a un disparo que YO hice
     * (es decir, dispare a una casilla del tablero enemigo).
     *
     * @param fila      fila a la que dispare
     * @param columna   columna a la que dispare
     * @param resultado "FALLO", "IMPACTO" o "HUNDIDO"
     */
    void recibirResultadoDisparoPropio(int fila, int columna, String resultado);

    /**
     * Llamado cuando el rival dispara a mi tablero.
     *
     * @param fila      fila objetivo
     * @param columna   columna objetivo
     * @param resultado "FALLO", "IMPACTO" o "HUNDIDO" sobre mi tablero
     */
    void recibirDisparoRival(int fila, int columna, String resultado);

    /**
     * Llamado cuando cambia el turno.
     *
     * @param esMiTurno true si ahora yo debo jugar, false si es turno del rival
     */
    void actualizarTurno(boolean esMiTurno);

    /**
     * Llamado cuando la partida termina.
     *
     * @param yoGane true si yo gane, false si perdi
     */
    void finalizarJuego(boolean yoGane);
}
