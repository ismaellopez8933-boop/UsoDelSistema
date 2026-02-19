package Model;

import java.time.LocalDateTime;
import java.util.*;

public class Memoria {
    private final int capacidadTotal;
    private final List<Block> bloques;
    private final Queue<Proceso> colaEspera;
    private final List<MemoriaListener> listeners;

    public Memoria(int capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
        this.bloques = new ArrayList<>();
        this.colaEspera = new LinkedList<>();
        this.listeners = new ArrayList<>();
        // inicio: la memoria libre
        bloques.add(new Block(0, capacidadTotal, null));
    }

    public static class Block {
        public int inicio;
        public int tamaño;
        public Proceso proceso; // null = libre

        public Block(int inicio, int tamaño, Proceso proceso) {
            this.inicio = inicio;
            this.tamaño = tamaño;
            this.proceso = proceso;
        }

        public boolean isLibre() { return proceso == null; }
    }

    public interface MemoriaListener {
        void memoriaActualizada(List<Block> bloques, int espacioLibre, int colaSize);
    }

    public void addListener(MemoriaListener l) { listeners.add(l); }
    public void removeListener(MemoriaListener l) { listeners.remove(l); }

    private void notificar() {
        List<Block> copia = new ArrayList<>();
        for (Block b : bloques) copia.add(new Block(b.inicio, b.tamaño, b.proceso));
        int libre = espacioLibre();
        int cola = colaEspera.size();
        for (MemoriaListener l : listeners) l.memoriaActualizada(copia, libre, cola);
    }

    public synchronized boolean agregarProceso(Proceso p) {

        if (p.getLlegada() == null) p.setLlegada(LocalDateTime.now());

        for (int i = 0; i < bloques.size(); i++) {
            Block b = bloques.get(i);
            if (b.isLibre() && b.tamaño >= p.getTamaño()) {

                p.marcarAtencion();
                if (b.tamaño == p.getTamaño()) {
                    b.proceso = p;
                } else {

                    // dividir: ocupado + resto libre

                    Block ocupado = new Block(b.inicio, p.getTamaño(), p);
                    Block resto = new Block(b.inicio + p.getTamaño(), b.tamaño - p.getTamaño(), null);
                    bloques.set(i, ocupado);
                    bloques.add(i + 1, resto);
                }
                notificar();
                return true;
            }
        }
        //  la cola de espera
        colaEspera.offer(p);
        notificar();
        return false;
    }

    public synchronized boolean eliminarProceso(String procesoId) {
        boolean encontrado = false;
        for (int i = 0; i < bloques.size(); i++) {
            Block b = bloques.get(i);
            if (!b.isLibre() && b.proceso.getId().equals(procesoId)) {
                b.proceso.marcarSalida();
                b.proceso = null; // liberar
                encontrado = true;
                // intentar mergear con vecinos libres
                mergearLibres(i);
                break;
            }
        }
        if (encontrado) {
            notificar();
            revisarCola();
        }
        return encontrado;
    }

    // Fusiona bloques libres alrededor del índice dado
    private void mergearLibres(int index) {
        // merge con anterior
        if (index > 0) {
            Block prev = bloques.get(index - 1);
            Block cur = bloques.get(index);
            if (prev.isLibre() && cur.isLibre()) {
                prev.tamaño += cur.tamaño;
                bloques.remove(index);
                index = index - 1;
            }
        }
        // merge con siguiente
        if (index < bloques.size() - 1) {
            Block cur = bloques.get(index);
            Block next = bloques.get(index + 1);
            if (cur.isLibre() && next.isLibre()) {
                cur.tamaño += next.tamaño;
                bloques.remove(index + 1);
            }
        }
    }

    // Revisa la cola y trata de insertar procesos en espera (First Fit)
    private void revisarCola() {
        if (colaEspera.isEmpty()) return;
        Iterator<Proceso> it = colaEspera.iterator();
        while (it.hasNext()) {
            Proceso p = it.next();
            boolean insertado = false;
            for (int i = 0; i < bloques.size(); i++) {
                Block b = bloques.get(i);
                if (b.isLibre() && b.tamaño >= p.getTamaño()) {
                    p.marcarAtencion();
                    if (b.tamaño == p.getTamaño()) {
                        b.proceso = p;
                    } else {
                        Block ocupado = new Block(b.inicio, p.getTamaño(), p);
                        Block resto = new Block(b.inicio + p.getTamaño(), b.tamaño - p.getTamaño(), null);
                        bloques.set(i, ocupado);
                        bloques.add(i + 1, resto);
                    }
                    it.remove();
                    insertado = true;
                    break;
                }
            }
            if (insertado) notificar();
        }
    }

    // Espacio libre total
    public synchronized int espacioLibre() {
        int total = 0;
        for (Block b : bloques) if (b.isLibre()) total += b.tamaño;
        return total;
    }

    public synchronized List<Block> getBloquesSnapshot() {
        List<Block> copia = new ArrayList<>();
        for (Block b : bloques) copia.add(new Block(b.inicio, b.tamaño, b.proceso));
        return copia;
    }

    public synchronized int getCapacidadTotal() { return capacidadTotal; }
    public synchronized int getColaSize() { return colaEspera.size(); }

    public synchronized void compactar() {
        List<Block> nuevos = new ArrayList<>();
        int cursor = 0;
        for (Block b : bloques) {
            if (!b.isLibre()) {
                nuevos.add(new Block(cursor, b.tamaño, b.proceso));
                cursor += b.tamaño;
            }
        }
        if (cursor < capacidadTotal) {
            nuevos.add(new Block(cursor, capacidadTotal - cursor, null));
        }
        bloques.clear();
        bloques.addAll(nuevos);
        notificar();
        revisarCola();
    }
}