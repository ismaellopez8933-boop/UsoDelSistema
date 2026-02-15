package org.example.usodelsistema.dao;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    // Instancia única de la conexión (Singleton)
    private static java.sql.Connection connection;

    // Datos de conexión
    // TODO: Reemplaza con el nombre real de tu base de datos
    private static final String URL = "jdbc:mariadb://localhost:3306/sistema"; //Nombre de la base de datos
    private static final String USER = "adminSistema"; //usuario
    private static final String PASSWORD = "123"; // Contraseña

    // Constructor privado para evitar instanciación externa
    private Connection() {}

    public static java.sql.Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar el driver explícitamente (opcional en versiones nuevas de Java, pero buena práctica)
                Class.forName("org.mariadb.jdbc.Driver");
                
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión establecida con éxito a: " + URL);
                
            } catch (ClassNotFoundException e) {
                System.err.println("Error: No se encontró el driver de MariaDB.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    // Método para cerrar la conexión explícitamente si es necesario
    public static void cerrarConexion() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
