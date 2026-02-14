module org.example.usodelsistema {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mariadb.jdbc;


    opens org.example.usodelsistema to javafx.fxml;
    exports org.example.usodelsistema;
    exports org.example.usodelsistema.dao;
}