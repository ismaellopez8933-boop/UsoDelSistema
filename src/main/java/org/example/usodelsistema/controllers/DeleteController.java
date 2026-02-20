package org.example.usodelsistema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.usodelsistema.model.GestorMemoria;

public class DeleteController {

    @FXML
    private TextField txtNombreEliminar;

    private GestorMemoria gestor;

    public DeleteController() {
        this.gestor = GestorMemoria.getInstance();
    }

    @FXML
    protected void onEliminarButtonClick() {
        String nombre = txtNombreEliminar.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese el nombre del proceso a eliminar.");
            return;
        }

        try {
            // Usar el GestorMemoria en lugar del DAO directo
            boolean eliminado = gestor.eliminarProceso(nombre);
            
            if (eliminado) {
                mostrarAlerta("Información", "Proceso eliminado correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta("Información", "No se encontró el proceso en memoria o cola.");
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al intentar eliminar: " + e.getMessage());
        }
    }

    @FXML
    protected void onBackButtonClick() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombreEliminar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
