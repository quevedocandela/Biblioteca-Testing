package biblioteca.presentacion;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;
import biblioteca.negocio.SocioService;

import java.util.List;

public class MenuBibliotecario {

    private final SocioService socioService;
    private final LibroService libroService;
    private final PrestamoService prestamoService;

    public MenuBibliotecario(SocioService socioService, LibroService libroService,
                             PrestamoService prestamoService) {
        this.socioService = socioService;
        this.libroService = libroService;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println();
            System.out.println("===== MENÚ BIBLIOTECARIO =====");
            System.out.println("1. Registrar socio");
            System.out.println("2. Modificar socio");
            System.out.println("3. Consultar socio");
            System.out.println("4. Dar de baja un socio");
            System.out.println("5. Consultar libros (por título o autor)");
            System.out.println("6. Registrar préstamo");
            System.out.println("7. Registrar devolución");
            System.out.println("8. Historial de préstamos de un socio");
            System.out.println("9. Listar préstamos vigentes");
            System.out.println("10. Listar préstamos vencidos");
            System.out.println("0. Volver al menú principal");
            opcion = MenuPrincipal.leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrarSocio();
                    case 2 -> modificarSocio();
                    case 3 -> consultarSocio();
                    case 4 -> darDeBajaSocio();
                    case 5 -> consultarLibros();
                    case 6 -> registrarPrestamo();
                    case 7 -> registrarDevolucion();
                    case 8 -> historialDeSocio();
                    case 9 -> MenuPrincipal.imprimirLista(prestamoService.listarVigentes(),
                            "No hay préstamos vigentes.");
                    case 10 -> MenuPrincipal.imprimirLista(prestamoService.listarVencidos(),
                            "No hay préstamos vencidos.");
                    case 0 -> { }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    // ---------- socios ----------

    private void registrarSocio() {
        int numero = MenuPrincipal.leerEntero("Número de socio: ");
        String dni = MenuPrincipal.leerTexto("DNI: ");
        String nombre = MenuPrincipal.leerTexto("Nombre: ");
        String apellido = MenuPrincipal.leerTexto("Apellido: ");
        Socio socio = socioService.registrar(numero, dni, nombre, apellido);
        System.out.println("Socio registrado: " + socio);
    }

    private void modificarSocio() {
        int numero = MenuPrincipal.leerEntero("Número del socio a modificar: ");
        Socio actual = socioService.buscarPorNumero(numero);
        System.out.println("Datos actuales: " + actual);
        System.out.println("Deje un campo vacío para mantener su valor actual.");
        String dni = conservar(MenuPrincipal.leerTexto("Nuevo DNI: "), actual.getDni());
        String nombre = conservar(MenuPrincipal.leerTexto("Nuevo nombre: "), actual.getNombre());
        String apellido = conservar(MenuPrincipal.leerTexto("Nuevo apellido: "), actual.getApellido());
        Socio modificado = socioService.modificar(numero, dni, nombre, apellido);
        System.out.println("Socio modificado: " + modificado);
    }

    private void consultarSocio() {
        int tipo = MenuPrincipal.leerEntero("Buscar por (1) número de socio o (2) DNI: ");
        Socio socio;
        if (tipo == 1) {
            socio = socioService.buscarPorNumero(MenuPrincipal.leerEntero("Número de socio: "));
        } else if (tipo == 2) {
            socio = socioService.buscarPorDni(MenuPrincipal.leerTexto("DNI: "));
        } else {
            System.out.println("Opción inválida.");
            return;
        }
        System.out.println(socio);
    }

    private void darDeBajaSocio() {
        int numero = MenuPrincipal.leerEntero("Número del socio a dar de baja: ");
        Socio socio = socioService.buscarPorNumero(numero);
        System.out.println(socio);
        if (!MenuPrincipal.confirmar("¿Confirma la baja?")) {
            System.out.println("Operación cancelada.");
            return;
        }
        Socio baja = socioService.darDeBaja(numero);
        System.out.println("Socio dado de baja: " + baja);
    }

    // ---------- libros ----------

    private void consultarLibros() {
        String texto = MenuPrincipal.leerTexto("Título o autor a buscar: ");
        List<Libro> libros = libroService.buscarPorTituloOAutor(texto);
        MenuPrincipal.imprimirLibros(libroService, libros);
    }

    // ---------- préstamos y devoluciones ----------

    private void registrarPrestamo() {
        int numeroSocio = MenuPrincipal.leerEntero("Número de socio: ");
        int libroId = MenuPrincipal.leerEntero("N° de libro: ");
        Prestamo prestamo = prestamoService.registrarPrestamo(numeroSocio, libroId);
        imprimirComprobantePrestamo(prestamo);
    }

    private void registrarDevolucion() {
        int prestamoId = MenuPrincipal.leerEntero("N° de préstamo: ");
        Prestamo prestamo = prestamoService.registrarDevolucion(prestamoId);
        imprimirComprobanteDevolucion(prestamo);
    }

    private void historialDeSocio() {
        int numero = MenuPrincipal.leerEntero("Número de socio: ");
        List<Prestamo> prestamos = prestamoService.historialDeSocio(numero);
        MenuPrincipal.imprimirLista(prestamos, "El socio no tiene préstamos registrados.");
    }

    // ---------- comprobantes ----------

    private void imprimirComprobantePrestamo(Prestamo p) {
        System.out.println();
        System.out.println("========== COMPROBANTE DE PRÉSTAMO ==========");
        System.out.println("Préstamo N°  : " + p.getId());
        System.out.println("Socio        : " + nombreSocio(p));
        System.out.println("Libro        : " + descripcionLibro(p));
        System.out.println("Fecha        : " + p.getFechaPrestamo());
        System.out.println("Vencimiento  : " + p.getFechaVencimiento());
        System.out.println("=============================================");
    }

    private void imprimirComprobanteDevolucion(Prestamo p) {
        System.out.println();
        System.out.println("========= COMPROBANTE DE DEVOLUCIÓN =========");
        System.out.println("Préstamo N°  : " + p.getId());
        System.out.println("Socio        : " + nombreSocio(p));
        System.out.println("Libro        : " + descripcionLibro(p));
        System.out.println("Devolución   : " + p.getFechaDevolucion());
        if (prestamoService.devueltoFueraDeTermino(p)) {
            System.out.println("*** Devolución fuera de término ***");
        }
        System.out.println("=============================================");
    }

    private String nombreSocio(Prestamo p) {
        Socio s = p.getSocio();
        return s.getNombre() + " " + s.getApellido() + " (N° " + s.getNumeroSocio() + ")";
    }

    private String descripcionLibro(Prestamo p) {
        Libro l = p.getLibro();
        return "\"" + l.getTitulo() + "\" - " + l.getAutor() + " (N° " + l.getId() + ")";
    }

    /** Si el campo se dejó vacío, se conserva el valor actual. */
    private static String conservar(String ingresado, String actual) {
        return ingresado.trim().isEmpty() ? actual : ingresado;
    }
}