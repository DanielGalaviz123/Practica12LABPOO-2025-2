package battleship.logica;

import java.util.*;

import battleship.abstractas.Barco;
import battleship.interfaces.JuegoTablero;

public class JuegoBattleship implements JuegoTablero {

    private static final int TAMANIO_TABLERO = 10;

    private Tablero tableroPropio;
    private Tablero tableroEnemigo;

    private GestorBarcos flota;

    private Set<Posicion> posicionesDisparadas;

    public JuegoBattleship() {
        tableroPropio  = new Tablero(TAMANIO_TABLERO);
        tableroEnemigo = new Tablero(TAMANIO_TABLERO);
        inicializarTableros();

        flota = new GestorBarcos();
        flota.agregarBarco(new BarcoBasico("PORTAAVIONES", 'P', 5));
        flota.agregarBarco(new BarcoBasico("ACORAZADO",    'A', 4));
        flota.agregarBarco(new BarcoBasico("CRUCERO",      'C', 3));
        flota.agregarBarco(new BarcoBasico("SUBMARINO",    'S', 3));
        flota.agregarBarco(new BarcoBasico("DESTRUCTOR",   'D', 2));

        posicionesDisparadas = new HashSet<>();
    }

    private void inicializarTableros() {
        tableroPropio.llenar('~');
        tableroEnemigo.llenar('?');
    }

    // ================= API publica que usa BattleshipP2P =================

    @Override
    public void colocarBarcosAutomaticamente() {
        Random random = new Random();
        int limite = tableroPropio.getTamanio();

        for (Barco barco : flota.getBarcos()) {
            int tamanio    = barco.getTamanio();
            boolean colocado = false;

            while (!colocado) {
                boolean horizontal = random.nextBoolean();
                int fila    = random.nextInt(limite);
                int columna = random.nextInt(limite);

                if (puedeColocarBarco(fila, columna, tamanio, horizontal)) {
                    colocarBarco(fila, columna, tamanio, horizontal, barco.getSimbolo());
                    colocado = true;
                }
            }
        }
    }

    @Override
    public boolean recibirDisparo(int fila, int columna) {
        char actual = tableroPropio.getCelda(fila, columna);

        // ya se había disparado aquí
        if (actual == 'X' || actual == 'O') {
            return false;
        }

        // hay barco
        if (actual != '~') {
            Barco barco = flota.obtenerPorSimbolo(actual);
            if (barco != null) {
                barco.registrarImpacto();
            } else {
                System.out.println("Advertencia: barco no registrado con simbolo '" + actual + "'");
            }

            tableroPropio.setCelda(fila, columna, 'X');
            return true;
        } else {
            // agua
            tableroPropio.setCelda(fila, columna, 'O');
            return false;
        }
    }

    @Override
    public void registrarImpacto(int fila, int columna) {
        tableroEnemigo.setCelda(fila, columna, 'X');
        posicionesDisparadas.add(new Posicion(fila, columna));
    }

    @Override
    public void registrarFallo(int fila, int columna) {
        tableroEnemigo.setCelda(fila, columna, 'O');
        posicionesDisparadas.add(new Posicion(fila, columna));
    }

    @Override
    public boolean yaDisparado(int fila, int columna) {
        return posicionesDisparadas.contains(new Posicion(fila, columna));
    }

    @Override
    public String obtenerTipoBarcoEn(int fila, int columna) {
        char c = tableroPropio.getCelda(fila, columna);

        if (c == 'X') {
            return "DESCONOCIDO";
        }

        return flota.obtenerNombrePorSimbolo(c);
    }

    @Override
    public boolean estaBarcoHundido(String tipoBarco) {
        return flota.estaHundido(tipoBarco);
    }

    @Override
    public boolean todosBarcosHundidos() {
        return flota.todosHundidos();
    }

    @Override
    public void mostrarTableroPropio() {
        System.out.println("\n=== TU TABLERO ===");
        mostrarTablero(tableroPropio);

        System.out.println("\nEstado de tus barcos:");
        for (Barco barco : flota.getBarcos()) {
            int impactos = barco.getImpactos();
            int tamanio  = barco.getTamanio();
            String estado = barco.estaHundido()
                    ? "HUNDIDO"
                    : impactos + "/" + tamanio;
            System.out.println("  " + barco.getNombre() + ": " + estado);
        }
    }

    @Override
    public void mostrarTableroEnemigo() {
        System.out.println("\n=== TABLERO ENEMIGO ===");
        mostrarTablero(tableroEnemigo);
    }

    // ================= ayuda interna =================

    private boolean puedeColocarBarco(int fila, int columna, int tamanio, boolean horizontal) {
        int limite = tableroPropio.getTamanio();

        if (horizontal) {
            if (columna + tamanio > limite) return false;
            for (int i = columna; i < columna + tamanio; i++) {
                if (tableroPropio.getCelda(fila, i) != '~') return false;
            }
        } else {
            if (fila + tamanio > limite) return false;
            for (int i = fila; i < fila + tamanio; i++) {
                if (tableroPropio.getCelda(i, columna) != '~') return false;
            }
        }
        return true;
    }

    private void colocarBarco(int fila, int columna, int tamanio, boolean horizontal, char simbolo) {
        if (horizontal) {
            for (int i = columna; i < columna + tamanio; i++) {
                tableroPropio.setCelda(fila, i, simbolo);
            }
        } else {
            for (int i = fila; i < fila + tamanio; i++) {
                tableroPropio.setCelda(i, columna, simbolo);
            }
        }
    }

    private void mostrarTablero(Tablero tablero) {
        int limite = tablero.getTamanio();

        System.out.print("  ");
        for (int i = 0; i < limite; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < limite; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < limite; j++) {
                System.out.print(tablero.getCelda(i, j) + " ");
            }
            System.out.println();
        }

        System.out.println("\nLeyenda: ~=Agua, ?=Desconocido, X=Impacto, O=Fallo, Letras=Barcos");
    }


     public int getTamanioTablero() {
        return tableroPropio.getTamanio();
    }

    public char getCeldaPropia(int fila, int columna) {
        return tableroPropio.getCelda(fila, columna);
    }

    public char getCeldaEnemiga(int fila, int columna) {
        return tableroEnemigo.getCelda(fila, columna);
    }
}