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
    
    // Mapa de memoria: true = ocupado, false = libre
    private boolean[] mapaMemoria;
    
    private List<Proceso> procesosEnMemoria;
    private Queue<Proceso> colaEspera;
    
    private IProcesoDAO procesoDAO;
    
    private Runnable onCambioEstado;

    // Tipos de algoritmos
    public static final int PRIMER_AJUSTE = 1;
    public static final int MEJOR_AJUSTE = 2;
    private int algoritmoActual = PRIMER_AJUSTE; // Por defecto

    private GestorMemoria() {
        this.memoriaDisponible = CAPACIDAD_TOTAL;
        this.mapaMemoria = new boolean[CAPACIDAD_TOTAL]; // Inicialmente todo false (libre)
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
    
    public void setAlgoritmo(int algoritmo) {
        this.algoritmoActual = algoritmo;
        System.out.println("Algoritmo cambiado a: " + (algoritmo == PRIMER_AJUSTE ? "Primer Ajuste" : "Mejor Ajuste"));
    }
    
    public void setOnCambioEstado(Runnable callback) {
        this.onCambioEstado = callback;
    }
    
    private void notificarCambio() {
        if (onCambioEstado != null) {
            onCambioEstado.run();
        }
    }

    // Método para buscar espacio según el algoritmo seleccionado
    private int buscarEspacioLibre(int tamano) {
        if (algoritmoActual == PRIMER_AJUSTE) {
            return buscarPrimerAjuste(tamano);
        } else {
            return buscarMejorAjuste(tamano);
        }
    }

    // Primer Ajuste: Devuelve el primer hueco donde quepa
    private int buscarPrimerAjuste(int tamano) {
        int contador = 0;
        int inicio = -1;
        
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (!mapaMemoria[i]) {
                if (contador == 0) inicio = i;
                contador++;
                if (contador == tamano) return inicio; // Encontró el primer espacio suficiente
            } else {
                contador = 0;
                inicio = -1;
            }
        }
        return -1; // No hay espacio contiguo suficiente
    }
    
    // Mejor Ajuste: Devuelve el hueco MÁS GRANDE que exista
    // (Según tu descripción: "va a meter el proceso en el hueco con mayor espacio que exista")
    // Nota: Tradicionalmente "Best Fit" es el hueco más pequeño donde quepa, 
    // pero implementaré exactamente lo que pediste: "el hueco con mayor espacio".
    // Esto en literatura se conoce como "Worst Fit" (Peor Ajuste).
    private int buscarMejorAjuste(int tamano) {
        int mejorInicio = -1;
        int maxHuecoEncontrado = -1;
        
        int contadorActual = 0;
        int inicioActual = -1;
        
        for (int i = 0; i <= CAPACIDAD_TOTAL; i++) {
            // Evaluamos hasta <= CAPACIDAD_TOTAL para poder cerrar el último hueco si está al final
            if (i < CAPACIDAD_TOTAL && !mapaMemoria[i]) {
                if (contadorActual == 0) inicioActual = i;
                contadorActual++;
            } else {
                // Se acabó un hueco (o llegamos al final de la memoria)
                if (contadorActual > 0) {
                    // Si el hueco actual es lo suficientemente grande Y es mayor que el hueco máximo que hemos visto
                    if (contadorActual >= tamano && contadorActual > maxHuecoEncontrado) {
                        maxHuecoEncontrado = contadorActual;
                        mejorInicio = inicioActual;
                    }
                }
                contadorActual = 0;
                inicioActual = -1;
            }
        }
        
        return mejorInicio;
    }
    
    private void ocuparMemoria(int inicio, int tamano) {
        for (int i = inicio; i < inicio + tamano; i++) {
            mapaMemoria[i] = true;
        }
        memoriaDisponible -= tamano;
    }
    
    private void liberarMemoria(int inicio, int tamano) {
        for (int i = inicio; i < inicio + tamano; i++) {
            mapaMemoria[i] = false;
        }
        memoriaDisponible += tamano;
    }

    public boolean agregarProceso(Proceso p) {
        if (p.getTamaño() > CAPACIDAD_TOTAL) {
            System.out.println("El proceso es demasiado grande.");
            return false;
        }

        // Buscar si hay espacio contiguo disponible usando el algoritmo actual
        int direccionInicio = buscarEspacioLibre(p.getTamaño());

        if (direccionInicio != -1) {
            // Asignar a memoria
            p.setEstado(Proceso.EN_MEMORIA);
            p.setDireccionInicio(direccionInicio);
            p.marcarAtencion();
            
            ocuparMemoria(direccionInicio, p.getTamaño());
            procesosEnMemoria.add(p);
            
            System.out.println("Proceso " + p.getId() + " asignado en dir " + direccionInicio + " con " + 
                               (algoritmoActual == PRIMER_AJUSTE ? "Primer Ajuste" : "Mejor Ajuste"));
        } else {
            // A la cola
            p.setEstado(Proceso.EN_ESPERA);
            p.marcarLlegada();
            colaEspera.add(p);
            System.out.println("Proceso " + p.getId() + " a cola de espera.");
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
            
            // Liberar el espacio específico que ocupaba
            liberarMemoria(aEliminar.getDireccionInicio(), aEliminar.getTamaño());
            
            aEliminar.marcarSalida();
            procesoDAO.eliminar(aEliminar);
            
            System.out.println("Proceso " + nombre + " eliminado. Memoria liberada en dir " + aEliminar.getDireccionInicio());
            
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
        int direccionInicio = buscarEspacioLibre(siguiente.getTamaño());
        
        if (direccionInicio != -1) {
            colaEspera.poll(); // Sacar de la cola
            
            siguiente.setEstado(Proceso.EN_MEMORIA);
            siguiente.setDireccionInicio(direccionInicio);
            siguiente.marcarAtencion();
            
            ocuparMemoria(direccionInicio, siguiente.getTamaño());
            procesosEnMemoria.add(siguiente);
            
            System.out.println("Proceso " + siguiente.getId() + " movido de cola a memoria en dir " + direccionInicio);

            verificarCola();
        }
    }

    public List<Proceso> getProcesosEnMemoria() { return procesosEnMemoria; }
    public Queue<Proceso> getColaEspera() { return colaEspera; }
    public int getMemoriaDisponible() { return memoriaDisponible; }
    public int getCapacidadTotal() { return CAPACIDAD_TOTAL; }
}
