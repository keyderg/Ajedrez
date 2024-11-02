package Modelo;

import java.util.ArrayList;
import java.util.List;

// Clase para representar el tablero
public class TableroAjedrez {
    private PiezaAjedrez[][] tablero;
    private List<String> historialMovimientos;

    public TableroAjedrez() {
        tablero = new PiezaAjedrez[8][8];
        historialMovimientos = new ArrayList<>();
        inicializarTablero();
    }

    private void inicializarTablero() {
        // Inicializar piezas blancas
        tablero[0][0] = new PiezaAjedrez("Torre", 'B', "a1");
        tablero[0][1] = new PiezaAjedrez("Caballo", 'B', "b1");
        tablero[0][2] = new PiezaAjedrez("Alfil", 'B', "c1");
        tablero[0][3] = new PiezaAjedrez("Dama", 'B', "d1");
        tablero[0][4] = new PiezaAjedrez("Rey", 'B', "e1");
        tablero[0][5] = new PiezaAjedrez("Alfil", 'B', "f1");
        tablero[0][6] = new PiezaAjedrez("Caballo", 'B', "g1");
        tablero[0][7] = new PiezaAjedrez("Torre", 'B', "h1");

        for (int i = 0; i < 8; i++) {
            tablero[1][i] = new PiezaAjedrez("Peón", 'B', (char)('a' + i) + "2");
        }

        // Inicializar piezas negras
        tablero[7][0] = new PiezaAjedrez("Torre", 'N', "a8");
        tablero[7][1] = new PiezaAjedrez("Caballo", 'N', "b8");
        tablero[7][2] = new PiezaAjedrez("Alfil", 'N', "c8");
        tablero[7][3] = new PiezaAjedrez("Dama", 'N', "d8");
        tablero[7][4] = new PiezaAjedrez("Rey", 'N', "e8");
        tablero[7][5] = new PiezaAjedrez("Alfil", 'N', "f8");
        tablero[7][6] = new PiezaAjedrez("Caballo", 'N', "g8");
        tablero[7][7] = new PiezaAjedrez("Torre", 'N', "h8");

        for (int i = 0; i < 8; i++) {
            tablero[6][i] = new PiezaAjedrez("Peón", 'N', (char)('a' + i) + "7");
        }
    }

    public void realizarMovimiento(String movimientoPGN) {
        if (movimientoPGN == null || movimientoPGN.isEmpty()) {
            return;
        }

        historialMovimientos.add(movimientoPGN);

        // Eliminar caracteres especiales de jaque/mate
        movimientoPGN = movimientoPGN.replaceAll("[+#]", "");

        try {
            // Obtener la pieza que se mueve
            char tipoPieza = 'P'; // Por defecto es peón
            if (Character.isUpperCase(movimientoPGN.charAt(0)) && movimientoPGN.charAt(0) != 'O') {
                tipoPieza = movimientoPGN.charAt(0);
                movimientoPGN = movimientoPGN.substring(1);
            }

            // Manejar enroque
            if (movimientoPGN.equals("O-O") || movimientoPGN.equals("O-O-O")) {
                // Implementar lógica de enroque
                return;
            }

            // Verificar si hay captura
            boolean hayCaptura = movimientoPGN.contains("x");
            if (hayCaptura) {
                movimientoPGN = movimientoPGN.replace("x", "");
            }

            // Obtener coordenadas destino (últimos 2 caracteres)
            String destino = movimientoPGN.substring(movimientoPGN.length() - 2);
            int columnaDestino = destino.charAt(0) - 'a';
            int filaDestino = Character.getNumericValue(destino.charAt(1)) - 1;

            // Obtener coordenadas de origen si están especificadas
            String origen = movimientoPGN.substring(0, movimientoPGN.length() - 2);
            Integer filaOrigen = null;
            Integer columnaOrigen = null;

            if (origen.length() > 0) {
                for (char c : origen.toCharArray()) {
                    if (Character.isLetter(c)) {
                        columnaOrigen = c - 'a';
                    } else if (Character.isDigit(c)) {
                        filaOrigen = Character.getNumericValue(c) - 1;
                    }
                }
            }

            // Buscar la pieza correcta para mover
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    PiezaAjedrez pieza = tablero[i][j];
                    if (pieza != null) {
                        boolean esLaPiezaCorrecta = false;

                        // Verificar si es la pieza que queremos mover
                        switch (tipoPieza) {
                            case 'P': esLaPiezaCorrecta = pieza.getNombre().equals("Peón"); break;
                            case 'N': esLaPiezaCorrecta = pieza.getNombre().equals("Caballo"); break;
                            case 'B': esLaPiezaCorrecta = pieza.getNombre().equals("Alfil"); break;
                            case 'R': esLaPiezaCorrecta = pieza.getNombre().equals("Torre"); break;
                            case 'Q': esLaPiezaCorrecta = pieza.getNombre().equals("Dama"); break;
                            case 'K': esLaPiezaCorrecta = pieza.getNombre().equals("Rey"); break;
                        }

                        // Verificar si coincide con las coordenadas de origen especificadas
                        if (esLaPiezaCorrecta &&
                                (columnaOrigen == null || columnaOrigen == j) &&
                                (filaOrigen == null || filaOrigen == i)) {

                            // Realizar el movimiento
                            tablero[filaDestino][columnaDestino] = pieza;
                            tablero[i][j] = null;
                            pieza.setPosicion(String.format("%c%d", (char)('a' + columnaDestino), filaDestino + 1));
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar movimiento: " + movimientoPGN);
            e.printStackTrace();
        }
    }

    public PiezaAjedrez[][] getTablero() {
        return tablero;
    }

    public List<String> getHistorialMovimientos() {
        return historialMovimientos;
    }

    public void setTablero(PiezaAjedrez[][] nuevoTablero) {
        if (nuevoTablero != null && nuevoTablero.length == 8 && nuevoTablero[0].length == 8) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    this.tablero[i][j] = nuevoTablero[i][j];
                }
            }
        }
    }
}