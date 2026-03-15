package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FormadorDaoImpl implements FormadorDao {

    @Autowired
    private JdbcTemplate jdbc;

    private static class FormadorMapper implements RowMapper<Formador> {
        @Override
        public Formador mapRow(ResultSet rs, int n) throws SQLException {
            Formador obj = new Formador();
            obj.setIdFormador(rs.getInt("id_formador"));
            obj.setNombre(rs.getString("nombre"));
            obj.setApellidos(rs.getString("apellidos"));
            obj.setOcupacion(rs.getString("ocupacion"));
            obj.setTelefono(rs.getString("telefono"));
            obj.setDireccion(rs.getString("direccion"));
            obj.setEmail(rs.getString("email"));
            return obj;
        }
    }

    @Override public List<Formador> getAll() {
        return jdbc.query("SELECT * FROM formador", new FormadorMapper());
    }
    @Override public Formador get(int id) {
        return jdbc.queryForObject("SELECT * FROM formador WHERE id_formador=?", new FormadorMapper(), id);
    }
    @Override public void add(Formador obj) {
        jdbc.update("INSERT INTO formador (nombre, apellidos, ocupacion, telefono, direccion, email) VALUES (?, ?, ?, ?, ?, ?)", obj.getNombre(), obj.getApellidos(), obj.getOcupacion(), obj.getTelefono(), obj.getDireccion(), obj.getEmail());
    }
    @Override public void update(Formador obj) {
        jdbc.update("UPDATE formador SET nombre=?, apellidos=?, ocupacion=?, telefono=?, direccion=?, email=? WHERE id_formador=?", obj.getNombre(), obj.getApellidos(), obj.getOcupacion(), obj.getTelefono(), obj.getDireccion(), obj.getEmail(), obj.getIdFormador());
    }
    @Override public void delete(int id) {
        jdbc.update("DELETE FROM formador WHERE id_formador=?", id);
    }
}
