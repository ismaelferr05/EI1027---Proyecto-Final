package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsusarioOviRowMapper implements RowMapper<UsuarioOvi> {
    @Override
    public UsuarioOvi mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsuarioOvi usuarioOvi = new UsuarioOvi();
        usuarioOvi.setIdUsuario(rs.getInt("id_usuario"));
        usuarioOvi.setNombre(rs.getString("nombre"));
        usuarioOvi.setApellidos(rs.getString("apellidos"));
        usuarioOvi.setDireccion(rs.getString("direccion"));
        usuarioOvi.setProvincia(rs.getString("provincia"));
        usuarioOvi.setCiudad(rs.getString("ciudad"));
        usuarioOvi.setEdad(rs.getInt("edad"));
        usuarioOvi.setGenero(rs.getString("genero"));
        usuarioOvi.setEmail(rs.getString("email"));
        usuarioOvi.setTelefono(rs.getString("telefono"));
        usuarioOvi.setConsentimientoRgpd(rs.getBoolean("consentimiento_rgpd"));
        if (rs.getDate("fecha_consentimiento") != null) {
            usuarioOvi.setFechaConsentimiento(rs.getDate("fecha_consentimiento").toLocalDate());
        }
        usuarioOvi.setIdSgovi(rs.getString("id_sgovi"));
        usuarioOvi.setContrasenaHash(rs.getString("contrasena_hash"));
        usuarioOvi.setEstado(rs.getString("estado"));
        usuarioOvi.setIdTecnico(rs.getInt("id_tecnico"));
        return usuarioOvi;
    }
}
