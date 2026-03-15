package es.uji.ei1027.sgovi.model;

public class Formador {
    private int idFormador;
    private String nombre;
    private String apellidos;
    private String ocupacion;
    private String telefono;
    private String direccion;
    private String email;

    public Formador() {}

    public int getIdFormador() { return idFormador; }
    public void setIdFormador(int idFormador) { this.idFormador = idFormador; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
