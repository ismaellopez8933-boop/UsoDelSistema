package org.example.usodelsistema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.usodelsistema.HelloApplication;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("ola choy omelo chino");
    }

    @FXML
    protected void onAddButtonClick() {
        abrirVentana("add-view.fxml", "Agregar Proceso", 400, 300);
    }

    @FXML
    protected void onDeleteButtonClick() {
        abrirVentana("delete-view.fxml", "Eliminar Proceso", 400, 200);
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
