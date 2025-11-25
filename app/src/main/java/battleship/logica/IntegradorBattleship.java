package battleship.logica;
import battleship.gui.ReproductorSonido;
import javax.swing.SwingUtilities;

import battleship.gui.TableroGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class IntegradorBattleship {

    private static final int PUERTO = 12345;

    private final JuegoBattleship juego;
    private final String nombreJugador;
    private final boolean esServidor;
    private final String ipServidor; 

    private TableroGUI tableroGUI;

    private Socket socket;
    private ServerSocket serverSocket;
    private PrintWriter salida;
    private BufferedReader entrada;

    
    private final Object monitorDisparo = new Object();
    private int disparoFila = -1;
    private int disparoColumna = -1;

    private boolean turnoJugador;
    private boolean juegoTerminado = false;

    public IntegradorBattleship(JuegoBattleship juego,String nombreJugador,boolean esServidor,String ipServidor) {
        this.juego = juego;
        this.nombreJugador = nombreJugador;
        this.esServidor = esServidor;
        this.ipServidor = ipServidor;
    }

    
   public void iniciar() {
    try {
        conectar();
        intercambioNombres();
        prepararTablero();

        // Musica de fondo durante la partida
        ReproductorSonido.iniciarMusicaFondo();

        handshakeListo();
        bucleJuego();
    } catch (Exception e) {
        e.printStackTrace();
        if (tableroGUI != null) {
            final String msg = "Error de conexion: " + e.getMessage();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tableroGUI.mostrarMensaje(msg);
                }
            });
        }
    } finally {
        cerrarConexion();
    }
}


   

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
        
        juego.colocarBarcosAutomaticamente();

        
        SwingUtilities.invokeAndWait(() -> {
            tableroGUI = new TableroGUI(juego, nombreJugador, esServidor, ipServidor);
            tableroGUI.setIntegrador(this);
            tableroGUI.setVisible(true);
        });
    }

    private void handshakeListo() throws IOException {
        if (esServidor) {
            
            salida.println(ProtocoloBattleship.LISTO);
            String resp = entrada.readLine();
            System.out.println("Respuesta LISTO de cliente: " + resp);
            turnoJugador = true;
        } else {
            
            String resp = entrada.readLine();
            System.out.println("LISTO recibido del servidor: " + resp);
            salida.println(ProtocoloBattleship.LISTO);
            turnoJugador = false;
        }
    }

    

    private void bucleJuego() throws IOException {
        while (!juegoTerminado) {
            if (turnoJugador) {
                turnoJugador();
            } else {
                turnoRival();
            }
        }
    }

    

    private void turnoJugador() throws IOException {
        SwingUtilities.invokeLater(
                () -> tableroGUI.mostrarMensajeTurno("Tu turno: elige una casilla para disparar.")
        );

        int fila;
        int columna;

        
        while (true) {
            int[] disparo = esperarDisparoJugador();
            if (disparo == null) {
                return; 
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

        
        String mensajeDisparo = ProtocoloBattleship.construirMensajeDisparo(fila, columna);
        salida.println(mensajeDisparo);

        
        String respuesta = entrada.readLine();
        if (respuesta == null) {
            juegoTerminado = true;
            return;
        }

        procesarRespuestaDisparoDelOponente(respuesta, fila, columna);

        
        if (!juegoTerminado) {
            turnoJugador = false;
        }
    }

   
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

    
    public void manejarClickEnEnemigo(int fila, int columna) {
        synchronized (monitorDisparo) {
            
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
                
                break;
        }
    }

    

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
            
            return;
        }

        String[] coords = partes[1].split(",");
        int fila = Integer.parseInt(coords[0]);
        int columna = Integer.parseInt(coords[1]);

        
        boolean impacto = juego.recibirDisparo(fila, columna);

        String tipoBarco = "barco";

        
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

        // Detener musica de fondo
        ReproductorSonido.detenerMusicaFondo();
    }
}
