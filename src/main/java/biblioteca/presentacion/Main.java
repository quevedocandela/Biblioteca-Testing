package biblioteca.presentacion;

import biblioteca.datos.ConexionBD;

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
    }
}