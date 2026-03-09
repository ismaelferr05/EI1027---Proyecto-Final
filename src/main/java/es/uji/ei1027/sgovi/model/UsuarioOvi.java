package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class UsuarioOvi {
    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String direccion;
    private String provincia;
    private String ciudad;
    private int edad;
    private String genero;
    private String email;
    private String telefono;
    private boolean consentimientoRgpd;
    private LocalDate fechaConsentimiento;
    private String idSgovi;
    private String contrasenaHash;
    private String estado; // pendent_aprovacio, actiu, inactiu
    private Integer idTecnico;

    public UsuarioOvi() {}

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public boolean isConsentimientoRgpd() { return consentimientoRgpd; }
    public void setConsentimientoRgpd(boolean consentimientoRgpd) { this.consentimientoRgpd = consentimientoRgpd; }
    public LocalDate getFechaConsentimiento() { return fechaConsentimiento; }
    public void setFechaConsentimiento(LocalDate fechaConsentimiento) { this.fechaConsentimiento = fechaConsentimiento; }
    public String getIdSgovi() { return idSgovi; }
    public void setIdSgovi(String idSgovi) { this.idSgovi = idSgovi; }
    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getIdTecnico() { return idTecnico; }
    public void setIdTecnico(Integer idTecnico) { this.idTecnico = idTecnico; }
}
