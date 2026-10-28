package biblioteca.negocio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Seccion D del TP: casos 1 a 4 (reglas puras). */
class ReglasTest {

    private final LibroService libroService = new LibroService();
    private final PrestamoService prestamoService = new PrestamoService();

    // ---------- Caso 1: disponibilidad de un libro (RN07) ----------
    @Test
    void libroConEjemplaresEstaDisponible() {
        assertTrue(libroService.estaDisponible(2));
    }

    @Test
    void libroSinEjemplaresNoEstaDisponible() { // caso limite
        assertFalse(libroService.estaDisponible(0));
    }

    // ---------- Caso 2: maximo de prestamos activos (RN08) ----------
    @Test
    void socioConUnPrestamoPuedePedirOtro() {
        assertTrue(prestamoService.puedePedirPrestamo(1));
    }

    @Test
    void socioConTresPrestamosNoPuedePedirOtro() { // caso limite
        assertFalse(prestamoService.puedePedirPrestamo(3));
    }

    @Test
    void socioConDosPrestamosPuedePedirElTercero() { // limite inferior
        assertTrue(prestamoService.puedePedirPrestamo(2));
    }

    // ---------- Caso 3: disminuir disponibilidad al prestar (RN11) ----------
    @Test
    void prestarEjemplarDisminuyeLaDisponibilidad() {
        assertEquals(1, libroService.prestarEjemplar(2));
    }

    @Test
    void prestarSinDisponibilidadLanzaExcepcion() { // caso invalido
        assertThrows(IllegalArgumentException.class, () -> libroService.prestarEjemplar(0));
    }

    // ---------- Caso 4: devolucion de libro (RN12) ----------
    @Test
    void devolverEjemplarAumentaLaDisponibilidad() {
        assertEquals(2, libroService.devolverEjemplar(1, 3));
    }

    @Test
    void devolverConDisponibilidadNegativaLanzaExcepcion() { // caso invalido
        assertThrows(IllegalArgumentException.class, () -> libroService.devolverEjemplar(-1, 3));
    }

    @Test
    void devolverConTodosLosEjemplaresYaDisponiblesLanzaExcepcion() { // caso invalido
        assertThrows(IllegalArgumentException.class, () -> libroService.devolverEjemplar(3, 3));
    }
}
