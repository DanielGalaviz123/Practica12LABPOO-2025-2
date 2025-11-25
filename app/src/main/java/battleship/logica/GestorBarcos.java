package battleship.logica;

import java.util.*;

import battleship.abstractas.Barco;

public class GestorBarcos {

    private final Map<String, Barco>    barcosPorNombre;
    private final Map<Character, Barco> barcosPorSimbolo;

    public GestorBarcos() {
        this.barcosPorNombre  = new LinkedHashMap<>();
        this.barcosPorSimbolo = new HashMap<>();
    }

    public void agregarBarco(Barco barco) {
        barcosPorNombre.put(barco.getNombre(), barco);
        barcosPorSimbolo.put(barco.getSimbolo(), barco);
    }

    public Barco obtenerPorSimbolo(char simbolo) {
        return barcosPorSimbolo.get(simbolo);
    }

    public String obtenerNombrePorSimbolo(char simbolo) {
        Barco barco = barcosPorSimbolo.get(simbolo);
        if (barco == null) {
            return "DESCONOCIDO";
        }
        return barco.getNombre();
    }

    public boolean estaHundido(String nombreBarco) {
        Barco barco = barcosPorNombre.get(nombreBarco);
        if (barco == null) {
            return false;
        }
        return barco.estaHundido();
    }

    public boolean todosHundidos() {
        for (Barco barco : barcosPorNombre.values()) {
            if (!barco.estaHundido()) {
                return false;
            }
        }
        return true;
    }

    public Collection<Barco> getBarcos() {
        return barcosPorNombre.values();
    }
}
