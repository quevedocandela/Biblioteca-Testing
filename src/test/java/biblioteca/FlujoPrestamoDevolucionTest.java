package biblioteca;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FlujoPrestamoDevolucionTest {
    private BibliotecaService svc;
    private Socio socio;
    private Libro libro;
    private final LocalDate hoy = LocalDate.of(2026, 10,14);

    @BeforeEach
    void preparar() {
        svc = new BibliotecaService();
        socio = svc.registrarSocio("30111222", "Ana", "Perez", "ana@gmail.com", "1155550000");
        libro = svc.registrarLibro("978-1", "Clean Code", "Robert Martin", "Prentice Hall", 2);
    }

    @AfterEach
    void limpiar() {
        svc.getPrestamos().clear();
        svc.getLibros().clear();
        svc.getSocios().clear();
        svc = null;
    }

    @Test
    void prestamoDisminuyeDisponibilidadYDevolucionLaReataura() {
        Prestamo p = svc.registrarPrestamo(socio.getId(), libro.getId(), hoy, hoy.plusDays(7));
        assertEquals(1, libro.getDisponibles());
        assertEquals(Prestamo.Estado.ACTIVO, p.getEstado());

        svc.registrarDevolucion(p.getId(), hoy.plusDays(3));
        assertEquals(2, libro.getDisponibles());
        assertEquals(Prestamo.Estado.DEVUELTO, p.getEstado());
        assertEquals(hoy.plusDays(3), p.getFechaDevolucion());
    }

    @Test
    void noSePuedePresentarUnLibroSinDisponibilidad() {
        svc.registrarPrestamo(socio.getId(), libro.getId(), hoy, hoy.plusDays(7));
        Socio otro = svc.registrarSocio("30222333", "Luis", "Gomez", "L@gmail.com", "1155551111");
        svc.registrarPrestamo(otro.getId(), libro.getId(), hoy, hoy.plusDays(7));
        assertEquals(0, libro.getDisponibles());

        Socio tercero = svc.registrarSocio("30333444", "Eva", "Diaz", "e@gmail.com", "1155552222");
        assertThrows(IllegalArgumentException.class,
                () -> svc.registrarPrestamo(tercero.getId(), libro.getId(), hoy, hoy.plusDays(7)));
    }

    @Test
    void socioInactivoNoPuedePedirPrestamo() {
        svc.cambiarEstadoSocio(socio.getId(), false);
        assertThrows(IllegalArgumentException.class,
                () -> svc. registrarPrestamo(socio.getId(), libro.getId(), hoy, hoy.plusDays(7)));
        assertEquals(2, libro.getDisponibles());
    }

    @Test
    void socioNoPuedeTenerMasDeTresPrestamosActivos() {
        for (int i = 1; i <= 3; i++) {
            Libro l = svc.registrarLibro("isbn-" + i, "Libro " + i, "Autor", "Ed", 5);
            svc.registrarPrestamo(socio.getId(), l.getId(), hoy, hoy.plusDays(7));
        }
        assertEquals(3, svc.contarPrestamosActivos(socio));
        assertThrows(IllegalArgumentException.class,
                () -> svc.registrarPrestamo(socio.getId(), libro.getId(),hoy, hoy.plusDays(7)));
    }

    @Test
    void devolverUnPrestamoInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException. class, () -> svc.registrarDevolucion(999, hoy));
    }

    @Test
    void noSePuedeDevolverDosVecesElMismoPrestamo() {
        Prestamo p = svc.registrarPrestamo(socio.getId(), libro.getId(), hoy, hoy.plusDays(7));
        svc.registrarDevolucion(p.getId(), hoy.plusDays(1));
        assertThrows(IllegalStateException.class, () -> svc.registrarDevolucion(p.getId(), hoy.plusDays(2)));
        assertEquals(2, libro.getDisponibles());
    }

    @Test
    void noSePuedeRegistrarSocioConDniRepetido() {
        assertThrows(IllegalArgumentException.class,
                () -> svc.registrarSocio("30111222", "Otra", "Persona", "o@gmai.com", "1"));
    }

    @Test
    void noSePuedeRegistrarSociosSinDni() {
        assertThrows(IllegalArgumentException.class,
                () -> svc.registrarSocio("", "Sin", "Dni", "s@gmail.com", "1"));
    }
}
