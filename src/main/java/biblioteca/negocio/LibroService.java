package biblioteca.negocio;

public class LibroService {

    /**RN07*/
    public boolean estaDisponible(int ejemplaresDsiponibles) {
        return ejemplaresDsiponibles > 0;
    }

    /**RN11*/
    public int prestarEjemplar(int disponibles) {
        if (disponibles <= 0) {
            throw new IllegalArgumentException("No hay ejemplares disponibles");
        }
        return disponibles -1;
    }

    /**RN12*/
    public int devolverEjemplar(int disponibles, int totalEjemplares) {
        if (disponibles < 0 || disponibles >= totalEjemplares) {
            throw new IllegalArgumentException("Devolucion invalida: disponible fuera del rango");
        }
        return disponibles + 1;
    }

}
