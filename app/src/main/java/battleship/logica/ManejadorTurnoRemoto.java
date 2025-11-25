package battleship.logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import battleship.interfaces.JuegoTablero;

public class ManejadorTurnoRemoto {

    private final JuegoTablero juego;
    private final PrintWriter salida;
    private final BufferedReader entrada;

    public ManejadorTurnoRemoto(JuegoTablero juego,
                                PrintWriter salida,
                                BufferedReader entrada) {
        this.juego   = juego;
        this.salida  = salida;
        this.entrada = entrada;
    }

    public boolean ejecutarTurno() throws IOException {
        System.out.println("\n=== TURNO DEL OPONENTE ===");
        System.out.println("Esperando disparo del oponente...");

        String mensajeEntrante = entrada.readLine();

        if (mensajeEntrante == null) {
            System.out.println("El oponente se desconectó.");
            return false;
        }

        try {
            ProtocoloBattleship.Mensaje mensaje =
                    ProtocoloBattleship.parsearMensaje(mensajeEntrante);

            if (ProtocoloBattleship.DISPARAR.equals(mensaje.comando)) {

                boolean impacto = juego.recibirDisparo(mensaje.x, mensaje.y);

                if (impacto) {
                    String tipoBarco = juego.obtenerTipoBarcoEn(mensaje.x, mensaje.y);

                    if ("DESCONOCIDO".equals(tipoBarco)) {
                        salida.println(ProtocoloBattleship.construirMensajeResultado(
                                ProtocoloBattleship.IMPACTO, mensaje.x, mensaje.y, null));
                        System.out.println("El oponente impactó en (" +
                                mensaje.x + "," + mensaje.y + ")");
                    } else if (juego.estaBarcoHundido(tipoBarco)) {
                        salida.println(ProtocoloBattleship.construirMensajeResultado(
                                ProtocoloBattleship.HUNDIDO, mensaje.x, mensaje.y, tipoBarco));

                        if (juego.todosBarcosHundidos()) {
                            salida.println(ProtocoloBattleship.JUEGO_TERMINADO);
                            System.out.println("El oponente hundió tu " + tipoBarco);
                            System.out.println("¡HAS PERDIDO!");
                            return false;
                        } else {
                            System.out.println("El oponente hundió tu " + tipoBarco +
                                    " en (" + mensaje.x + "," + mensaje.y + ")");
                        }
                    } else {
                        salida.println(ProtocoloBattleship.construirMensajeResultado(
                                ProtocoloBattleship.IMPACTO, mensaje.x, mensaje.y, null));
                        System.out.println("El oponente impactó en (" +
                                mensaje.x + "," + mensaje.y + ")");
                    }
                } else {
                    salida.println(ProtocoloBattleship.construirMensajeResultado(
                            ProtocoloBattleship.FALLO, mensaje.x, mensaje.y, null));
                    System.out.println("El oponente falló en (" +
                            mensaje.x + "," + mensaje.y + ")");
                }
            }

            juego.mostrarTableroPropio();
            return true;

        } catch (Exception e) {
            System.out.println("Error procesando mensaje del oponente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
