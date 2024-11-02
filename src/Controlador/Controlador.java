package Controlador;

import Modelo.*;
import Vista.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controlador implements ActionListener {
    private Modelo modelo;
    private Vista vista;
    private int movimientoActual;
    private List<TableroEstado> historicoTableros;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.movimientoActual = 0;
        this.historicoTableros = new ArrayList<>();
        vista.setControlador(this);

        try {
            cargarPartidaPGN("partida.pgn");
            inicializarHistoricoTableros();
            actualizarVista();
        } catch (Exception e) {
            System.err.println("Error al inicializar el controlador: " + e.getMessage());
        }
    }

    private void inicializarHistoricoTableros() {
        historicoTableros.clear();

        // Guardar estado inicial
        TableroAjedrez tableroInicial = new TableroAjedrez();
        historicoTableros.add(new TableroEstado(tableroInicial.getTablero()));

        // Crear tablero temporal para simular movimientos
        TableroAjedrez tableroTemp = new TableroAjedrez();

        // Simular movimientos
        for (String movimiento : modelo.getMovimientosPGN()) {
            if (movimiento != null && !movimiento.isEmpty()) {
                tableroTemp.realizarMovimiento(movimiento);
                historicoTableros.add(new TableroEstado(tableroTemp.getTablero()));
            }
        }

        movimientoActual = 0;
        modelo.reiniciarTablero();
    }

    private void cargarPartidaPGN(String archivo) {
        List<String> movimientos = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(archivo))) {
            StringBuilder contenidoMovimientos = new StringBuilder();

            // Lee el archivo línea por línea
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                // Ignora las líneas vacías y los metadatos (que empiezan con '[')
                if (!linea.isEmpty() && !linea.startsWith("[")) {
                    contenidoMovimientos.append(linea).append(" ");
                }
            }

            // Procesa el contenido de los movimientos
            String contenido = contenidoMovimientos.toString()
                    .replaceAll("\\d+\\.", "") // Elimina números de movimiento
                    .replaceAll("\\{.*?\\}", "") // Elimina comentarios
                    .replaceAll("1-0|0-1|1/2-1/2", "") // Elimina resultado
                    .trim();

            // Divide los movimientos y los añade a la lista
            String[] elementosMovimientos = contenido.split("\\s+");
            for (String movimiento : elementosMovimientos) {
                if (!movimiento.isEmpty()) {
                    movimientos.add(movimiento);
                    System.out.println("Movimiento leído: " + movimiento); // Para debug
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("No se pudo encontrar el archivo PGN: " + e.getMessage());
            e.printStackTrace();
        }

        modelo.cargarMovimientosPGN(movimientos);
    }

    private void actualizarVista() {
        if (movimientoActual >= 0 && movimientoActual < historicoTableros.size()) {
            TableroEstado estado = historicoTableros.get(movimientoActual);
            if (estado != null) {
                modelo.setTablero(estado.getEstado());
                vista.actualizarTablero(estado.getEstado());
                vista.mostrarMovimientos(modelo.getMovimientosPGN());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "ANTERIOR":
                if (movimientoActual > 0) {
                    movimientoActual--;
                    actualizarVista();
                }
                break;
            case "SIGUIENTE":
                if (movimientoActual < historicoTableros.size() - 1) {
                    movimientoActual++;
                    actualizarVista();
                }
                break;
        }
    }
}

