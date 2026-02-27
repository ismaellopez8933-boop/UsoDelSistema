package org.example.usodelsistema.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Proceso {
    // Estados de los procesos
    public static final String EN_ESPERA = "EN_ESPERA";
    public static final String EN_MEMORIA = "EN_MEMORIA";
    public static final String FINALIZADO = "FINALIZADO";

    // ID interno de la Base de Datos (Auto-incrementable)
    private int idBaseDatos;
    
    private String id;               // Nombre lógico: "P1", "P2", "Firefox", etc.
    private int tamaño;
    private LocalDateTime llegada;
    private LocalDateTime atencion;
    private LocalDateTime salida;
    private String estado;
    
    // Posición en memoria (0 a 99)
    private int direccionInicio = -1; 
    
    // Estos campos se calcularán automáticamente
    private long tiempoEspera;
    private long tiempoSistema;

    public Proceso(String id, int tamaño) {
        this.id = id;
        this.tamaño = tamaño;
        this.estado = EN_ESPERA;
        this.tiempoEspera = 0;
        this.tiempoSistema = 0;
    }

    // Getter y Setter para el ID de Base de Datos
    public int getIdBaseDatos() { return idBaseDatos; }
    public void setIdBaseDatos(int idBaseDatos) { this.idBaseDatos = idBaseDatos; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getTamaño() { return tamaño; }
    public void setTamaño(int tamaño) { this.tamaño = tamaño; }

    public LocalDateTime getLlegada() { return llegada; }
    public void setLlegada(LocalDateTime llegada) { this.llegada = llegada; }

    public LocalDateTime getAtencion() { return atencion; }
    public void setAtencion(LocalDateTime atencion) { this.atencion = atencion; }

    public LocalDateTime getSalida() { return salida; }
    public void setSalida(LocalDateTime salida) { this.salida = salida; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public int getDireccionInicio() { return direccionInicio; }
    public void setDireccionInicio(int direccionInicio) { this.direccionInicio = direccionInicio; }

    public long getTiempoEspera() { return tiempoEspera; }
    public void setTiempoEspera(long tiempoEspera) { this.tiempoEspera = tiempoEspera; }

    public long getTiempoSistema() { return tiempoSistema; }
    public void setTiempoSistema(long tiempoSistema) { this.tiempoSistema = tiempoSistema; }

    public void marcarLlegada() {
        this.llegada = LocalDateTime.now();
        this.estado = EN_ESPERA;
    }

    public void marcarAtencion() {
        this.atencion = LocalDateTime.now();
        this.estado = EN_MEMORIA;
        
        // Calcular tiempo de espera: desde que llegó hasta que fue atendido
        if (llegada != null) {
            this.tiempoEspera = Duration.between(llegada, atencion).toSeconds();
        }
    }

    public void marcarSalida() {
        this.salida = LocalDateTime.now();
        this.estado = FINALIZADO;
        
        // Calcular tiempo en sistema: desde que llegó hasta que salió
        if (llegada != null) {
            this.tiempoSistema = Duration.between(llegada, salida).toSeconds();
        }
    }
    
    public int getEstadoInt() {
        switch (estado) {
            case EN_MEMORIA: return 1;
            case FINALIZADO: return 2;
            default: return 0;
        }
    }
    
    public void setEstadoFromInt(int estadoInt) {
        switch (estadoInt) {
            case 1: this.estado = EN_MEMORIA; break;
            case 2: this.estado = FINALIZADO; break;
            default: this.estado = EN_ESPERA; break;
        }
    }

    @Override
    public String toString() {
        return id + " (ID_BD:" + idBaseDatos + ") tamaño=" + tamaño + " estado=" + estado;
    }
}
