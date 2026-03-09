package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuarioOvi;
import java.util.List;

public interface UsuarioOviDao {
    List<UsuarioOvi> getAll();
    UsuarioOvi get(int id);
    void add(UsuarioOvi obj);
    void update(UsuarioOvi obj);
    void delete(int id);
}
