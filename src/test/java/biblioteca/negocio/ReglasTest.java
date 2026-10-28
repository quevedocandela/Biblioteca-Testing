package biblioteca.negocio;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de reglas puras: no usan la base de datos. */
class ReglasTest {

    private final PrestamoService prestamoService = new PrestamoService();
    private final LocalDate hoy = LocalDate.of(2026, 10, 28);
    private final Socio socio = new Socio(1, 1001, "40111222", "Lucía", "Fernández", true);
    private final Libro libro = new Libro(1, "Rayuela", "Julio Cortázar", null, 1963, true);

    /** Arma un préstamo con el vencimiento y la devolución indicados. */
    private Prestamo prestamo(LocalDate vencimiento, LocalDate devolucion) {
        return new Prestamo(1, socio, libro, vencimiento.minusDays(7), vencimiento, devolucion);
    }

    // ---------- RN-07: préstamo vencido ----------

    @Test
    void sinDevolverYConFechaSuperadaEstaVencido() {
        assertTrue(prestamo(hoy.minusDays(1), null).estaVencido(hoy));
    }

    @Test
    void queVenceHoyTodaviaNoEstaVencido() { // caso límite
        assertFalse(prestamo(hoy, null).estaVencido(hoy));
    }

    @Test
    void conFechaFuturaNoEstaVencido() {
        assertFalse(prestamo(hoy.plusDays(3), null).estaVencido(hoy));
    }

    @Test
    void unPrestamoDevueltoNuncaEstaVencido() {
        assertFalse(prestamo(hoy.minusDays(5), hoy.minusDays(2)).estaVencido(hoy));
    }

    // ---------- Devolución ----------

    @Test
    void estaDevueltoSoloSiTieneFechaDeDevolucion() {
        assertFalse(prestamo(hoy, null).estaDevuelto());
        assertTrue(prestamo(hoy, hoy).estaDevuelto());
    }

    @Test
    void devolucionPosteriorAlVencimientoEsFueraDeTermino() {
        assertTrue(prestamoService.devueltoFueraDeTermino(prestamo(hoy.minusDays(3), hoy)));
    }

    @Test
    void devolucionEnLaFechaDeVencimientoEsEnTermino() { // caso límite
        assertFalse(prestamoService.devueltoFueraDeTermino(prestamo(hoy, hoy)));
    }

    @Test
    void unPrestamoSinDevolverNoEsFueraDeTermino() {
        assertFalse(prestamoService.devueltoFueraDeTermino(prestamo(hoy.minusDays(3), null)));
    }

    // ---------- RN-03 y RN-05 ----------

    @Test
    void plazoYMaximoSegunLasReglas() {
        assertEquals(7, PrestamoService.PLAZO_DIAS);
        assertEquals(3, PrestamoService.MAX_PRESTAMOS_SIN_DEVOLVER);
    }
}