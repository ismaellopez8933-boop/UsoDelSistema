package org.example.usodelsistema.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.usodelsistema.dao.IProcesoDAO;
import org.example.usodelsistema.dao.ProcesoDAOImpl;
import org.example.usodelsistema.model.Proceso;

import java.time.format.DateTimeFormatter;

public class ReportController {

    @FXML
    private TableView<Proceso> tablaHistorial;
    @FXML
    private TableColumn<Proceso, String> colNombre;
    @FXML
    private TableColumn<Proceso, Integer> colTamano;
    @FXML
    private TableColumn<Proceso, String> colLlegada;
    @FXML
    private TableColumn<Proceso, String> colSalida;
    @FXML
    private TableColumn<Proceso, Number> colEspera;
    @FXML
    private TableColumn<Proceso, Number> colSistema;

    private IProcesoDAO procesoDAO;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        procesoDAO = new ProcesoDAOImpl();
        
        // Configurar columnas
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colTamano.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTamaño()).asObject());
        
        colLlegada.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLlegada() != null) {
                return new SimpleStringProperty(cellData.getValue().getLlegada().format(timeFormatter));
            }
            return new SimpleStringProperty("-");
        });
        
        colSalida.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSalida() != null) {
                return new SimpleStringProperty(cellData.getValue().getSalida().format(timeFormatter));
            }
            return new SimpleStringProperty("-");
        });
        
        colEspera.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getTiempoEspera()));
        colSistema.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getTiempoSistema()));

        // Cargar datos
        cargarDatos();
    }

    private void cargarDatos() {
        ObservableList<Proceso> listaHistorial = FXCollections.observableArrayList(procesoDAO.obtenerHistorial());
        tablaHistorial.setItems(listaHistorial);
    }

    @FXML
    protected void onCloseButtonClick() {
        Stage stage = (Stage) tablaHistorial.getScene().getWindow();
        stage.close();
    }
}
