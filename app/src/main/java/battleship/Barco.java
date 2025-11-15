package battleship;

public abstract class Barco implements Hundible {

    private final String nombre;
    private final char simbolo;
    private final int tamanio;
    private int impactos;

    public Barco(String nombre, char simbolo, int tamanio) {
        this.nombre  = nombre;
        this.simbolo = simbolo;
        this.tamanio = tamanio;
        this.impactos = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public char getSimbolo() {
        return simbolo;
    }

    @Override
    public int getTamanio() {
        return tamanio;
    }

    @Override
    public int getImpactos() {
        return impactos;
    }

    @Override
    public void registrarImpacto() {
        if (impactos < tamanio) {
            impactos++;
        }
    }

    @Override
    public boolean estaHundido() {
        return impactos >= tamanio;
    }
}
