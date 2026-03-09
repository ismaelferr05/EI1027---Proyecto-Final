package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class UsuarioOviDaoImpl implements UsuarioOviDao {

    @Autowired
    private JdbcTemplate jdbc;

    private static class UsuarioOviMapper implements RowMapper<UsuarioOvi> {
        @Override
        public UsuarioOvi mapRow(ResultSet rs, int n) throws SQLException {
            UsuarioOvi obj = new UsuarioOvi();
            obj.setIdUsuario(rs.getInt("id_usuario"));
            obj.setNombre(rs.getString("nombre"));
            obj.setApellidos(rs.getString("apellidos"));
            obj.setDireccion(rs.getString("direccion"));
            obj.setProvincia(rs.getString("provincia"));
            obj.setCiudad(rs.getString("ciudad"));
            obj.setEdad(rs.getInt("edad"));
            obj.setGenero(rs.getString("genero"));
            obj.setEmail(rs.getString("email"));
            obj.setTelefono(rs.getString("telefono"));
            obj.setConsentimientoRgpd(rs.getBoolean("consentimiento_rgpd"));
            var fc = rs.getDate("fecha_consentimiento");
            obj.setFechaConsentimiento(fc != null ? fc.toLocalDate() : null);
            obj.setIdSgovi(rs.getString("id_sgovi"));
            obj.setContrasenaHash(rs.getString("contrasena_hash"));
            obj.setEstado(rs.getString("estado"));
            var idTec = rs.getObject("id_tecnico", Integer.class);
            obj.setIdTecnico(idTec);
            return obj;
        }
    }

    @Override public List<UsuarioOvi> getAll() {
        return jdbc.query("SELECT * FROM usuario_ovi", new UsuarioOviMapper());
    }
    @Override public UsuarioOvi get(int id) {
        return jdbc.queryForObject("SELECT * FROM usuario_ovi WHERE id_usuario=?", new UsuarioOviMapper(), id);
    }
    @Override public void add(UsuarioOvi obj) {
        if (obj.getIdSgovi() == null || obj.getIdSgovi().isBlank()) {
            obj.setIdSgovi("SGOVI-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (obj.getContrasenaHash() == null || obj.getContrasenaHash().isBlank()) {
            obj.setContrasenaHash("");
        }
        jdbc.update("INSERT INTO usuario_ovi (nombre, apellidos, direccion, provincia, ciudad, edad, genero, email, telefono, consentimiento_rgpd, fecha_consentimiento, id_sgovi, contrasena_hash, estado, id_tecnico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                obj.getNombre(), obj.getApellidos(), obj.getDireccion(), obj.getProvincia(), obj.getCiudad(), obj.getEdad(), obj.getGenero(), obj.getEmail(), obj.getTelefono(),
                obj.isConsentimientoRgpd(), obj.getFechaConsentimiento() != null ? java.sql.Date.valueOf(obj.getFechaConsentimiento()) : null, obj.getIdSgovi(), obj.getContrasenaHash(), obj.getEstado() != null ? obj.getEstado() : "pendent_aprovacio", obj.getIdTecnico());
    }
    @Override public void update(UsuarioOvi obj) {
        if (obj.getContrasenaHash() != null && !obj.getContrasenaHash().isBlank()) {
            jdbc.update("UPDATE usuario_ovi SET nombre=?, apellidos=?, direccion=?, provincia=?, ciudad=?, edad=?, genero=?, email=?, telefono=?, consentimiento_rgpd=?, fecha_consentimiento=?, contrasena_hash=?, estado=?, id_tecnico=? WHERE id_usuario=?",
                    obj.getNombre(), obj.getApellidos(), obj.getDireccion(), obj.getProvincia(), obj.getCiudad(), obj.getEdad(), obj.getGenero(), obj.getEmail(), obj.getTelefono(),
                    obj.isConsentimientoRgpd(), obj.getFechaConsentimiento() != null ? java.sql.Date.valueOf(obj.getFechaConsentimiento()) : null, obj.getContrasenaHash(), obj.getEstado(), obj.getIdTecnico(), obj.getIdUsuario());
        } else {
            jdbc.update("UPDATE usuario_ovi SET nombre=?, apellidos=?, direccion=?, provincia=?, ciudad=?, edad=?, genero=?, email=?, telefono=?, consentimiento_rgpd=?, fecha_consentimiento=?, estado=?, id_tecnico=? WHERE id_usuario=?",
                    obj.getNombre(), obj.getApellidos(), obj.getDireccion(), obj.getProvincia(), obj.getCiudad(), obj.getEdad(), obj.getGenero(), obj.getEmail(), obj.getTelefono(),
                    obj.isConsentimientoRgpd(), obj.getFechaConsentimiento() != null ? java.sql.Date.valueOf(obj.getFechaConsentimiento()) : null, obj.getEstado(), obj.getIdTecnico(), obj.getIdUsuario());
        }
    }
    @Override public void delete(int id) {
        jdbc.update("DELETE FROM usuario_ovi WHERE id_usuario=?", id);
    }
}
