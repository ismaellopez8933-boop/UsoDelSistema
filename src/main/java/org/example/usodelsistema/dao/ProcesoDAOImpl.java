package org.example.usodelsistema.dao;

import org.example.usodelsistema.model.Proceso;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ProcesoDAOImpl implements IProcesoDAO {

    @Override
    public void insertar(Proceso proceso) {
        String sql = "INSERT INTO proceso (nombre, tamaño, hora_llegada, status, tiempo_espera, tiempo_sistema) VALUES (?, ?, ?, ?, ?, ?)";

        // Usamos RETURN_GENERATED_KEYS para obtener el ID autoincrementable
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, proceso.getId()); // Nombre lógico (ej: "P1")
            pstmt.setDouble(2, proceso.getTamaño());
            
            LocalTime hora = proceso.getLlegada() != null ? proceso.getLlegada().toLocalTime() : LocalTime.now();
            pstmt.setTime(3, Time.valueOf(hora));
            
            pstmt.setInt(4, proceso.getEstadoInt());
            pstmt.setInt(5, proceso.getTiempoEspera());
            pstmt.setInt(6, proceso.getTiempoSistema());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Recuperamos el ID generado
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        proceso.setIdBaseDatos(idGenerado); // Guardamos el ID de BD en el objeto
                        System.out.println("Proceso insertado con ID de BD: " + idGenerado);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar proceso: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(String nombre) {
        // Seguimos eliminando por nombre lógico, ya que es lo que usas en la UI
        String sql = "DELETE FROM proceso WHERE nombre = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Proceso eliminado correctamente: " + nombre);
            } else {
                System.out.println("No se encontró el proceso con nombre: " + nombre);
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar proceso: " + e.getMessage());
        }
    }

    @Override
    public List<Proceso> obtenerTodos() {
        List<Proceso> procesos = new ArrayList<>();
        String sql = "SELECT * FROM proceso";

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Recuperamos el ID autoincrementable
                int idBd = rs.getInt("id_proceso");
                
                String nombre = rs.getString("nombre");
                double tamano = rs.getDouble("tamaño");
                Time horaLlegada = rs.getTime("hora_llegada");
                
                int status = rs.getInt("status");
                int tiempoEspera = rs.getInt("tiempo_espera");
                int tiempoSistema = rs.getInt("tiempo_sistema");

                // Reconstruimos el objeto Proceso
                Proceso p = new Proceso(nombre, (int) tamano);
                
                // Asignamos el ID de la base de datos
                p.setIdBaseDatos(idBd);
                
                if (horaLlegada != null) {
                    p.setLlegada(LocalDateTime.of(LocalDate.now(), horaLlegada.toLocalTime()));
                }
                
                p.setEstadoFromInt(status);
                p.setTiempoEspera(tiempoEspera);
                p.setTiempoSistema(tiempoSistema);
                
                procesos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener procesos: " + e.getMessage());
        }
        return procesos;
    }
}
