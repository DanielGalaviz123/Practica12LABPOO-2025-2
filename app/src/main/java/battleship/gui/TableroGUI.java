package battleship.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import battleship.logica.JuegoBattleship;
import battleship.logica.IntegradorBattleship;

public class TableroGUI extends JFrame {

    private static final int TAMANIO = 10;

    
    private static final Color COLOR_FONDO_AGUA_PROPIO  = new Color(180, 210, 255);
    private static final Color COLOR_TEXTO_AGUA_PROPIO  = new Color(0, 70, 130);
    private static final Color COLOR_FONDO_AGUA_ENEMIGO = new Color(180, 210, 255);

    private JLabel[][]  celdasPropias;
    private JButton[][] botonesEnemigo;

    private JLabel etiquetaEstado;
    private ImageIcon iconoAgua;
    private ImageIcon iconoImpacto;

    private final JuegoBattleship juego;
    private final String nombreJugador;
    private final boolean esServidor;
    private final String ipServidor;

    private IntegradorBattleship integrador;

    public TableroGUI(JuegoBattleship juego, String nombreJugador,
                      boolean esServidor, String ipServidor) {

        super("Battleship - Tableros");

        this.juego = juego;
        this.nombreJugador = nombreJugador;
        this.esServidor = esServidor;
        this.ipServidor = ipServidor;

        iconoAgua    = cargarIconoEscalado("/battleship/img/agua.png", 32, 32);
        iconoImpacto = cargarIconoEscalado("/battleship/img/ImpactoBarco.png", 32, 32);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(230, 235, 240));

        JPanel panelNorte = new JPanel(new GridLayout(1, 2));
        panelNorte.setOpaque(false);

        JLabel lblPropio  = new JLabel("Tu tablero", SwingConstants.CENTER);
        JLabel lblEnemigo = new JLabel("Tablero enemigo", SwingConstants.CENTER);

        Font fuenteTitulo = new Font("Arial", Font.BOLD, 22);
        lblPropio.setFont(fuenteTitulo);
        lblEnemigo.setFont(fuenteTitulo);

        panelNorte.add(lblPropio);
        panelNorte.add(lblEnemigo);

        
        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentro.setOpaque(false);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel panelPropio  = crearPanelPropio();
        JPanel panelEnemigo = crearPanelEnemigo();

