package battleship;

import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * IntegradorBattleship (modo grafico P2P).
 *
 * - Maneja la conexion P2P (servidor/cliente).
 * - Crea y controla la ventana TableroGUI.
 * - Coordina los turnos y el envio/recepcion de mensajes usando ProtocoloBattleship.
 * - Recibe los clicks del jugador desde TableroGUI mediante manejarClickEnEnemigo().
 *
 * NO cambia la logica de JuegoBattleship: solo llama a sus metodos.
 */
public class IntegradorBattleship {

    private static final int PUERTO = 12345;

    private final JuegoBattleship juego;
    private final String nombreJugador;
    private final boolean esServidor;
    private final String ipServidor; // null si somos servidor

    private TableroGUI tableroGUI;

    private Socket socket;
    private ServerSocket serverSocket;
    private PrintWriter salida;
    private BufferedReader entrada;

    // Sincronizacion entre hilo de red y clicks de la GUI
    private final Object monitorDisparo = new Object();
    private int disparoFila = -1;
    private int disparoColumna = -1;

    private boolean turnoJugador;
    private boolean juegoTerminado = false;

    public IntegradorBattleship(JuegoBattleship juego,
                                String nombreJugador,
                                boolean esServidor,
                                String ipServidor) {
        this.juego = juego;
        this.nombreJugador = nombreJugador;
        this.esServidor = esServidor;
        this.ipServidor = ipServidor;
    }

    /**
     * Punto de entrada del modo grafico P2P.
     * Llamalo desde un hilo aparte (desde el menu).
     */
    public void iniciar() {
        try {
            conectar();
            intercambioNombres();
            prepararTablero();
            handshakeListo();
            bucleJuego();
        } catch (Exception e) {
            e.printStackTrace();
            if (tableroGUI != null) {
                final String msg = "Error de conexion: " + e.getMessage();
                SwingUtilities.invokeLater(() -> tableroGUI.mostrarMensaje(msg));
            }
        } finally {
            cerrarConexion();
        }
    }

    // =================== Conexion y handshake ===================

    private void conectar() throws IOException {
        if (esServidor) {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor grafico esperando en puerto " + PUERTO + "...");
            socket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + socket.getInetAddress());
        } else {
            System.out.println("Conectando a " + ipServidor + ":" + PUERTO + "...");
            socket = new Socket(ipServidor, PUERTO);
            System.out.println("Conectado al servidor grafico.");
        }

        salida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void intercambioNombres() throws IOException {
        if (esServidor) {
            String nombreOponente = entrada.readLine();
            salida.println(nombreJugador);
            System.out.println("Jugando contra (cliente): " + nombreOponente);
        } else {
            salida.println(nombreJugador);
            String nombreOponente = entrada.readLine();
            System.out.println("Jugando contra (servidor): " + nombreOponente);
        }
    }

    private void prepararTablero() throws Exception {
        // Colocamos barcos de este jugador
        juego.colocarBarcosAutomaticamente();

        // Crear la GUI en el hilo de eventos
        SwingUtilities.invokeAndWait(() -> {
            tableroGUI = new TableroGUI(juego, nombreJugador, esServidor, ipServidor);
            tableroGUI.setIntegrador(this);
            tableroGUI.setVisible(true);
        });
    }

    private void handshakeListo() throws IOException {
        if (esServidor) {
            // el servidor inicia
            salida.println(ProtocoloBattleship.LISTO);
            String resp = entrada.readLine();
            System.out.println("Respuesta LISTO de cliente: " + resp);
            turnoJugador = true;
        } else {
            // el cliente espera primero
            String resp = entrada.readLine();
            System.out.println("LISTO recibido del servidor: " + resp);
            salida.println(ProtocoloBattleship.LISTO);
            turnoJugador = false;
        }
    }

    // ======================= Bucle principal =======================

    private void bucleJuego() throws IOException {
        while (!juegoTerminado) {
            if (turnoJugador) {
                turnoJugador();
            } else {
                turnoRival();
            }
        }
    }

    // ---------- Turno local (jugador frente a la GUI) ----------

    private void turnoJugador() throws IOException {
        SwingUtilities.invokeLater(
                () -> tableroGUI.mostrarMensajeTurno("Tu turno: elige una casilla para disparar.")
        );

        int fila;
        int columna;

        // Esperar un disparo valido (no repetido)
        while (true) {
            int[] disparo = esperarDisparoJugador();
            if (disparo == null) {
                return; // juegoTerminado durante la espera
            }
            fila = disparo[0];
            columna = disparo[1];

            if (juego.yaDisparado(fila, columna)) {
                final String msg = "Ya disparaste en (" + fila + "," + columna + "). Elige otra casilla.";
                SwingUtilities.invokeLater(() -> tableroGUI.mostrarMensaje(msg));
            } else {
                break;
            }
        }

        // Enviar disparo al oponente
        String mensajeDisparo = ProtocoloBattleship.construirMensajeDisparo(fila, columna);
        salida.println(mensajeDisparo);

        // Esperar respuesta (IMPACTO/FALLO/HUNDIDO/JUEGO_TERMINADO)
        String respuesta = entrada.readLine();
        if (respuesta == null) {
            juegoTerminado = true;
            return;
        }

        procesarRespuestaDisparoDelOponente(respuesta, fila, columna);

        // Si el juego no ha terminado, pasa el turno al rival
        if (!juegoTerminado) {
            turnoJugador = false;
        }
    }

