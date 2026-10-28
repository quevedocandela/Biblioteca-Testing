package biblioteca.presentacion;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;

import java.util.List;

public class MenuSocio {

    private final Socio socio;
    private final LibroService libroService;
    private final PrestamoService prestamoService;

    public MenuSocio(Socio socio, LibroService libroService, PrestamoService prestamoService) {
        this.socio = socio;
        this.libroService = libroService;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println();
            System.out.println("===== MENÚ SOCIO (N° " + socio.getNumeroSocio() + ") =====");
            System.out.println("1. Consultar libros (por título o autor)");
            System.out.println("2. Consultar mis préstamos");
            System.out.println("0. Volver al menú principal");
            opcion = MenuPrincipal.leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> consultarLibros();
                    case 2 -> consultarMisPrestamos();
                    case 0 -> { }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void consultarLibros() {
        String texto = MenuPrincipal.leerTexto("Título o autor a buscar: ");
        List<Libro> libros = libroService.buscarPorTituloOAutor(texto);
        MenuPrincipal.imprimirLibros(libroService, libros);
    }

    /** RF-08: siempre usa el socio con el que se ingresó; nunca pregunta por otro número. */
    private void consultarMisPrestamos() {
        List<Prestamo> prestamos = prestamoService.historialDeSocio(socio.getNumeroSocio());
        MenuPrincipal.imprimirLista(prestamos, "Todavía no tenés préstamos registrados.");
    }
}