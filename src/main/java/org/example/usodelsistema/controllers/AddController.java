package org.example.usodelsistema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.usodelsistema.model.GestorMemoria;
import org.example.usodelsistema.model.Proceso;

public class AddController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTamano;

    private GestorMemoria gestor;

    public AddController() {
        this.gestor = GestorMemoria.getInstance();
    }

    @FXML
    protected void onGuardarButtonClick() {
        String nombre = txtNombre.getText();
        String tamanoStr = txtTamano.getText();

        if (nombre.isEmpty() || tamanoStr.isEmpty()) {
            mostrarAlerta("Error", "Por favor complete todos los campos.");
            return;
        }

        try {
            int tamano = Integer.parseInt(tamanoStr);

            // Crear el nuevo proceso
            Proceso nuevoProceso = new Proceso(nombre, tamano);
            nuevoProceso.marcarLlegada(); // Marca la hora de llegada actual y estado EN_ESPERA

            // Usar el GestorMemoria en lugar del DAO directo
            boolean agregado = gestor.agregarProceso(nuevoProceso);

            if (agregado) {
                mostrarAlerta("Éxito", "Proceso agregado correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "El proceso es demasiado grande para el sistema.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El tamaño debe ser un número entero válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al guardar el proceso: " + e.getMessage());
        }
    }

    @FXML
    protected void onBackButtonClick() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
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
