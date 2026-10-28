package biblioteca.datos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final String ARCHIVO_CONFIG = "db.properties";

    private ConexionBD() {
        // Clase de utilidad: no se instancia
    }

    /** Abre y devuelve una conexión nueva. Quien la pide es responsable de cerrarla. */
    public static Connection getConexion() {
        Properties config = cargarConfiguracion();
        try {
            return DriverManager.getConnection(
                    config.getProperty("db.url"),
                    config.getProperty("db.user"),
                    config.getProperty("db.password"));
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a la base de datos. "
                    + "Verifique que PostgreSQL esté activo y que los datos de db.properties sean correctos. "
                    + "Detalle: " + e.getMessage(), e);
        }
    }

    private static Properties cargarConfiguracion() {
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream(ARCHIVO_CONFIG)) {
            if (in == null) {
                throw new RuntimeException("No se encontró el archivo " + ARCHIVO_CONFIG
                        + ". Copie db.properties.example como db.properties (en src/main/resources) "
                        + "y complete su usuario y contraseña.");
            }
            Properties config = new Properties();
            config.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            return config;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo " + ARCHIVO_CONFIG, e);
        }
    }
}