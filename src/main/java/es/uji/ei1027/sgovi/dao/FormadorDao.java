package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Formador;
import java.util.List;

public interface FormadorDao {
    List<Formador> getAll();
    Formador get(int id);
    void add(Formador obj);
    void update(Formador obj);
    void delete(int id);
}