    /**
     * Espera hasta que el jugador haga click en una casilla enemiga.
     */
    private int[] esperarDisparoJugador() {
        synchronized (monitorDisparo) {
            while (!juegoTerminado && disparoFila < 0) {
                try {
                    monitorDisparo.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            if (juegoTerminado) {
                return null;
            }
            int[] res = new int[]{disparoFila, disparoColumna};
            disparoFila = -1;
            disparoColumna = -1;
            return res;
        }
    }

    /**
     * Lo llama TableroGUI cuando haces click en una casilla enemiga.
     */
    public void manejarClickEnEnemigo(int fila, int columna) {
        synchronized (monitorDisparo) {
            // Solo tiene efecto si es tu turno y no hay un disparo pendiente
            if (!turnoJugador || disparoFila >= 0) {
                return;
            }
            this.disparoFila = fila;
            this.disparoColumna = columna;
            monitorDisparo.notifyAll();
        }
    }

    private void procesarRespuestaDisparoDelOponente(String respuesta,
                                                     int fila, int columna) {
        String[] partes = respuesta.split("\\|");
        String comando = partes[0];

        switch (comando) {
            case ProtocoloBattleship.IMPACTO:
            case ProtocoloBattleship.HUNDIDO: {
                juego.registrarImpacto(fila, columna);

                String nombreBarco = (partes.length >= 3) ? partes[2] : "barco";

                final String msg;
                if (comando.equals(ProtocoloBattleship.HUNDIDO)) {
                    msg = "¡Impacto y hundiste el " + nombreBarco + "!";
                } else {
                    msg = "Impacto en (" + fila + "," + columna + ") sobre " + nombreBarco + ".";
                }

                SwingUtilities.invokeLater(() -> {
                    tableroGUI.actualizarTableroEnemigoDesdeJuego();
                    tableroGUI.mostrarMensaje(msg);
                });
                break;
            }
            case ProtocoloBattleship.FALLO: {
                juego.registrarFallo(fila, columna);

                SwingUtilities.invokeLater(() -> {
                    tableroGUI.actualizarTableroEnemigoDesdeJuego();
                    tableroGUI.mostrarMensaje("Fallo en (" + fila + "," + columna + ").");
                });
                break;
            }
            case ProtocoloBattleship.JUEGO_TERMINADO: {
                // Ultimo impacto para pintar bien el tablero enemigo
                juego.registrarImpacto(fila, columna);
                juegoTerminado = true;

                final String msg = "¡Has ganado la partida!";
                SwingUtilities.invokeLater(() -> {
                    tableroGUI.actualizarTableroEnemigoDesdeJuego();
                    tableroGUI.bloquearTableroEnemigo();
                    tableroGUI.mostrarMensaje(msg);
                });
                break;
            }
            default:
                // comando inesperado, lo ignoramos
                break;
        }
    }

    // ---------- Turno del rival ----------

    private void turnoRival() throws IOException {
        SwingUtilities.invokeLater(
                () -> tableroGUI.mostrarMensajeTurno("Turno del oponente. Esperando disparo...")
        );

        String linea = entrada.readLine();
        if (linea == null) {
            juegoTerminado = true;
            return;
        }

        String[] partes = linea.split("\\|");
        String comando = partes[0];

        if (!ProtocoloBattleship.DISPARAR.equals(comando)) {
            // mensaje inesperado
            return;
        }

        String[] coords = partes[1].split(",");
        int fila = Integer.parseInt(coords[0]);
        int columna = Integer.parseInt(coords[1]);

        // El rival dispara a nuestro tablero propio
        boolean impacto = juego.recibirDisparo(fila, columna);

        // Aqui podrias obtener el tipo real del barco si quieres
        String tipoBarco = "barco";

        // Actualizar tablero propio en la GUI
        SwingUtilities.invokeLater(() -> tableroGUI.actualizarTableroPropioDesdeJuego());

        String respuesta;
        if (impacto) {
            if (juego.todosBarcosHundidos()) {
                respuesta = ProtocoloBattleship.JUEGO_TERMINADO + "|" + fila + "," + columna + "|" + tipoBarco;
                juegoTerminado = true;

                final String msg = "La CPU (tu rival) ha hundido toda tu flota. Has perdido.";
                SwingUtilities.invokeLater(() -> {
                    tableroGUI.mostrarMensaje(msg);
                    tableroGUI.bloquearTableroEnemigo();
                });
            } else {
                // Aqui podrias diferenciar IMPACTO y HUNDIDO si tu juego lo soporta
                respuesta = ProtocoloBattleship.IMPACTO + "|" + fila + "," + columna + "|" + tipoBarco;
            }
        } else {
            respuesta = ProtocoloBattleship.FALLO + "|" + fila + "," + columna;
        }

        salida.println(respuesta);

        if (!juegoTerminado) {
            turnoJugador = true;
        }
    }

    // ======================= Limpieza =======================

    private void cerrarConexion() {
        try {
            if (entrada != null) entrada.close();
        } catch (IOException ignored) {}
        if (salida != null) salida.close();
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
    }
}
