package Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import Model.Memoria;
import Model.Proceso;

import java.util.List;
import java.util.Random;

public class MemoriaController {
    @FXML private Pane memoriaPane;
    @FXML private TableView<Proceso> tabla;
    @FXML private TableColumn<Proceso, String> colId;
    @FXML private TableColumn<Proceso, Integer> colTam;
    @FXML private Label lblEspacioLibre;
    @FXML private Label lblCola;
    @FXML private TextField txtId;
    @FXML private TextField txtTam;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCompactar;


    public class MemoriaController {

    @FXML private Pane memoriaPane;           // Pane donde se dibuja la base
    @FXML private TableView<Proceso> tabla;   // TableView para procesos (usa Proceso simple)
    @FXML private TableColumn<Proceso, String> colId;
    @FXML private TableColumn<Proceso, Integer> colTam;
    @FXML private Label lblEspacioLibre;
    @FXML private Label lblCola;
    @FXML private TextField txtId;
    @FXML private TextField txtTam;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCompactar;

    private Memoria memoria;
    private ObservableList<Proceso> procesosObservable;
    private final Random rnd = new Random();

    // Mapa simple para recordar color por proceso id
    private final java.util.Map<String, Color> colorMap = new java.util.HashMap<>();

    // Ancho en pixeles del rectángulo total (puede ajustarse dinámicamente)
    private static final double MEMORIA_WIDTH = 600;
    private static final double MEMORIA_HEIGHT = 60;

    @FXML
    public void initialize() {
        // Inicializar memoria con capacidad 100
        memoria = new Memoria(100);

        // Inicializar tabla
        procesosObservable = FXCollections.observableArrayList();
        tabla.setItems(procesosObservable);

        // Si usas FXML, configura las columnas en el FXML o aquí con cellValueFactory
        // Ejemplo simple si las columnas están definidas para propiedades públicas:
        // colId.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getId()));
        // colTam.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTamaño()));

        // Registrar listener para actualizar UI cuando cambie la memoria
        memoria.addListener((bloques, espacioLibre, colaSize) -> {
            Platform.runLater(() -> {
                actualizarLabels(espacioLibre, colaSize);
                redibujarMemoria(bloques);
                sincronizarTabla(bloques);
            });
        });

        // Dibujar estado inicial
        redibujarMemoria(memoria.getBloquesSnapshot());
        actualizarLabels(memoria.espacioLibre(), memoria.getColaSize());

        // Botones acciones
        btnAgregar.setOnAction(e -> onAgregarProceso());
        btnEliminar.setOnAction(e -> onEliminarProceso());
        btnCompactar.setOnAction(e -> onCompactar());
    }

    // Añadir proceso desde los campos de texto
    private void onAgregarProceso() {
        String id = txtId.getText() != null ? txtId.getText().trim() : "";
        String tamStr = txtTam.getText() != null ? txtTam.getText().trim() : "";
        if (id.isEmpty() || tamStr.isEmpty()) {
            mostrarAlerta("Datos incompletos", "Ingrese id y tamaño del proceso.");
            return;
        }
        int tam;
        try {
            tam = Integer.parseInt(tamStr);
            if (tam <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarAlerta("Tamaño inválido", "Ingrese un número entero positivo para el tamaño.");
            return;
        }

        Proceso p = new Proceso(id, tam);
        p.marcarLlegada();
        // asignar color si no existe
        colorMap.putIfAbsent(id, generarColorAleatorio());

        boolean insertado = memoria.agregarProceso(p);
        if (!insertado) {
            mostrarInformacion("En espera", "El proceso se ha colocado en la cola de espera.");
        }
        // limpiar campos
        txtId.clear();
        txtTam.clear();
    }

    // Eliminar proceso seleccionado en la tabla
    private void onEliminarProceso() {
        Proceso seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccionar proceso", "Seleccione un proceso en la tabla para eliminar.");
            return;
        }
        boolean eliminado = memoria.eliminarProceso(seleccionado.getId());
        if (!eliminado) {
            mostrarAlerta("No encontrado", "El proceso no se encontró en memoria.");
        }
    }

    // Compactar memoria
    private void onCompactar() {
        memoria.compactar();
    }

    // Redibuja el Pane con los bloques actuales
    private void redibujarMemoria(List<Memoria.Block> bloques) {
        memoriaPane.getChildren().clear();

        // Fondo (memoria total)
        Rectangle fondo = new Rectangle(MEMORIA_WIDTH, MEMORIA_HEIGHT);
        fondo.setFill(Color.LIGHTGRAY);
        fondo.setStroke(Color.BLACK);
        memoriaPane.getChildren().add(fondo);

        double cursorX = 0;
        for (Memoria.Block b : bloques) {
            double ancho = MEMORIA_WIDTH * b.tamaño / memoria.getCapacidadTotal();
            Rectangle r = new Rectangle(cursorX, 0, ancho, MEMORIA_HEIGHT);
            if (b.isLibre()) {
                r.setFill(Color.WHITE);
                r.setStroke(Color.GRAY);
            } else {
                Color c = colorMap.getOrDefault(b.proceso.getId(), generarColorAleatorio());
                r.setFill(c);
                r.setStroke(Color.BLACK);
                // etiqueta con id y tamaño
                Label lbl = new Label(b.proceso.getId() + " (" + b.tamaño + ")");
                lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: black;");
                lbl.setLayoutX(cursorX + 4);
                lbl.setLayoutY(MEMORIA_HEIGHT / 2.0 - 8);
                memoriaPane.getChildren().add(lbl);
            }
            memoriaPane.getChildren().add(r);
            cursorX += ancho;
        }
    }

    // Mantener la tabla sincronizada con los procesos en memoria y en cola
    private void sincronizarTabla(List<Memoria.Block> bloques) {
        procesosObservable.clear();
        // Añadir procesos que están en memoria
        for (Memoria.Block b : bloques) {
            if (!b.isLibre()) procesosObservable.add(b.proceso);
        }
        // También podrías mostrar la cola si quieres; aquí solo mostramos los que están en memoria
    }

    private void actualizarLabels(int espacioLibre, int colaSize) {
        lblEspacioLibre.setText("Espacio libre: " + espacioLibre);
        lblCola.setText("En cola: " + colaSize);
    }

    private Color generarColorAleatorio() {
        // colores pastel para mejor legibilidad
        double r = 0.4 + rnd.nextDouble() * 0.6;
        double g = 0.4 + rnd.nextDouble() * 0.6;
        double b = 0.4 + rnd.nextDouble() * 0.6;
        return new Color(r, g, b, 1.0);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
