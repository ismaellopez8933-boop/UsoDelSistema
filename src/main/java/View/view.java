<?xml version="1.0" encoding="UTF-8"?>

        <?import javafx.scene.layout.*?>
        <?import javafx.scene.control.*?>
        <?import javafx.scene.text.*?>

<BorderPane xmlns:fx="http://javafx.com/fxml"
fx:controller="Controller.MemoriaController">

    <!-- Parte superior: controles -->
    <top>
        <HBox spacing="10" padding="10">
            <Label text="ID:"/>
            <TextField fx:id="txtId" prefWidth="60"/>
            <Label text="Tamaño:"/>
            <TextField fx:id="txtTam" prefWidth="60"/>
            <Button fx:id="btnAgregar" text="Agregar"/>
            <Button fx:id="btnEliminar" text="Eliminar"/>
            <Button fx:id="btnCompactar" text="Compactar"/>
        </HBox>
    </top>

    <!-- Centro: representación gráfica de la memoria -->
    <center>
        <Pane fx:id="memoriaPane" prefHeight="100" prefWidth="600"
style="-fx-border-color: black; -fx-background-color: lightgray;"/>
    </center>

    <!-- Abajo: tabla y etiquetas -->
    <bottom>
        <VBox spacing="10" padding="10">
            <TableView fx:id="tabla" prefHeight="200">
                <columns>
                    <TableColumn fx:id="colId" text="ID" prefWidth="100"/>
                    <TableColumn fx:id="colTam" text="Tamaño" prefWidth="100"/>
                </columns>
            </TableView>
            <HBox spacing="20">
                <Label fx:id="lblEspacioLibre" text="Espacio libre: 100"/>
                <Label fx:id="lblCola" text="En cola: 0"/>
            </HBox>
        </VBox>
    </bottom>
</BorderPane>