package biblioteca.modelo;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String editorial;          // puede ser null (la columna admite NULL)
    private Integer anioPublicacion;   // Integer y no int: puede ser null
    private boolean activo;

    /** Para un libro NUEVO: todavía no tiene id (lo asigna la base al guardarlo). */
    public Libro(String titulo, String autor, String editorial, Integer anioPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.activo = true; // un libro nuevo siempre se registra como activo
    }

    /** Para un libro que ya existe: lo usa el DAO al leer una fila de la tabla. */
    public Libro(int id, String titulo, String autor, String editorial,
                 Integer anioPublicacion, boolean activo) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Integer getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(Integer anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Libro N° " + id + " | \"" + titulo + "\" - " + autor
                + " | Editorial: " + (editorial == null ? "-" : editorial)
                + " | Año: " + (anioPublicacion == null ? "-" : anioPublicacion)
                + " | Estado: " + (activo ? "Activo" : "Inactivo");
    }
}