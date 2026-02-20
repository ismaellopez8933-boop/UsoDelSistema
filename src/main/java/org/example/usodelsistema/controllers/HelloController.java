package org.example.usodelsistema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    
    @FXML
    private VBox vboxProcesos;
    
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
        vboxProcesos.getChildren().clear();
        
        List<Proceso> procesos = gestor.getProcesosEnMemoria();
        
        // Factor de escala: 400px altura total / 100 unidades memoria = 4px por unidad
        double factorEscala = 4.0; 
        
        for (Proceso p : procesos) {
            double altura = p.getTamaño() * factorEscala;
            
            // Crear rectángulo visual
            Rectangle rect = new Rectangle(100, altura);
            rect.setFill(generarColorAleatorio());
            rect.setStroke(Color.WHITE);
            rect.setStrokeWidth(1);
            rect.setArcWidth(5); // Bordes redondeados
            rect.setArcHeight(5);
            
            // Etiqueta con nombre y tamaño
            Label lbl = new Label(p.getId() + " (" + p.getTamaño() + ")");
            lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 2, 0, 0, 1);");
            
            // Contenedor para el proceso (rectángulo + etiqueta)
            StackPane stack = new StackPane();
            stack.getChildren().addAll(rect, lbl);
            
            // Agregar al VBox (se apilan de abajo hacia arriba si usamos alignment BOTTOM_CENTER en FXML)
            vboxProcesos.getChildren().add(0, stack); // Agregar al inicio para que se apilen visualmente correcto
        }
        
        // Actualizar etiqueta de estado
        lblEstadoMemoria.setText("Disponible: " + gestor.getMemoriaDisponible() + " / " + gestor.getCapacidadTotal());
    }
    
    private Color generarColorAleatorio() {
        // Generar colores pasteles/agradables
        float hue = random.nextFloat();
        // Saturation entre 0.5 y 0.9
        float saturation = 0.5f + random.nextFloat() * 0.4f;
        // Brightness entre 0.6 y 0.9
        float brightness = 0.6f + random.nextFloat() * 0.3f;
        
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
