package biblioteca.negocio;

import biblioteca.datos.PrestamoDAO;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoService {

    /** RN-05: plazo de préstamo en días corridos. */
    public static final int PLAZO_DIAS = 7;
    /** RN-03: máximo de préstamos sin devolver por socio. */
    public static final int MAX_PRESTAMOS_SIN_DEVOLVER = 3;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final SocioService socioService = new SocioService();
    private final LibroService libroService = new LibroService();

    /** Registra un préstamo aplicando RN-01 a RN-06 y RN-12. Devuelve el préstamo para el comprobante. */
    public Prestamo registrarPrestamo(int numeroSocio, int libroId) {
        // RN-01: el socio debe existir y estar activo
        Socio socio = socioService.buscarPorNumero(numeroSocio);
        if (!socio.isActivo()) {
            throw new IllegalStateException("El socio N° " + numeroSocio
                    + " está inactivo: no puede recibir préstamos.");
        }

        List<Prestamo> sinDevolver = prestamoDAO.listarSinDevolverPorSocio(socio.getId());

        // RN-06: con un préstamo vencido no puede retirar otro libro
        for (Prestamo p : sinDevolver) {
            if (p.estaVencido()) {
                throw new IllegalStateException("El socio N° " + numeroSocio
                        + " tiene un préstamo vencido sin devolver (préstamo N° " + p.getId()
                        + "). Debe regularizar su situación antes de retirar otro libro.");
            }
        }

        // RN-03: máximo de préstamos sin devolver
        if (sinDevolver.size() >= MAX_PRESTAMOS_SIN_DEVOLVER) {
            throw new IllegalStateException("El socio N° " + numeroSocio + " ya tiene "
                    + MAX_PRESTAMOS_SIN_DEVOLVER + " préstamos sin devolver.");
        }

        // RN-02: el libro debe existir, estar activo y disponible
        Libro libro = libroService.buscarPorId(libroId);
        if (!libro.isActivo()) {
            throw new IllegalStateException("El libro N° " + libroId
                    + " está dado de baja: no se puede prestar.");
        }
        if (!libroService.estaDisponible(libro)) {
            throw new IllegalStateException("El libro N° " + libroId + " ya está prestado.");
        }

        // RN-05 y RN-12: fecha del sistema y vencimiento a 7 días
        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = new Prestamo(socio, libro, hoy, hoy.plusDays(PLAZO_DIAS));
        prestamoDAO.insertar(prestamo);
        return prestamo;
    }

    /** RN-08 y RN-12: registra la devolución una sola vez, con la fecha del sistema. */
    public Prestamo registrarDevolucion(int prestamoId) {
        Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
        if (prestamo == null) {
            throw new IllegalArgumentException("No existe un préstamo con el N° " + prestamoId + ".");
        }
        if (prestamo.estaDevuelto()) {
            throw new IllegalStateException("El préstamo N° " + prestamoId
                    + " ya fue devuelto el " + prestamo.getFechaDevolucion() + ".");
        }

        prestamo.setFechaDevolucion(LocalDate.now());
        if (!prestamoDAO.actualizarDevolucion(prestamo)) {
            throw new IllegalStateException("No se pudo registrar la devolución: "
                    + "el préstamo N° " + prestamoId + " ya había sido devuelto.");
        }
        return prestamo;
    }

    /** true si el préstamo se devolvió después de su fecha de vencimiento. */
    public boolean devueltoFueraDeTermino(Prestamo prestamo) {
        return prestamo.estaDevuelto()
                && prestamo.getFechaDevolucion().isAfter(prestamo.getFechaVencimiento());
    }

    /** RF-06 / RF-08: historial completo de un socio, identificado por su número. */
    public List<Prestamo> historialDeSocio(int numeroSocio) {
        Socio socio = socioService.buscarPorNumero(numeroSocio);
        return prestamoDAO.listarPorSocio(socio.getId());
    }

    /** RF-07: préstamos sin devolver que todavía no vencieron. */
    public List<Prestamo> listarVigentes() {
        List<Prestamo> vigentes = new ArrayList<>();
        for (Prestamo p : prestamoDAO.listarSinDevolver()) {
            if (!p.estaVencido()) {
                vigentes.add(p);
            }
        }
        return vigentes;
    }

    /** RF-07 / RN-07: préstamos sin devolver con la fecha de vencimiento superada. */
    public List<Prestamo> listarVencidos() {
        List<Prestamo> vencidos = new ArrayList<>();
        for (Prestamo p : prestamoDAO.listarSinDevolver()) {
            if (p.estaVencido()) {
                vencidos.add(p);
            }
        }
        return vencidos;
    }
}