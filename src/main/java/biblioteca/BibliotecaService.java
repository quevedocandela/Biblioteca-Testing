package biblioteca;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaService {
    private final List<Socio> socios = new ArrayList<>();
    private final List<Libro> libros = new ArrayList<>();
    private final List<Prestamo> prestamos = new ArrayList<>();
    private final LibroService libroService = new LibroService();
    private final PrestamoService prestamoService = new PrestamoService();
    private int sigSocio = 1, sigLibro = 1, sigPrestamo = 1;

    private static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    //SOCIOS
    public Socio registrarSocio(String dni, String nombre, String apellido, String correo, String telefono) {
        if (vacio(dni)) throw new IllegalArgumentException("El DNI es obligatorio"); //RN01
        if (vacio(nombre)) throw new IllegalArgumentException("El nombre es obligatorio"); //HU01
        if (vacio(apellido)) throw new IllegalArgumentException("El apellido es obligatorio"); //HU01
        if (buscarSocioPorDni(dni) != null)
            throw new IllegalArgumentException("Ya existe un socio con ese DNI");
        Socio s = new Socio(sigSocio++, dni.trim(), nombre.trim(), apellido.trim(), correo, telefono);
        socios.add(s);
        return s;
    }

    public Socio buscarSocioPorDni(String dni) {
        if (dni == null) return null;
        for (Socio s : socios) if (s.getDni().equals(dni.trim())) return s;
        return null;
    }

    public Socio buscarSocioPorId(int id) {
        for (Socio s : socios) if (s.getId() == id) return s;
        return null;
    }

    public void cambiarEstadoSocio(int id, boolean activo) {
        Socio s = buscarSocioPorId(id);
        if (s == null) throw new IllegalArgumentException("El socio no existe");
        s.setActivo(activo);
    }

    //LIBROS
    public Libro registrarLibro(String isbn, String titulo, String autor, String editorial, int cantidad) {
        if (vacio(isbn) || vacio(titulo) || vacio(autor))
            throw new IllegalArgumentException("ISBN, titulo y autor son obligatorios"); //RN04
        if (cantidad < 0)
            throw new IllegalArgumentException("La cantidad de ejemplares no puede ser negativa"); //RN06
        if (buscarPorIsbn(isbn) != null)
            throw new IllegalArgumentException("Ya existe un libro con ese ISBN"); //RN05
        Libro l = new Libro(sigLibro++, isbn.trim(), titulo.trim(), autor.trim(), editorial, cantidad);
        libros.add(l);
        return l;
    }

    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null) return null;
        for (Libro l : libros) if (l.getIsbn().equals(isbn.trim())) return l;
        return null;
    }

    public Libro buscarLibroPorId(int id) {
        for(Libro l : libros) if (l.getId() == id) return l;
        return null;
    }

    /* HU03: busca por titulo, autor o ISBN (coincidencia parcial, sin distinguir mayusculas). */
    public List<Libro> buscarLibros(String texto) {
        List<Libro> res = new ArrayList<>();
        if (vacio(texto)) return res;
        String t = texto.trim().toLowerCase();
        for (Libro l : libros) {
            if (l.getTitulo().toLowerCase().contains(t)
                    || l.getAutor().toLowerCase().contains(t)
                    || l.getIsbn().toLowerCase().contains(t)) {
                res.add(l);
            }
        }
        return res;
    }

    public void cambiarEstadoLibro(int id, boolean activo){
        Libro l = buscarLibroPorId(id);
        if (l == null) throw new IllegalArgumentException("El libro no existe");
        l.setActivo(activo);
    }

    //PRESTAMOS
    public int contarPrestamosActivos(Socio socio) {
        int n = 0;
        for (Prestamo p : prestamos) if (p.isActivo() && p.getSocio() == socio) n++;
        return n;
    }

    public Prestamo registrarPrestamo(int socioId, int libroId, LocalDate fechaPrestamo, LocalDate fechaPrevista) {
        Socio socio = buscarSocioPorId(socioId);
        if (socio == null) throw new IllegalArgumentException("El socio no existe"); //HU04
        Libro libro = buscarLibroPorId(libroId);
        if (libro == null) throw new IllegalArgumentException("El libro no existe"); //HU04
        if (!socio.isActivo())
            throw new IllegalArgumentException("El socio esta inactivo"); //RN03
        if (!libroService.estaDisponible(libro.getDisponibles()))
            throw new IllegalArgumentException("No hay ejemplares disponibles"); //RN07 / RN09
        if (!prestamoService.puedePedirPrestamo(contarPrestamosActivos(socio)))
            throw new IllegalStateException("El socio ya tiene 3 prestamos activos"); // RN08
        if (fechaPrestamo == null || fechaPrevista == null)
            throw new IllegalArgumentException("Las fechas son obligatorias");
        if (fechaPrevista.isBefore(fechaPrestamo))
            throw new IllegalArgumentException("La fecha prevista no puede ser anterior al prestamo");

        libro.setDisponibles(libroService.prestarEjemplar((libro.getDisponibles())));
        Prestamo p = new Prestamo(sigPrestamo++, socio, libro, fechaPrestamo, fechaPrevista);
        prestamos.add(p);
        return p;
    }

    //DEVOLUCIONES
    public Prestamo buscarPrestamoActivo(int prestamoId) {
        for (Prestamo p : prestamos) if (p.getId() == prestamoId && p.isActivo()) return p;
        return null;
    }

    public Prestamo registrarDevolucion(int prestamoId, LocalDate fechaDevolucion) {
        Prestamo p = buscarPrestamoActivo(prestamoId);
        if (p == null)
            throw new IllegalArgumentException("No existe un prestamo activo con ese ID");
        if (fechaDevolucion == null)
            throw new IllegalArgumentException("La fecha de devolucion es obligatoria");
        Libro l = p.getLibro();
        l.setDisponibles(libroService. devolverEjemplar(l.getDisponibles(), l.getCantidad()));
        p.devolver(fechaDevolucion);
        return p;
    }

    //LISTADOS
    public List<Socio> getSocios() { return socios; }
    public List<Libro> getLibros() { return libros; }
    public List<Prestamo> getPrestamos() { return prestamos; }
}