        panelPropio.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180)),
                        "Tu flota")
        );
        panelEnemigo.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180)),
                        "Zona enemiga")
        );

        panelCentro.add(panelPropio);
        panelCentro.add(panelEnemigo);

      
        String textoEstado = "Jugador: " + nombreJugador;
        if (esServidor) {
            textoEstado += " (Servidor)";
        } else if (ipServidor != null && !ipServidor.isEmpty()) {
            textoEstado += " (Cliente, IP servidor: " + ipServidor + ")";
        } else {
            textoEstado += " (Cliente)";
        }

        etiquetaEstado = new JLabel(textoEstado, SwingConstants.CENTER);
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        add(panelNorte,  BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
        add(etiquetaEstado, BorderLayout.SOUTH);

        pack();
        setSize(900, 600);
        setLocationRelativeTo(null);

        actualizarTableroPropioDesdeJuego();
        actualizarTableroEnemigoDesdeJuego();
    }

    public TableroGUI(JuegoBattleship juego) {
        this(juego, "Jugador", true, null);
    }

    public TableroGUI() {
        this(crearJuegoPorDefecto(), "Jugador", true, null);
    }

    private static JuegoBattleship crearJuegoPorDefecto() {
        JuegoBattleship juego = new JuegoBattleship();
        juego.colocarBarcosAutomaticamente();
        return juego;
    }

    public void setIntegrador(IntegradorBattleship integrador) {
        this.integrador = integrador;
    }

  

    private JPanel crearPanelPropio() {
        JPanel panel = new JPanel(new GridLayout(TAMANIO, TAMANIO));
        celdasPropias = new JLabel[TAMANIO][TAMANIO];

        for (int fila = 0; fila < TAMANIO; fila++) {
            for (int col = 0; col < TAMANIO; col++) {

                JLabel celda = new JLabel("", SwingConstants.CENTER);
                celda.setOpaque(true);
                celda.setBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210)));
                celda.setFont(new Font("Consolas", Font.PLAIN, 14));
                celda.setPreferredSize(new Dimension(32, 32));

                
                pintarCeldaPropia(celda, '~');

                panel.add(celda);
                celdasPropias[fila][col] = celda;
            }
        }
        return panel;
    }

    

    private JPanel crearPanelEnemigo() {
        JPanel panel = new JPanel(new GridLayout(TAMANIO, TAMANIO));
        botonesEnemigo = new JButton[TAMANIO][TAMANIO];

        for (int fila = 0; fila < TAMANIO; fila++) {
            for (int col = 0; col < TAMANIO; col++) {

                JButton boton = new JButton();
                boton.setPreferredSize(new Dimension(32, 32));
                boton.setMargin(new Insets(0, 0, 0, 0));

                boton.setOpaque(true);
                boton.setContentAreaFilled(true);
                boton.setBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210)));
                boton.setBackground(COLOR_FONDO_AGUA_ENEMIGO);
                boton.setFocusPainted(false);

                if (iconoAgua != null) {
                    boton.setIcon(iconoAgua);
                }

                int f = fila;
                int c = col;

                boton.addActionListener(e -> {
                    if (integrador != null) {
                        integrador.manejarClickEnEnemigo(f, c);
                    } else {
                        String texto = "Click en (" + f + "," + c + ")";
                        System.out.println(texto);
                        etiquetaEstado.setText(texto);
                    }
                });

                panel.add(boton);
                botonesEnemigo[fila][col] = boton;
            }
        }

        return panel;
    }

    

    private void pintarCeldaPropia(JLabel celda, char celdaModelo) {
        celda.setOpaque(true);
        celda.setIcon(null);

        if (celdaModelo == '~') {
            
            celda.setBackground(COLOR_FONDO_AGUA_PROPIO);
            celda.setForeground(COLOR_TEXTO_AGUA_PROPIO);
            if (iconoAgua != null) {
                celda.setIcon(iconoAgua);
                celda.setText("");
            } else {
                celda.setText("~");
            }
        } else if (celdaModelo == 'X') {
            
            celda.setBackground(Color.WHITE);
            celda.setForeground(Color.BLACK);
            celda.setText("");
            if (iconoImpacto != null) {
                celda.setIcon(iconoImpacto);
            } else {
                celda.setBackground(Color.RED.darker());
                celda.setForeground(Color.WHITE);
                celda.setText("X");
            }
        } else if (celdaModelo == 'O') {
            
            celda.setBackground(Color.LIGHT_GRAY);
            celda.setForeground(Color.BLACK);
            celda.setText("O");
        } else {
            
            celda.setBackground(Color.BLACK);
            celda.setForeground(Color.WHITE);
            celda.setText(String.valueOf(celdaModelo));
        }
    }

    

    public void actualizarTableroPropioDesdeJuego() {
        int limite = juego.getTamanioTablero();

        for (int fila = 0; fila < limite; fila++) {
            for (int col = 0; col < limite; col++) {

                char celdaModelo = juego.getCeldaPropia(fila, col);
                JLabel celda     = celdasPropias[fila][col];

                pintarCeldaPropia(celda, celdaModelo);
            }
        }
    }

    public void actualizarTableroEnemigoDesdeJuego() {
        int limite = juego.getTamanioTablero();

        for (int fila = 0; fila < limite; fila++) {
            for (int col = 0; col < limite; col++) {

                char    celdaModelo = juego.getCeldaEnemiga(fila, col);
                JButton boton       = botonesEnemigo[fila][col];

                boton.setOpaque(true);
                boton.setBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210)));

                if (celdaModelo == '?' || celdaModelo == '~') {
                    
                    boton.setEnabled(true);
                    boton.setBackground(COLOR_FONDO_AGUA_ENEMIGO);
                    boton.setForeground(COLOR_TEXTO_AGUA_PROPIO);
                    boton.setText("");
                    boton.setIcon(iconoAgua);
                } else if (celdaModelo == 'X') {
                    
                    boton.setEnabled(false);
                    boton.setBackground(Color.WHITE);
                    boton.setText("");
                    if (iconoImpacto != null) {
                        boton.setIcon(iconoImpacto);
                    } else {
                        boton.setIcon(null);
                        boton.setText("X");
                    }
                } else if (celdaModelo == 'O') {
                    
                    boton.setEnabled(false);
                    boton.setBackground(Color.LIGHT_GRAY);
                    boton.setForeground(Color.BLACK);
                    boton.setIcon(null);
                    boton.setText("O");
                } else {
                    
                    boton.setEnabled(false);
                    boton.setBackground(Color.BLACK);
                    boton.setForeground(Color.WHITE);
                    boton.setIcon(null);
                    boton.setText(String.valueOf(celdaModelo));
                }
            }
        }
    }

    

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void mostrarMensajeTurno(String mensaje) {
        etiquetaEstado.setText(mensaje);
    }

    public void bloquearTableroEnemigo() {
        for (int fila = 0; fila < botonesEnemigo.length; fila++) {
            for (int col = 0; col < botonesEnemigo[fila].length; col++) {
                botonesEnemigo[fila][col].setEnabled(false);
            }
        }
    }

    private ImageIcon cargarIconoEscalado(String ruta, int ancho, int alto) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontro la imagen: " + ruta);
            return null;
        }
        ImageIcon original = new ImageIcon(url);
        Image imgEscalada = original.getImage().getScaledInstance(
                ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imgEscalada);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            TableroGUI ventana = new TableroGUI();
            ventana.setVisible(true);
        });
    }
}
