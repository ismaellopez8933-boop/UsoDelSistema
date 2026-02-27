package org.example.usodelsistema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.usodelsistema.HelloApplication;
import org.example.usodelsistema.model.GestorMemoria;
import org.example.usodelsistema.model.Proceso;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class HelloController {
    @FXML
    private Label welcomeText;
    
    // Cambiamos VBox por Pane para posicionamiento absoluto
    @FXML
    private Pane paneProcesos;
    
    @FXML
    private Label lblEstadoMemoria;

    private GestorMemoria gestor;
    private Random random = new Random();

    @FXML
    public void initialize() {
        gestor = GestorMemoria.getInstance();
        
        // Suscribirse a cambios en el gestor
        gestor.setOnCambioEstado(this::actualizarVista);
        
        // Dibujar estado inicial
        actualizarVista();
    }

    private void actualizarVista() {
        // Limpiar vista actual
        paneProcesos.getChildren().clear();
        
        List<Proceso> procesos = gestor.getProcesosEnMemoria();
        
        // Factor de escala: 400px altura total / 100 unidades memoria = 4px por unidad
        double factorEscala = 4.0; 
        double alturaTotalPane = 400.0;
        
        for (Proceso p : procesos) {
            double altura = p.getTamaño() * factorEscala;
            
            // Calcular posición Y (invertida para que 0 esté abajo)
            // Y = AlturaTotal - (Inicio * Factor) - AlturaProceso
            double yPos = alturaTotalPane - (p.getDireccionInicio() * factorEscala) - altura;
            
            // Crear rectángulo visual
            Rectangle rect = new Rectangle(100, altura);
            rect.setFill(generarColorAleatorio(p.getId())); // Color consistente por ID
            rect.setStroke(Color.WHITE);
            rect.setStrokeWidth(1);
            rect.setArcWidth(5);
            rect.setArcHeight(5);
            
            // Etiqueta con nombre y tamaño
            Label lbl = new Label(p.getId() + "\n(" + p.getTamaño() + ")");
            lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 2, 0, 0, 1); -fx-alignment: center;");
            
            // Contenedor para el proceso
            StackPane stack = new StackPane();
            stack.getChildren().addAll(rect, lbl);
            
            // Posicionar en el Pane
            stack.setLayoutX(0);
            stack.setLayoutY(yPos);
            stack.setPrefSize(100, altura);
            
            paneProcesos.getChildren().add(stack);
        }
        
        // Actualizar etiqueta de estado
        lblEstadoMemoria.setText("Disponible: " + gestor.getMemoriaDisponible() + " / " + gestor.getCapacidadTotal());
    }
    
    // Generar color basado en el nombre para que sea consistente (mismo proceso = mismo color)
    private Color generarColorAleatorio(String seed) {
        int hash = seed.hashCode();
        Random r = new Random(hash);
        
        float hue = r.nextFloat();
        float saturation = 0.5f + r.nextFloat() * 0.4f;
        float brightness = 0.6f + r.nextFloat() * 0.3f;
        
        return Color.hsb(hue * 360, saturation, brightness);
    }

    @FXML
    protected void onAddButtonClick() {
        abrirVentana("add-view.fxml", "Agregar Proceso", 400, 300);
    }

    @FXML
    protected void onDeleteButtonClick() {
        abrirVentana("delete-view.fxml", "Eliminar Proceso", 400, 200);
    }
    
    @FXML
    protected void onReportButtonClick() {
        abrirVentana("report-view.fxml", "Historial de Procesos", 650, 400);
    }

    private void abrirVentana(String fxml, String titulo, int width, int height) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
