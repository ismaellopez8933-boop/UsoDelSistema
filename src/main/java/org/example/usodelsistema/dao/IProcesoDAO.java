package org.example.usodelsistema.dao;

import org.example.usodelsistema.model.Proceso;
import java.util.List;

public interface IProcesoDAO {
    void insertar(Proceso proceso);
    void eliminar(String nombre);
    List<Proceso> obtenerTodos();
}
