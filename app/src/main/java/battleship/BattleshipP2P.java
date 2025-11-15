package battleship;

import java.io.*;
import java.net.*;
import java.util.*;

public class BattleshipP2P implements JuegoEnLiena {

    private static final int PUERTO = 12345;

    private Socket socket;
    private ServerSocket serverSocket;
    private PrintWriter salida;
    private BufferedReader entrada;

    private Jugador jugadorLocal;
    private boolean esServidor;

    private final Scanner scanner;

    public BattleshipP2P() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void iniciar() {
        System.out.println("=== BATTLESHIP P2P ===");
        System.out.print("Ingresa tu nombre: ");
        String nombre = scanner.nextLine();

        this.jugadorLocal = new Jugador(nombre);

        elegirModo();
    }

    private void elegirModo() {
        while (true) {
            System.out.println("\nSelecciona modo:");
            System.out.println("1. Crear partida (Esperar conexión)");
            System.out.println("2. Unirse a partida (Conectar a otro jugador)");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine();

            if ("1".equals(opcion)) {
                esServidor = true;
                esperarConexion();
                break;
            } else if ("2".equals(opcion)) {
                esServidor = false;
                conectarAPartida();
                break;
            } else {
                System.out.println("Opción inválida. Intenta nuevamente.");
            }
        }
    }

    private void esperarConexion() {
        try {
            System.out.println("\nIniciando servidor en puerto " + PUERTO + "...");
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Esperando conexión de otro jugador...");

            socket = serverSocket.accept();
            System.out.println("¡Jugador conectado desde: " + socket.getInetAddress() + "!");

            configurarFlujos();
            intercambiarNombres();
            iniciarJuego();

        } catch (IOException e) {
            System.err.println("Error al esperar conexión: " + e.getMessage());
        }
    }

    private void conectarAPartida() {
        try {
            System.out.print("\nIngresa la IP del otro jugador: ");
            String ip = scanner.nextLine();

            System.out.println("Conectando a " + ip + ":" + PUERTO + "...");
            socket = new Socket(ip, PUERTO);
            System.out.println("¡Conectado exitosamente!");

            configurarFlujos();
            intercambiarNombres();
            iniciarJuego();

        } catch (IOException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            System.out.println("¿Deseas intentar nuevamente? (s/n)");
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                conectarAPartida();
            }
        }
    }

    private void configurarFlujos() throws IOException {
        salida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void intercambiarNombres() throws IOException {
        if (esServidor) {
            String nombreOponente = entrada.readLine();
            salida.println(jugadorLocal.getNombre());
            System.out.println("Jugando contra: " + nombreOponente);
        } else {
            salida.println(jugadorLocal.getNombre());
            String nombreOponente = entrada.readLine();
            System.out.println("Jugando contra: " + nombreOponente);
        }
    }

    private void iniciarJuego() throws IOException {
        System.out.println("\n=== INICIANDO JUEGO ===");

        JuegoTablero juego = jugadorLocal.getJuego();

        juego.colocarBarcosAutomaticamente();
        System.out.println("Tus barcos han sido colocados automáticamente.");
        juego.mostrarTableroPropio();

        boolean juegoActivo = true;
        boolean miTurno = esServidor;

        ManejadorTurnoLocal manejadorLocal =
                new ManejadorTurnoLocal(juego, salida, entrada, scanner);

        ManejadorTurnoRemoto manejadorRemoto =
                new ManejadorTurnoRemoto(juego, salida, entrada);

        try {
            salida.println(ProtocoloBattleship.LISTO);
            String respuesta = entrada.readLine();

            if (respuesta == null) {
                System.out.println("El oponente se desconectó durante la inicialización.");
                return;
            }

            if (ProtocoloBattleship.LISTO.equals(respuesta)) {
                System.out.println("¡Ambos jugadores listos! El juego comienza.");

                if (miTurno) {
                    System.out.println("\n¡Tú comienzas!");
                } else {
                    System.out.println("\nEl oponente comienza...");
                }

                while (juegoActivo) {
                    if (miTurno) {
                        juegoActivo = manejadorLocal.ejecutarTurno();
                        if (juegoActivo) miTurno = false;
                    } else {
                        juegoActivo = manejadorRemoto.ejecutarTurno();
                        if (juegoActivo) miTurno = true;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void cerrarConexion() {
        try {
            if (entrada != null)      entrada.close();
            if (salida != null)       salida.close();
            if (socket != null)       socket.close();
            if (serverSocket != null) serverSocket.close();
            scanner.close();
            System.out.println("Conexión cerrada.");
        } catch (IOException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        BattleshipP2P juego = new BattleshipP2P();
        juego.iniciar();
    }
}
