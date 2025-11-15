package battleship;

public class Jugador {

    private String nombre;
    private JuegoBattleship juego;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.juego  = new JuegoBattleship();
    }

    public String getNombre() {
        return nombre;
    }

    public JuegoBattleship getJuego() {
        return juego;
    }
}
