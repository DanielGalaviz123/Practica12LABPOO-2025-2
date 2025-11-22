package battleship;

import javax.swing.SwingUtilities;

public class BattleshipMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // crear la logica del juego
            JuegoBattleship juego = new JuegoBattleship();

            // pasarla al menu
            MenuInterfazGUI ventana = new MenuInterfazGUI(juego);
            ventana.setVisible(true);
        });
    }
}
