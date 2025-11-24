package battleship;

import javax.swing.*;
import java.awt.*;

public class MenuInterfazGUI extends JFrame {

    private final JuegoBattleship juego;

    private JButton botonConsola;
    private JButton botonGrafica;
    private JButton botonSalir;

    public MenuInterfazGUI(JuegoBattleship juego) {
        super("Battleship - seleccion de interfaz");
        this.juego = juego;

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
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.add(titulo, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        setContentPane(panelPrincipal);

        configurarAcciones();
    }

    private void configurarAcciones() {

        // === MODO CONSOLA (tu implementacion original) ===
        botonConsola.addActionListener(e -> {
            dispose(); // cierro el menu y sigo en consola

            new Thread(() -> {
                BattleshipP2P juegoP2P = new BattleshipP2P();
                juegoP2P.iniciar();
            }).start();
        });

        // === MODO GRAFICO P2P ===
        botonGrafica.addActionListener(e -> {

            // 1) Pedir nombre
            String nombre = JOptionPane.showInputDialog(
                    this,
                    "Ingresa tu nombre:",
                    "Nombre jugador",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (nombre == null || nombre.trim().isEmpty()) {
                return; // cancelado
            }

            // 2) Preguntar rol
            Object[] opciones = { "Servidor (crear partida)", "Cliente (unirse a partida)" };
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

            // 3) Crear la logica de juego para este jugador
            JuegoBattleship juegoJugador = new JuegoBattleship();

            // 4) Crear el integrador P2P grafico y arrancar la partida en un hilo aparte
            IntegradorBattleship integrador =
                    new IntegradorBattleship(juegoJugador, nombre, esServidor, ipServidor);

            new Thread(integrador::iniciar).start();

            // Cerrar el menu
            dispose();
        });

        botonSalir.addActionListener(e -> System.exit(0));
    }

    // Main de prueba
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoBattleship juego = new JuegoBattleship();
            MenuInterfazGUI ventana = new MenuInterfazGUI(juego);
            ventana.setVisible(true);
        });
    }
}
