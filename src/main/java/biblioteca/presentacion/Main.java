package biblioteca.presentacion;

import biblioteca.datos.ConexionBD;
import biblioteca.datos.SocioDAO;
import biblioteca.datos.LibroDAO;
import biblioteca.datos.PrestamoDAO;

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

}