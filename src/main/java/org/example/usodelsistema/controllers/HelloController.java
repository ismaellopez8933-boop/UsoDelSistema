package org.example.usodelsistema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
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
    
    @FXML
    private Pane paneProcesos;
    
    @FXML
    private Label lblEstadoMemoria;

    @FXML
    private RadioButton rbPrimerAjuste;
    
    @FXML
    private RadioButton rbMejorAjuste;

    private GestorMemoria gestor;
    private Random random = new Random();

    @FXML
    public void initialize() {
        gestor = GestorMemoria.getInstance();
        
        // Configurar el algoritmo inicial
        javafx.scene.control.ToggleGroup toggleGroup = new javafx.scene.control.ToggleGroup();
        rbPrimerAjuste.setToggleGroup(toggleGroup);
        rbMejorAjuste.setToggleGroup(toggleGroup);
        
        // Suscribirse a cambios en el gestor
        gestor.setOnCambioEstado(this::actualizarVista);
        
        // Dibujar estado inicial
        actualizarVista();
    }
    
    @FXML
    protected void onAlgoritmoCambiado() {
        if (rbPrimerAjuste.isSelected()) {
            gestor.setAlgoritmo(GestorMemoria.PRIMER_AJUSTE);
        } else if (rbMejorAjuste.isSelected()) {
            gestor.setAlgoritmo(GestorMemoria.MEJOR_AJUSTE);
        }
    }

    private void actualizarVista() {
        paneProcesos.getChildren().clear();
        
        List<Proceso> procesos = gestor.getProcesosEnMemoria();
        
        double factorEscala = 4.0; 
        double alturaTotalPane = 400.0;
        
        for (Proceso p : procesos) {
            double altura = p.getTamaño() * factorEscala;
            
            double yPos = alturaTotalPane - (p.getDireccionInicio() * factorEscala) - altura;
            
            Rectangle rect = new Rectangle(100, altura);
            rect.setFill(generarColorAleatorio(p.getId()));
            rect.setStroke(Color.WHITE);
            rect.setStrokeWidth(1);
            rect.setArcWidth(5);
            rect.setArcHeight(5);
            
            Label lbl = new Label(p.getId() + "\n(" + p.getTamaño() + ")");
            lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 2, 0, 0, 1); -fx-alignment: center;");
            
            StackPane stack = new StackPane();
            stack.getChildren().addAll(rect, lbl);
            
            stack.setLayoutX(0);
            stack.setLayoutY(yPos);
            stack.setPrefSize(100, altura);
            
            paneProcesos.getChildren().add(stack);
        }
        
        lblEstadoMemoria.setText("Disponible: " + gestor.getMemoriaDisponible() + " / " + gestor.getCapacidadTotal());
    }
    
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
