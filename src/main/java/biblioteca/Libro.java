package biblioteca;

public class Libro {
    private final int id;
    private final String isbn;
    private final String titulo;
    private final String autor;
    private final String editorial;
    private final int cantidad;
    private int disponibles;
    private boolean activo;

    public Libro(int id, String isbn, String titulo, String autor, String editorial, int cantidad) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.cantidad = cantidad;
        this.disponibles = cantidad;
        this.activo = true;
    }

    public int getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getEditorial() { return editorial; }
    public int getCantidad() { return cantidad; }
    public int getDisponibles() { return disponibles; }
    public void setDisponibles(int disponibles) { this.disponibles = disponibles; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Libro N° " + id + " | ISBN: " + isbn + " | \"" + titulo + "\" - " + autor
                + " | Editorial: " + editorial
                + " | Ejemplares: " + cantidad + " | Disponibles: " + disponibles
                + " | " + (disponibles > 0 ? "Disponible" : "No disponible")
                + " | Estado: " +  (activo ? "Activo" : "Inactivo");
    }
}