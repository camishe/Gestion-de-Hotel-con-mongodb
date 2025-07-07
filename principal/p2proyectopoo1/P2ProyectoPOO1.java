package principal.p2proyectopoo1;

import Controlador.ControladorVentanaPrincipal;
import Vista.VentanaPrinciapl;

public class P2ProyectoPOO1 {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            VentanaPrinciapl vista = new VentanaPrinciapl();
            new ControladorVentanaPrincipal(vista);
            vista.setVisible(true);
        });
    }
}
