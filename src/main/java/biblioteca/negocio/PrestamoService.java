package biblioteca.negocio;

public class PrestamoService {

    public static final int MAX_PRESTAMOS_ACTIVOS = 3;

    /*RN08*/
    public boolean puedePedirPrestamo(int prestamosActivos) {
        return prestamosActivos < MAX_PRESTAMOS_ACTIVOS;
    }
}
