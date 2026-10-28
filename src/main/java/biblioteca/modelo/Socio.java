package biblioteca.modelo;

public class Socio {
    private int id;
    private int numeroSocio;
    private String dni;
    private String nombre;
    private String apellido;
    private boolean activo;

    /** Para un socio NUEVO: todavía no tiene id (lo asigna la base al guardarlo). */
    public Socio(int numeroSocio, String dni, String nombre, String apellido) {
        this.numeroSocio = numeroSocio;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = true; // un socio nuevo siempre se registra como activo
    }

    /** Para un socio que ya existe: lo usa el DAO al leer una fila de la tabla. */
    public Socio(int id, int numeroSocio, String dni, String nombre, String apellido, boolean activo) {
        this.id = id;
        this.numeroSocio = numeroSocio;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumeroSocio() { return numeroSocio; }
    public void setNumeroSocio(int numeroSocio) { this.numeroSocio = numeroSocio; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Socio N° " + numeroSocio + " | DNI: " + dni + " | Nombre: " + nombre +
                " | Apellido: " + apellido + " | Estado: " + (activo ? "Activo" : "Inactivo");
    }
}