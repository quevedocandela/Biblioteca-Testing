package biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReglasTest {
    private final LibroService libroService = new LibroService();
    private final PrestamoService prestamoService = new PrestamoService();

    //Caso 1: Disponibilidad de un Libro (RN07)
    @Test
    void libroConEjemplaresEstaDisponible() {
        assertTrue(libroService.estaDisponible(2));
    }

    @Test
    void libroSinEjemplaresNoEstaDisponible() { //caso limite
        assertFalse(libroService.estaDisponible(0));
    }

    //Caso 2: Maximo de prestamos activos (RN08)
    @Test
    void socioConUnPrestamoPuedePedirOtro() {
        assertTrue(prestamoService.puedePedirPrestamo(1));
    }

    @Test
    void socioConTresPrestamosNoPuedePedirOtro() { //caso limite
        assertFalse(prestamoService.puedePedirPrestamo(3));
    }

    @Test
    void socioDosPrestamosPuedePedirElTercero() { //limite inferior
        assertTrue(prestamoService.puedePedirPrestamo(2));
    }

    //Caso 3: Disminuir disponibilidad al prestar (RN11)
    @Test
    void prestarEjemplarDiminuyeLaDisponibilidad() {
        assertEquals(1, libroService.prestarEjemplar(2));
    }

    @Test
    void prestarSinDisponibilidadLanzaExcepcion() { //caso invalido
        assertThrows(IllegalArgumentException.class, () -> libroService.prestarEjemplar(0));
    }

    //Caso 4: devolucion de Libro (RN12)
    @Test
    void devolverEjemplarAumentaLaDisponibilidad() {
        assertEquals(2, libroService.devolverEjemplar(1,3));
    }

    @Test
    void devolverConDisponibilidadNegativaLanzaExcepcion() { //caso invalido
        assertThrows(IllegalArgumentException.class, () -> libroService.devolverEjemplar(-1, 3));
    }

    @Test
    void devolverConTOdosLosEjemplaresYaDisponiblesLanzaExcepcion() { //caso invalido
        assertThrows(IllegalArgumentException.class, () -> libroService.devolverEjemplar(3,3));
    }

}
