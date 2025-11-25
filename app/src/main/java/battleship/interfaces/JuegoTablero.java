package battleship.interfaces;

public interface JuegoTablero {

    void colocarBarcosAutomaticamente();

    boolean recibirDisparo(int fila, int columna);

    void registrarImpacto(int fila, int columna);

    void registrarFallo(int fila, int columna);

    boolean yaDisparado(int fila, int columna);

    String obtenerTipoBarcoEn(int fila, int columna);

    boolean estaBarcoHundido(String tipoBarco);

    boolean todosBarcosHundidos();

    void mostrarTableroPropio();

    void mostrarTableroEnemigo();
}
