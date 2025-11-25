package battleship.logica;

public class Tablero {

    private final int tamanio;
    private final char[][] celdas;

    public Tablero(int tamanio) {
        this.tamanio = tamanio;
        this.celdas  = new char[tamanio][tamanio];
    }

    public int getTamanio() {
        return tamanio;
    }

    public void llenar(char valor) {
        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                celdas[i][j] = valor;
            }
        }
    }

    public char getCelda(int fila, int columna) {
        return celdas[fila][columna];
    }

    public void setCelda(int fila, int columna, char valor) {
        celdas[fila][columna] = valor;
    }
}
