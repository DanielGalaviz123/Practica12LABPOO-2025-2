package battleship.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana simple que muestra un mensaje de espera
 * mientras el otro jugador se conecta.
 */
public class VentanaDeEsperaGUI extends JDialog {

    private JLabel etiquetaMensaje;
    private JButton botonCancelar;
    private boolean cancelado;

    public VentanaDeEsperaGUI(Frame ventanaPadre, String mensaje) {
        // false => NO modal, asi no bloquea la aplicacion
        super(ventanaPadre, "Esperando al otro jugador", false);

        etiquetaMensaje = new JLabel(mensaje, SwingConstants.CENTER);
        botonCancelar = new JButton("Cancelar");
        cancelado = false;

        configurarVentana();
        configurarEventos();
    }

    private void configurarVentana() {
        setLayout(new BorderLayout(10, 10));

        etiquetaMensaje.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(etiquetaMensaje, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(botonCancelar);
        add(panelBoton, BorderLayout.SOUTH);

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(320, 150);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void configurarEventos() {
        botonCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelado = true;
                dispose();
            }
        });
    }

    /**
     * Permite cambiar el mensaje mostrado mientras se espera.
     */
    public void actualizarMensaje(String nuevoMensaje) {
        etiquetaMensaje.setText(nuevoMensaje);
    }

    /**
     * Indica si el usuario presiono el boton Cancelar.
     */
    public boolean fueCancelado() {
        return cancelado;
    }
}
