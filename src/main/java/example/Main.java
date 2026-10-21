package example;

import biblioteca.BibliotecaService;
import biblioteca.Libro;
import biblioteca.Socio;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

//TIP Para <b>ejecutar</b> el código, pulsar <shortcut actionId="Run"/> o
// Haz clic en el ícono <icon src="AllIcons.Actions.Execute"/> del margen.
public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final BibliotecaService svc = new BibliotecaService();

    public static void main(String[] args) {
        int op;
        do {
            System.out.println("\n===== BIBLIOTECA =====");
            System.out.println("1. Registrar socio");
            System.out.println("2. Consultar socio (por DNI)");
            System.out.println("3. Activar/Desactivar socio");
            System.out.println("4. Registrar libro");
            System.out.println("5. Consultar disponibilidad (titulo/autor/ISBN)");
            System.out.println("6. Activar/Desactivar libro");
            System.out.println("7. Registrar prestamo");
            System.out.println("8. Registrar devolucion");
            System.out.println("9. Listar prestamos");
            System.out.println("0. Salir");
            op = leerInt("Opcion: ");
            try {
                switch (op) {
                    case 1 -> registrarSocio();
                    case 2 -> consultarSocio();
                    case 3 -> estadoSocio();
                    case 4 -> registrarLibro();
                    case 5 -> consultarLibros();
                    case 6 -> estadoLibro(); 
                    case 7 -> registrarPrestamo();
                    case 8 -> registrarDevolucion();
                    case 9 -> svc.getPrestamos().forEach(System.out::println);
                    case 0 -> System.out.println("Hasta luego");
                    default -> System.out.println("Opcion invalida");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: "+ e.getMessage());
            }
        } while (op != 0);
    }

    private static String leer(String msg) {
        System.out.println(msg);
        return in.nextLine();
    }

    private static int leerInt(String msg) {
        while (true) {
            try {
                return Integer.parseInt(leer(msg).trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número valido");
            }
        }
    }

    private static LocalDate leerFecha(String msg) {
        while (true) {
            try {
                return LocalDate.parse(leer(msg + " (AAAA-MM-DD): ").trim());
            } catch (Exception e) {
                System.out.println("Fecha invalida. Formato  válido AAAA-MM-DD");
            }
        }
    }

    private static void registrarSocio() {
        Socio s = svc.registrarSocio(leer("DNI: "), leer("Nombre: "), leer("Apellido: "),
                leer("Correo: "), leer("Telefono: "));
        System.out.println("Socio registrado: " + s);
    }

    private static void consultarSocio() {
        Socio s = svc.buscarSocioPorDni(leer("DNI: "));
        System.out.println(s == null ? "Socio no encontrado" : s.toString());
    }

    private static void estadoSocio() {
        int id = leerInt("ID del socio: ");
        boolean activar = leer("Activar (A) o Desactivar (D): ").trim().equalsIgnoreCase("A");
        svc.cambiarEstadoSocio(id, activar);
        System.out.println("Estado actualizado: " + svc.buscarSocioPorId(id));
    }

    private static void registrarLibro() {
        String isbn = leer("ISBN: ");
        String titulo = leer("Titulo: ");
        String autor = leer("Autor: ");
        String editorial = leer("Editorial: ");
        int cant = leerInt("Cantidad de ejemplares: ");
        Libro l = svc.registrarLibro(isbn, titulo, autor, editorial, cant);
        System.out.println("Libro registrado: " + l);
    }

    private static void consultarLibros() {
        List<Libro> res = svc.buscarLibros(leer("Buscar: "));
        if (res.isEmpty()) System.out.println("No se encontraron libros");
        else res.forEach(System.out::println);
    }

    private static void estadoLibro() {
        int id = leerInt("ID del libro: ");
        boolean activar = leer("Activar (A) o Desactivar (D): ").trim().equalsIgnoreCase("A");
        svc.cambiarEstadoLibro(id, activar);
        System.out.println("Estado actualizado: " + svc.buscarLibroPorId(id));
    }

    private static void registrarPrestamo() {
        int socioId = leerInt("ID del socio: ");
        int libroId = leerInt("ID del libro: ");
        LocalDate prestamo = leerFecha("Fecha de prestamo");
        LocalDate prevista = leerFecha("Fecha prevista de devolucion");
        System.out.println("Prestamos registrado: " + svc.registrarPrestamo(socioId, libroId, prestamo, prevista));
    }

    private static void registrarDevolucion() {
        int id = leerInt("ID del prestamo: ");
        LocalDate fecha = leerFecha("Fecha de devolucion");
        System.out.println("Devolucion registrada: " + svc.registrarDevolucion(id, fecha));
    }

}