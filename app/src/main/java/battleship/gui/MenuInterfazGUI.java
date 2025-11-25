package battleship.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import battleship.logica.BattleshipP2P;
import battleship.logica.IntegradorBattleship;
import battleship.logica.JuegoBattleship;
import battleship.interfaces.*;

public class MenuInterfazGUI extends JFrame {

    private final JuegoBattleship juego;

    private JButton botonConsola;
    private JButton botonGrafica;
    private JButton botonSalir;

    private VentanaDeEsperaGUI ventanaEspera;

    public MenuInterfazGUI(JuegoBattleship juego) {
        super("Battleship - seleccion de interfaz");
        this.juego = juego;

         // ===== NUEVO: iniciar musica de fondo desde el menu =====
        ReproductorSonido.iniciarMusicaFondo();
        // =====

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel titulo = new JLabel("Battleship P2P", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        botonConsola = new JButton("Modo consola");
        botonGrafica = new JButton("Modo grafico (P2P)");
        botonSalir   = new JButton("Salir");

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 10));
        panelBotones.add(botonConsola);
        panelBotones.add(botonGrafica);
        panelBotones.add(botonSalir);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.add(titulo, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        setContentPane(panelPrincipal);

        configurarAcciones();
    }

    private void configurarAcciones() {

        // ===== MODO CONSOLA =====
        botonConsola.addActionListener(e -> {
            dispose();

            new Thread(() -> {
                BattleshipP2P juegoP2P = new BattleshipP2P();
                juegoP2P.iniciar();
            }).start();
        });

        // ===== MODO GRAFICO P2P =====
        botonGrafica.addActionListener(e -> {

            // 1) Pedir nombre
            String nombre = JOptionPane.showInputDialog(
                    this,
                    "Ingresa tu nombre:",
                    "Nombre jugador",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (nombre == null || nombre.trim().isEmpty()) {
                return;
            }

            // 2) Elegir rol
            Object[] opciones = {
                    "Servidor (crear partida)",
                    "Cliente (unirse a partida)"
            };
            int opcion = JOptionPane.showOptionDialog(
                    this,
                    "Selecciona el rol:",
                    "Modo grafico P2P",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );
            if (opcion == JOptionPane.CLOSED_OPTION) {
                return;
            }

            boolean esServidor = (opcion == 0);
            String ipServidor = null;

            if (!esServidor) {
                ipServidor = JOptionPane.showInputDialog(
                        this,
                        "Ingresa la IP del servidor:",
                        "Direccion del servidor",
                        JOptionPane.QUESTION_MESSAGE
                );
                if (ipServidor == null || ipServidor.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Debes ingresar una IP valida para conectarte como cliente.",
                            "IP requerida",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            // 3) Crear juego para este jugador
            final JuegoBattleship juegoJugador = new JuegoBattleship();

            // 4) Crear integrador
            final IntegradorBattleship integrador =
                    new IntegradorBattleship(juegoJugador,
                            nombre, esServidor, ipServidor);

            // 5) Crear ventana de espera (IMPORTANTE: padre null)
            String mensajeEspera;
            if (esServidor) {
                mensajeEspera = "Esperando conexion del otro jugador...";
            } else {
                mensajeEspera = "Conectando con el servidor: " + ipServidor + "...";
            }

            ventanaEspera = new VentanaDeEsperaGUI(null, mensajeEspera);

            // 6) Lanzar hilo con la logica de red
            Thread hilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    integrador.iniciar();  // aqui esperas conexion y luego ya se muestra el tablero

                    // Cuando terminar iniciar, cerrar la ventana de espera en el hilo de Swing
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (ventanaEspera != null) {
                                ventanaEspera.dispose();
                                ventanaEspera = null;
                            }
                        }
                    });
                }
            });
            hilo.start();

            // 7) Mostrar la ventana de espera
            ventanaEspera.setVisible(true);

            // 8) Cerrar el menu (ya no afecta a la ventana porque su padre es null)
            dispose();
        });

        botonSalir.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoBattleship juego = new JuegoBattleship();
            MenuInterfazGUI ventana = new MenuInterfazGUI(juego);
            ventana.setVisible(true);
        });
    }
}
