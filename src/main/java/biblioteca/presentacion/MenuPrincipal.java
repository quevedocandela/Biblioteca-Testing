package biblioteca.presentacion;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Socio;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;
import biblioteca.negocio.SocioService;

import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {

    private static final Scanner ENTRADA = new Scanner(System.in);

    private final SocioService socioService = new SocioService();
    private final LibroService libroService = new LibroService();
    private final PrestamoService prestamoService = new PrestamoService();

    /** Menú inicial: elige el perfil con el que se ingresa. */
    public void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println("===== SISTEMA DE GESTIÓN DE BIBLIOTECA =====");
            System.out.println("Ingresar como:");
            System.out.println("1. Socio");
            System.out.println("2. Bibliotecario");
            System.out.println("3. Administrador");
            System.out.println("0. Salir");
            opcion = leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> ingresarComoSocio();
                    case 2 -> new MenuBibliotecario(socioService, libroService, prestamoService).mostrar();                    case 3 -> System.out.println("El menú de administrador se habilita en el próximo paso.");
                    case 0 -> System.out.println("Hasta luego.");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    /** Para entrar como socio se pide un número de socio existente (sin usuario ni contraseña). */
    private void ingresarComoSocio() {
        int numero = leerEntero("Número de socio: ");
        Socio socio = socioService.buscarPorNumero(numero); // falla con mensaje claro si no existe
        System.out.println("Bienvenido/a, " + socio.getNombre() + " " + socio.getApellido() + ".");
        new MenuSocio(socio, libroService, prestamoService).mostrar();
    }

    // ---------- utilidades de consola compartidas por todos los menús ----------

    static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return ENTRADA.nextLine();
    }

    /** Pide un entero y repite hasta que se ingrese uno válido. */
    static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje).trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    /** Igual que leerEntero, pero si se deja vacío devuelve null (campo opcional). */
    static Integer leerEnteroOpcional(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim();
            if (texto.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido o deje el campo vacío.");
            }
        }
    }

    /** Pregunta S/N y devuelve true si se respondió "S". */
    static boolean confirmar(String mensaje) {
        return leerTexto(mensaje + " (S/N): ").trim().equalsIgnoreCase("S");
    }

    /** Imprime libros indicando si cada uno está disponible. */
    static void imprimirLibros(LibroService libroService, List<Libro> libros) {
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros.");
            return;
        }
        for (Libro libro : libros) {
            String disponibilidad = libroService.estaDisponible(libro) ? "Disponible" : "No disponible";
            System.out.println(libro + " | " + disponibilidad);
        }
    }

    /** Imprime una lista de objetos (socios, préstamos...) o un mensaje si está vacía. */
    static <T> void imprimirLista(List<T> elementos, String mensajeVacio) {
        if (elementos.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }
        elementos.forEach(System.out::println);
    }
}