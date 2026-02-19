package org.example.usodelsistema.dao;

import org.example.usodelsistema.model.Proceso;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessDAO {

    public void insertarProceso(Proceso proceso) {
        String sql = "INSERT INTO proceso (nombre, tamaño, hora_llegada) VALUES (?, ?, ?)";

        try (java.sql.Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proceso.getId());
            pstmt.setDouble(2, proceso.getTamaño());
            
            LocalTime hora = proceso.getLlegada() != null ? proceso.getLlegada().toLocalTime() : LocalTime.now();
            pstmt.setTime(3, Time.valueOf(hora));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar proceso: " + e.getMessage());
        }
    }

    public List<Proceso> obtenerTodos() {
        List<Proceso> procesos = new ArrayList<>();
        String sql = "SELECT * FROM proceso";

        try (java.sql.Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                double tamano = rs.getDouble("tamaño");
                Time horaLlegada = rs.getTime("hora_llegada");

                Proceso p = new Proceso(nombre, (int) tamano);
                
                if (horaLlegada != null) {
                    p.setLlegada(LocalDateTime.of(LocalDate.now(), horaLlegada.toLocalTime()));
                }
                
                procesos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener procesos: " + e.getMessage());
        }
        return procesos;
    }
    
    public void eliminarProceso(String nombre) {
        String sql = "DELETE FROM proceso WHERE nombre = ?";

        try (java.sql.Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar proceso: " + e.getMessage());
        }
    }
}
