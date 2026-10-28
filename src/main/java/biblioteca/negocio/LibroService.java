package biblioteca.negocio;

import biblioteca.datos.LibroDAO;
import biblioteca.modelo.Libro;

import java.time.LocalDate;
import java.util.List;

public class LibroService {

    private final LibroDAO libroDAO = new LibroDAO();

    /** RF-03: registra un ejemplar. Dos ejemplares del mismo título son dos libros distintos. */
    public Libro registrar(String titulo, String autor, String editorial, Integer anioPublicacion) {
        String tituloLimpio = validarObligatorio(titulo, "El título", 150);
        String autorLimpio = validarObligatorio(autor, "El autor", 100);
        String editorialLimpia = validarOpcional(editorial, "La editorial", 100);
        validarAnio(anioPublicacion);

        Libro libro = new Libro(tituloLimpio, autorLimpio, editorialLimpia, anioPublicacion);
        libroDAO.insertar(libro);
        return libro;
    }

    /** RF-03: modifica los datos de un libro existente. */
    public Libro modificar(int id, String titulo, String autor, String editorial, Integer anioPublicacion) {
        Libro libro = buscarPorId(id);
        String tituloLimpio = validarObligatorio(titulo, "El título", 150);
        String autorLimpio = validarObligatorio(autor, "El autor", 100);
        String editorialLimpia = validarOpcional(editorial, "La editorial", 100);
        validarAnio(anioPublicacion);

        libro.setTitulo(tituloLimpio);
        libro.setAutor(autorLimpio);
        libro.setEditorial(editorialLimpia);
        libro.setAnioPublicacion(anioPublicacion);
        libroDAO.actualizar(libro);
        return libro;
    }

    /** Devuelve el libro con ese N° o falla si no existe. */
    public Libro buscarPorId(int id) {
        Libro libro = libroDAO.buscarPorId(id);
        if (libro == null) {
            throw new IllegalArgumentException("No existe un libro con el N° " + id + ".");
        }
        return libro;
    }

    /** RF-04: busca por título o autor (coincidencia parcial). */
    public List<Libro> buscarPorTituloOAutor(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un título o autor para buscar.");
        }
        return libroDAO.buscarPorTituloOAutor(texto);
    }

    public List<Libro> listarTodos() {
        return libroDAO.listarTodos();
    }

    /** RN-02: un ejemplar está disponible si está activo y no tiene un préstamo sin devolver. */
    public boolean estaDisponible(Libro libro) {
        return libro.isActivo() && !libroDAO.estaPrestado(libro.getId());
    }

    /** RN-09 / RN-10: baja lógica. No se permite si el libro está prestado. */
    public Libro darDeBaja(int id) {
        Libro libro = buscarPorId(id);
        if (!libro.isActivo()) {
            throw new IllegalStateException("El libro N° " + id + " ya está dado de baja.");
        }
        if (libroDAO.estaPrestado(id)) {
            throw new IllegalStateException("No se puede dar de baja el libro N° " + id
                    + ": está prestado y todavía no fue devuelto.");
        }
        libro.setActivo(false);
        libroDAO.actualizar(libro);
        return libro;
    }

    // ---------- validaciones de entrada (privadas) ----------

    private String validarObligatorio(String valor, String campo, int maximo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new IllegalArgumentException(campo + " no puede superar los " + maximo + " caracteres.");
        }
        return limpio;
    }

    /** Campo opcional: si viene vacío se guarda como null. */
    private String validarOpcional(String valor, String campo, int maximo) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return validarObligatorio(valor, campo, maximo);
    }

    private void validarAnio(Integer anio) {
        if (anio != null && (anio <= 0 || anio > LocalDate.now().getYear())) {
            throw new IllegalArgumentException("El año de publicación no es válido.");
        }
    }
}