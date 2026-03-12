package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public class UsuarioOviDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(UsuarioOvi usuarioOvi) {
        String sql = "INSERT INTO usuario_ovi (nombre, apellidos, direccion, provincia, ciudad, edad, genero, email, telefono, consentimiento_rgpd, fecha_consentimiento, id_sgovi, contrasena_hash, estado, id_tecnico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, usuarioOvi.getNombre(), usuarioOvi.getApellidos(), usuarioOvi.getDireccion(),
                usuarioOvi.getProvincia(), usuarioOvi.getCiudad(), usuarioOvi.getEdad(), usuarioOvi.getGenero(),
                usuarioOvi.getEmail(), usuarioOvi.getTelefono(), usuarioOvi.isConsentimientoRgpd(),
                usuarioOvi.getFechaConsentimiento() != null ? Date.valueOf(usuarioOvi.getFechaConsentimiento()) : null,
                usuarioOvi.getIdSgovi(), usuarioOvi.getContrasenaHash(), usuarioOvi.getEstado(), usuarioOvi.getIdTecnico());
    }

    public void update(UsuarioOvi usuarioOvi) {
        String sql = "UPDATE usuario_ovi SET nombre=?, apellidos=?, direccion=?, provincia=?, ciudad=?, edad=?, genero=?, email=?, telefono=?, consentimiento_rgpd=?, fecha_consentimiento=?, id_sgovi=?, contrasena_hash=?, estado=?, id_tecnico=? WHERE id_usuario=?";
        jdbcTemplate.update(sql, usuarioOvi.getNombre(), usuarioOvi.getApellidos(), usuarioOvi.getDireccion(),
                usuarioOvi.getProvincia(), usuarioOvi.getCiudad(), usuarioOvi.getEdad(), usuarioOvi.getGenero(),
                usuarioOvi.getEmail(), usuarioOvi.getTelefono(), usuarioOvi.isConsentimientoRgpd(),
                usuarioOvi.getFechaConsentimiento() != null ? Date.valueOf(usuarioOvi.getFechaConsentimiento()) : null,
                usuarioOvi.getIdSgovi(), usuarioOvi.getContrasenaHash(), usuarioOvi.getEstado(), usuarioOvi.getIdTecnico(), usuarioOvi.getIdUsuario());
    }

    public void delete(int idUsuario) {
        String sql = "DELETE FROM usuario_ovi WHERE id_usuario=?";
        jdbcTemplate.update(sql, idUsuario);
    }

    public UsuarioOvi get(int idUsuario) {
        String sql = "SELECT * FROM usuario_ovi WHERE id_usuario=?";
        return jdbcTemplate.queryForObject(sql, new UsusarioOviRowMapper(), idUsuario);
    }

    public List<UsuarioOvi> getAll() {
        String sql = "SELECT * FROM usuario_ovi";
        return jdbcTemplate.query(sql, new UsusarioOviRowMapper());
    }

    public List<UsuarioOvi> getByTecnico(int idTecnico) {
        String sql = "SELECT * FROM usuario_ovi WHERE id_tecnico=?";
        return jdbcTemplate.query(sql, new UsusarioOviRowMapper(), idTecnico);
    }

    public List<UsuarioOvi> getByEstado(String estado) {
        String sql = "SELECT * FROM usuario_ovi WHERE estado=?";
        return jdbcTemplate.query(sql, new UsusarioOviRowMapper(), estado);
    }
}
