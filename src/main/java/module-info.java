module org.example.usodelsistema {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.usodelsistema to javafx.fxml;
    exports org.example.usodelsistema;
}