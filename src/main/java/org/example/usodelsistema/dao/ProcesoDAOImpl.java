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

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, proceso.getId());
            pstmt.setDouble(2, proceso.getTamaño());
            
            LocalTime hora = proceso.getLlegada() != null ? proceso.getLlegada().toLocalTime() : LocalTime.now();
            pstmt.setTime(3, Time.valueOf(hora));
            
            pstmt.setInt(4, proceso.getEstadoInt());
            pstmt.setLong(5, proceso.getTiempoEspera());
            pstmt.setLong(6, proceso.getTiempoSistema());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        proceso.setIdBaseDatos(idGenerado);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar proceso: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(Proceso proceso) {
        insertarHistorial(proceso);
        
        String sql = "DELETE FROM proceso WHERE nombre = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proceso.getId());
            pstmt.executeUpdate();
            System.out.println("Proceso movido al historial y eliminado de activos: " + proceso.getId());

        } catch (SQLException e) {
            System.err.println("Error al eliminar proceso: " + e.getMessage());
        }
    }
    
    private void insertarHistorial(Proceso p) {
        String sql = "INSERT INTO historial_procesos (nombre, tamaño, hora_llegada, hora_salida, tiempo_espera, tiempo_sistema) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, p.getId());
            pstmt.setDouble(2, p.getTamaño());
            
            LocalTime llegada = p.getLlegada() != null ? p.getLlegada().toLocalTime() : LocalTime.now();
            pstmt.setTime(3, Time.valueOf(llegada));
            
            LocalTime salida = p.getSalida() != null ? p.getSalida().toLocalTime() : LocalTime.now();
            pstmt.setTime(4, Time.valueOf(salida));
            
            pstmt.setLong(5, p.getTiempoEspera());
            pstmt.setLong(6, p.getTiempoSistema());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
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
                int idBd = rs.getInt("id_proceso");
                String nombre = rs.getString("nombre");
                double tamano = rs.getDouble("tamaño");
                Time horaLlegada = rs.getTime("hora_llegada");
                
                int status = rs.getInt("status");
                int tiempoEspera = rs.getInt("tiempo_espera");
                int tiempoSistema = rs.getInt("tiempo_sistema");

                Proceso p = new Proceso(nombre, (int) tamano);
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

    @Override
    public List<Proceso> obtenerHistorial() {
        List<Proceso> historial = new ArrayList<>();
        String sql = "SELECT * FROM historial_procesos ORDER BY id_historial DESC"; // Más recientes primero

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                double tamano = rs.getDouble("tamaño");
                Time horaLlegada = rs.getTime("hora_llegada");
                Time horaSalida = rs.getTime("hora_salida");
                int tiempoEspera = rs.getInt("tiempo_espera");
                int tiempoSistema = rs.getInt("tiempo_sistema");

                Proceso p = new Proceso(nombre, (int) tamano);
                p.setEstado(Proceso.FINALIZADO);
                
                if (horaLlegada != null) {
                    p.setLlegada(LocalDateTime.of(LocalDate.now(), horaLlegada.toLocalTime()));
                }
                if (horaSalida != null) {
                    p.setSalida(LocalDateTime.of(LocalDate.now(), horaSalida.toLocalTime()));
                }
                
                p.setTiempoEspera(tiempoEspera);
                p.setTiempoSistema(tiempoSistema);
                
                historial.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
        }
        return historial;
    }
}
