package biblioteca.presentacion;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;
import biblioteca.negocio.SocioService;

import java.util.List;

public class MenuAdministrador {

    private final SocioService socioService;
    private final LibroService libroService;
    private final PrestamoService prestamoService;

    public MenuAdministrador(SocioService socioService, LibroService libroService,
                             PrestamoService prestamoService) {
        this.socioService = socioService;
        this.libroService = libroService;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println();
            System.out.println("===== MENÚ ADMINISTRADOR =====");
            System.out.println("1. Registrar libro");
            System.out.println("2. Modificar libro");
            System.out.println("3. Consultar libros");
            System.out.println("4. Dar de baja un libro");
            System.out.println("5. Consultar socios");
            System.out.println("6. Historial de préstamos de un socio");
            System.out.println("7. Listar préstamos vigentes");
            System.out.println("8. Listar préstamos vencidos");
            System.out.println("0. Volver al menú principal");
            opcion = MenuPrincipal.leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrarLibro();
                    case 2 -> modificarLibro();
                    case 3 -> consultarLibros();
                    case 4 -> darDeBajaLibro();
                    case 5 -> consultarSocios();
                    case 6 -> historialDeSocio();
                    case 7 -> MenuPrincipal.imprimirLista(prestamoService.listarVigentes(),
                            "No hay préstamos vigentes.");
                    case 8 -> MenuPrincipal.imprimirLista(prestamoService.listarVencidos(),
                            "No hay préstamos vencidos.");
                    case 0 -> { }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    // ---------- libros ----------

    private void registrarLibro() {
        System.out.println("Cada libro es un ejemplar. Para cargar otro ejemplar del mismo título, registre el libro de nuevo.");
        String titulo = MenuPrincipal.leerTexto("Título: ");
        String autor = MenuPrincipal.leerTexto("Autor: ");
        String editorial = MenuPrincipal.leerTexto("Editorial (opcional): ");
        Integer anio = MenuPrincipal.leerEnteroOpcional("Año de publicación (opcional): ");
        Libro libro = libroService.registrar(titulo, autor, editorial, anio);
        System.out.println("Libro registrado: " + libro);
    }

    private void modificarLibro() {
        int id = MenuPrincipal.leerEntero("N° del libro a modificar: ");
        Libro actual = libroService.buscarPorId(id);
        System.out.println("Datos actuales: " + actual);
        System.out.println("Deje un campo vacío para mantener su valor actual.");
        String titulo = conservar(MenuPrincipal.leerTexto("Nuevo título: "), actual.getTitulo());
        String autor = conservar(MenuPrincipal.leerTexto("Nuevo autor: "), actual.getAutor());
        String editorial = conservar(MenuPrincipal.leerTexto("Nueva editorial: "), actual.getEditorial());
        Integer anio = MenuPrincipal.leerEnteroOpcional("Nuevo año de publicación: ");
        if (anio == null) {
            anio = actual.getAnioPublicacion();
        }
        Libro modificado = libroService.modificar(id, titulo, autor, editorial, anio);
        System.out.println("Libro modificado: " + modificado);
    }

    private void consultarLibros() {
        int tipo = MenuPrincipal.leerEntero("Consultar por (1) título o autor, (2) N° de libro, (3) listar todos: ");
        switch (tipo) {
            case 1 -> {
                String texto = MenuPrincipal.leerTexto("Título o autor a buscar: ");
                MenuPrincipal.imprimirLibros(libroService, libroService.buscarPorTituloOAutor(texto));
            }
            case 2 -> {
                Libro libro = libroService.buscarPorId(MenuPrincipal.leerEntero("N° de libro: "));
                MenuPrincipal.imprimirLibros(libroService, List.of(libro));
            }
            case 3 -> MenuPrincipal.imprimirLibros(libroService, libroService.listarTodos());
            default -> System.out.println("Opción inválida.");
        }
    }

    private void darDeBajaLibro() {
        int id = MenuPrincipal.leerEntero("N° del libro a dar de baja: ");
        Libro libro = libroService.buscarPorId(id);
        System.out.println(libro);
        if (!MenuPrincipal.confirmar("¿Confirma la baja?")) {
            System.out.println("Operación cancelada.");
            return;
        }
        Libro baja = libroService.darDeBaja(id);
        System.out.println("Libro dado de baja: " + baja);
    }

    // ---------- socios y préstamos (solo consulta) ----------

    private void consultarSocios() {
        int tipo = MenuPrincipal.leerEntero("Consultar por (1) número de socio, (2) DNI, (3) listar todos: ");
        switch (tipo) {
            case 1 -> System.out.println(
                    socioService.buscarPorNumero(MenuPrincipal.leerEntero("Número de socio: ")));
            case 2 -> System.out.println(
                    socioService.buscarPorDni(MenuPrincipal.leerTexto("DNI: ")));
            case 3 -> {
                List<Socio> socios = socioService.listarTodos();
                MenuPrincipal.imprimirLista(socios, "No hay socios registrados.");
            }
            default -> System.out.println("Opción inválida.");
        }
    }

    private void historialDeSocio() {
        int numero = MenuPrincipal.leerEntero("Número de socio: ");
        List<Prestamo> prestamos = prestamoService.historialDeSocio(numero);
        MenuPrincipal.imprimirLista(prestamos, "El socio no tiene préstamos registrados.");
    }

    /** Si el campo se dejó vacío, se conserva el valor actual. */
    private static String conservar(String ingresado, String actual) {
        return ingresado.trim().isEmpty() ? actual : ingresado;
    }
}