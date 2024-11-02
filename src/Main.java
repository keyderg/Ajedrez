import Modelo.*;
import Controlador.*;
import Vista.Vista;

public class Main {
    public static void main(String[] args) {
        // Asegurarse de que la interfaz gráfica se ejecute en el EDT
        javax.swing.SwingUtilities.invokeLater(() -> {
            Modelo modelo = new Modelo();
            Vista vista = new Vista();
            Controlador controlador = new Controlador(modelo, vista);
            vista.setVisible(true);
        });
    }
}
