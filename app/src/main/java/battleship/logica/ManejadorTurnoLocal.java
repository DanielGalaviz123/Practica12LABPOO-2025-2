package battleship.logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import battleship.interfaces.JuegoTablero;

public class ManejadorTurnoLocal {

    private final JuegoTablero juego;
    private final PrintWriter salida;
    private final BufferedReader entrada;
    private final Scanner scanner;

    public ManejadorTurnoLocal(JuegoTablero juego,PrintWriter salida,BufferedReader entrada,Scanner scanner) {
        this.juego   = juego;
        this.salida  = salida;
        this.entrada = entrada;
        this.scanner = scanner;
    }

    public boolean ejecutarTurno() throws IOException {
        System.out.println("\n=== TU TURNO ===");
        juego.mostrarTableroEnemigo();
        juego.mostrarTableroPropio();

        int[] disparo = obtenerDisparoJugador();
        salida.println(ProtocoloBattleship.construirMensajeDisparo(disparo[0], disparo[1]));

        String respuesta = entrada.readLine();

        if (respuesta == null) {
            System.out.println("El oponente se desconectó o hubo un error en la comunicación.");
            return false;
        }

        try {
            ProtocoloBattleship.Mensaje mensaje =
                    ProtocoloBattleship.parsearMensaje(respuesta);

            switch (mensaje.comando) {
                case ProtocoloBattleship.IMPACTO:
                    System.out.println("¡IMPACTO en (" + mensaje.x + "," + mensaje.y + ")!");
                    juego.registrarImpacto(mensaje.x, mensaje.y);
                    return true;

                case ProtocoloBattleship.FALLO:
                    System.out.println("FALLO en (" + mensaje.x + "," + mensaje.y + ")");
                    juego.registrarFallo(mensaje.x, mensaje.y);
                    return true;

                case ProtocoloBattleship.HUNDIDO:
                    System.out.println("¡HUNDIDO! " + mensaje.tipoBarco +
                                       " en (" + mensaje.x + "," + mensaje.y + ")");
                    juego.registrarImpacto(mensaje.x, mensaje.y);
                    return true;

                case ProtocoloBattleship.JUEGO_TERMINADO:
                    System.out.println("¡FELICIDADES! ¡HAS GANADO!");
                    return false;

                default:
                    System.out.println("Respuesta inesperada: " + respuesta);
                    return true;
            }
        } catch (Exception e) {
            System.out.println("Error procesando respuesta: " + e.getMessage());
            System.out.println("Respuesta recibida: " + respuesta);
            return false;
        }
    }

    private int[] obtenerDisparoJugador() {
        while (true) {
            try {
                System.out.print("Ingresa coordenadas para disparar (fila,columna 0-9): ");
                String texto  = scanner.nextLine();
                String[] partes = texto.split(",");

                if (partes.length != 2) {
                    System.out.println("Formato inválido. Usa: fila,columna");
                    continue;
                }

                int fila    = Integer.parseInt(partes[0].trim());
                int columna = Integer.parseInt(partes[1].trim());

                if (fila >= 0 && fila < 10 && columna >= 0 && columna < 10) {
                    if (!juego.yaDisparado(fila, columna)) {
                        return new int[] { fila, columna };
                    } else {
                        System.out.println("Ya disparaste en esa posición.");
                    }
                } else {
                    System.out.println("Coordenadas fuera de rango. Usa números del 0 al 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa números válidos.");
            }
        }
    }
}
