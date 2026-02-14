package org.example.usodelsistema.model;

import java.time.LocalDateTime;

public class Proceso {
    // Estados de los procesos
    public static final String EN_ESPERA = "EN_ESPERA";
    public static final String EN_MEMORIA = "EN_MEMORIA";
    public static final String FINALIZADO = "FINALIZADO";

    private String id;               // "P1", "P2", ...
    private int tamaño;              // unidades que ocupan en memoria (ej. 22)
    private LocalDateTime llegada;
    private LocalDateTime atencion;
    private LocalDateTime salida;
    private String estado;           // lo de arriba

    public Proceso(String id, int tamaño) {
        this.id = id;
        this.tamaño = tamaño;
        this.estado = EN_ESPERA;
    }

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

    // Métodos de conveniencia para marcar tiempos y estado
    public void marcarLlegada() {
        this.llegada = LocalDateTime.now();
        this.estado = EN_ESPERA;
    }

    public void marcarAtencion() {
        this.atencion = LocalDateTime.now();
        this.estado = EN_MEMORIA;
    }

    public void marcarSalida() {
        this.salida = LocalDateTime.now();
        this.estado = FINALIZADO;
    }

    @Override
    public String toString() {
        return id + " tamaño=" + tamaño + " estado=" + estado;
    }
}

