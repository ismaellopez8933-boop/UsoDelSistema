package org.example.usodelsistema.dao;

import org.example.usodelsistema.model.Proceso;
import java.util.List;

public interface IProcesoDAO {
    void insertar(Proceso proceso);
    void eliminar(Proceso proceso);
    List<Proceso> obtenerTodos(); // Procesos activos
    List<Proceso> obtenerHistorial(); // Procesos terminados
}
