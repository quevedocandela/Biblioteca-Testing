package biblioteca.presentacion;

import biblioteca.datos.ConexionBD;
import biblioteca.datos.SocioDAO;
import biblioteca.datos.LibroDAO;
import biblioteca.datos.PrestamoDAO;
import biblioteca.negocio.SocioService;
import biblioteca.negocio.LibroService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/** PROVISIONAL: solo prueba la conexión. Se reemplaza en la rama feature/menus. */
public class Main {
    public static void main(String[] args) {
        try (Connection con = ConexionBD.getConexion()) {
            System.out.println("Conexión exitosa a: " + con.getMetaData().getURL());
            System.out.println("Versión de PostgreSQL: " + con.getMetaData().getDatabaseProductVersion());
            contar(con, "socio");
            contar(con, "libro");
            contar(con, "prestamo");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void contar(Connection con, String tabla) throws Exception {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            System.out.println("Filas en " + tabla + ": " + rs.getInt(1));
        }

        LibroService libroService = new LibroService();
        probar(() -> libroService.registrar("  ", "Autor", null, 2000));      // título vacío
        probar(() -> libroService.registrar("Título", "Autor", null, 3000));  // año futuro
        probar(() -> libroService.darDeBaja(1));                               // está prestado
        probar(() -> libroService.darDeBaja(8));                               // ya inactivo
        probar(() -> libroService.buscarPorId(99));                            // no existe
        probar(() -> libroService.buscarPorTituloOAutor("  "));                // búsqueda vacía
        System.out.println("Disponible libro 1? " + libroService.estaDisponible(libroService.buscarPorId(1)));
        System.out.println("Disponible libro 2? " + libroService.estaDisponible(libroService.buscarPorId(2)));
        System.out.println("Disponible libro 8? " + libroService.estaDisponible(libroService.buscarPorId(8)));

        SocioService socios = new SocioService();
        probar(() -> socios.registrar(1001, "99999999", "Ana", "Prueba"));  // número repetido
        probar(() -> socios.registrar(2000, "40111222", "Ana", "Prueba"));  // DNI repetido
        probar(() -> socios.registrar(2000, "abc", "Ana", "Prueba"));       // DNI inválido
        probar(() -> socios.registrar(2000, "50123456", "  ", "Prueba"));   // nombre vacío
        probar(() -> socios.darDeBaja(1004));                               // tiene 3 préstamos
        probar(() -> socios.darDeBaja(1003));                               // ya está inactivo
        probar(() -> socios.buscarPorNumero(9999));                         // no existe

        SocioDAO dao = new SocioDAO();
        dao.listarTodos().forEach(System.out::println);
        System.out.println("Buscar 1003: " + dao.buscarPorNumero(1003));
        System.out.println("Buscar 9999: " + dao.buscarPorNumero(9999));

        LibroDAO libros = new LibroDAO();
        System.out.println("Buscar 'rayuela': " + libros.buscarPorTituloOAutor("rayuela"));
        System.out.println("Buscar 'borges': " + libros.buscarPorTituloOAutor("borges"));
        System.out.println("Libro 1 prestado? " + libros.estaPrestado(1));
        System.out.println("Libro 2 prestado? " + libros.estaPrestado(2));
        System.out.println("Libro 8: " + libros.buscarPorId(8));

        PrestamoDAO prestamos = new PrestamoDAO();
        System.out.println("--- Sin devolver:");
        prestamos.listarSinDevolver().forEach(System.out::println);
        System.out.println("--- Historial del socio con id 1 (1001):");
        prestamos.listarPorSocio(1).forEach(System.out::println);
        System.out.println("Sin devolver del socio con id 4 (1004): " + prestamos.listarSinDevolverPorSocio(4).size());
        System.out.println("Préstamo 3: " + prestamos.buscarPorId(3));


    }

    private static void probar(Runnable accion) {
        try {
            accion.run();
            System.out.println("OK (no falló)");
        } catch (RuntimeException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }
    }

}