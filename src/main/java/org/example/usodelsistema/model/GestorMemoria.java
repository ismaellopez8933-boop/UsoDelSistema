package org.example.usodelsistema.model;

import org.example.usodelsistema.dao.IProcesoDAO;
import org.example.usodelsistema.dao.ProcesoDAOImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GestorMemoria {
    private static GestorMemoria instance;
    
    private final int CAPACIDAD_TOTAL = 100;
    private int memoriaDisponible;
    
    private List<Proceso> procesosEnMemoria;
    private Queue<Proceso> colaEspera;
    
    private IProcesoDAO procesoDAO;
    
    private Runnable onCambioEstado;

    private GestorMemoria() {
        this.memoriaDisponible = CAPACIDAD_TOTAL;
        this.procesosEnMemoria = new ArrayList<>();
        this.colaEspera = new LinkedList<>();
        this.procesoDAO = new ProcesoDAOImpl();
    }

    public static GestorMemoria getInstance() {
        if (instance == null) {
            instance = new GestorMemoria();
        }
        return instance;
    }
    
    public void setOnCambioEstado(Runnable callback) {
        this.onCambioEstado = callback;
    }
    
    private void notificarCambio() {
        if (onCambioEstado != null) {
            onCambioEstado.run();
        }
    }

    public boolean agregarProceso(Proceso p) {
        if (p.getTamaño() > CAPACIDAD_TOTAL) {
            System.out.println("El proceso es demasiado grande.");
            return false;
        }

        if (p.getTamaño() <= memoriaDisponible) {
            p.setEstado(Proceso.EN_MEMORIA);
            p.marcarAtencion();
            procesosEnMemoria.add(p);
            memoriaDisponible -= p.getTamaño();
        } else {
            p.setEstado(Proceso.EN_ESPERA);
            p.marcarLlegada();
            colaEspera.add(p);
        }

        procesoDAO.insertar(p);
        notificarCambio();
        return true;
    }

    public boolean eliminarProceso(String nombre) {
        Proceso aEliminar = null;
        
        // Buscar en memoria
        for (Proceso p : procesosEnMemoria) {
            if (p.getId().equals(nombre)) {
                aEliminar = p;
                break;
            }
        }

        if (aEliminar != null) {
            procesosEnMemoria.remove(aEliminar);
            memoriaDisponible += aEliminar.getTamaño();
            
            // Marcar salida y calcular tiempos finales
            aEliminar.marcarSalida();
            
            // Pasar el objeto completo al DAO para que guarde el historial
            procesoDAO.eliminar(aEliminar);
            
            verificarCola();
            notificarCambio();
            return true;
        } else {
            // Buscar en cola
            Proceso enCola = null;
            for(Proceso p : colaEspera) {
                if(p.getId().equals(nombre)) {
                    enCola = p;
                    break;
                }
            }
            if(enCola != null) {
                colaEspera.remove(enCola);
                
                // Marcar salida (aunque no entró a memoria, salió del sistema)
                enCola.marcarSalida();
                
                procesoDAO.eliminar(enCola);
                notificarCambio();
                return true;
            }
        }
        return false;
    }

    private void verificarCola() {
        if (colaEspera.isEmpty()) return;

        Proceso siguiente = colaEspera.peek();
        
        if (siguiente.getTamaño() <= memoriaDisponible) {
            colaEspera.poll();
            
            siguiente.setEstado(Proceso.EN_MEMORIA);
            siguiente.marcarAtencion();
            
            procesosEnMemoria.add(siguiente);
            memoriaDisponible -= siguiente.getTamaño();
            
            // Aquí podríamos actualizar el estado en BD si tuviéramos update
            // procesoDAO.actualizar(siguiente);
            
            verificarCola();
        }
    }

    public List<Proceso> getProcesosEnMemoria() { return procesosEnMemoria; }
    public Queue<Proceso> getColaEspera() { return colaEspera; }
    public int getMemoriaDisponible() { return memoriaDisponible; }
    public int getCapacidadTotal() { return CAPACIDAD_TOTAL; }
}
