package battleship;

import javax.swing.SwingUtilities;

public class BattleshipMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoBattleship juego = new JuegoBattleship();
            MenuInterfazGUI ventana = new MenuInterfazGUI(juego);
            ventana.setVisible(true);
        });
    }
}
