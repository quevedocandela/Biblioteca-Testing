package biblioteca.modelo;

public class Socio {
    private final int id;
    private final String dni;
    private final String nombre;
    private final String apellido;
    private final String correo;
    private final String telefono;
    private boolean activo;

    public Socio(int id, String dni, String nombre, String apellido, String correo, String telefono) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.activo = true; // HU01: se registra inicialmente como activo
    }

    public int getId() { return id; }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Socio N° " + id + " | DNI: " + dni + " | Nombre: " + nombre +
                " | Apellido: " + apellido + " | Correo: " + correo +
                " | Telefono: " + telefono + " | Estado: " + (activo ? "Activo" : "Inactivo");
    }
}