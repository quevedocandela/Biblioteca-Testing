package biblioteca.datos;

import biblioteca.modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    private static final String COLUMNAS = "id, titulo, autor, editorial, anio_publicacion, activo";

    /** Guarda un libro nuevo y le asigna el id que generó la base. */
    public void insertar(Libro libro) {
        String sql = "INSERT INTO libro (titulo, autor, editorial, anio_publicacion, activo) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setObject(4, libro.getAnioPublicacion(), Types.INTEGER);
            ps.setBoolean(5, libro.isActivo());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                libro.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw error("guardar el libro", e);
        }
    }

    /** Guarda los cambios de un libro existente (datos y estado activo). */
    public void actualizar(Libro libro) {
        String sql = "UPDATE libro SET titulo = ?, autor = ?, editorial = ?, "
                + "anio_publicacion = ?, activo = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setObject(4, libro.getAnioPublicacion(), Types.INTEGER);
            ps.setBoolean(5, libro.isActivo());
            ps.setInt(6, libro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw error("actualizar el libro", e);
        }
    }

    public Libro buscarPorId(int id) {
        List<Libro> resultado = consultar("WHERE id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    /** Coincidencia parcial en título o autor, sin distinguir mayúsculas (ILIKE). */
    public List<Libro> buscarPorTituloOAutor(String texto) {
        String patron = "%" + texto.trim() + "%";
        return consultar("WHERE titulo ILIKE ? OR autor ILIKE ? ORDER BY titulo, id", patron, patron);
    }

    public List<Libro> listarTodos() {
        return consultar("ORDER BY titulo, id");
    }

    /** true si el ejemplar tiene un préstamo sin devolver (fecha_devolucion NULL). */
    public boolean estaPrestado(int libroId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM prestamo WHERE libro_id = ? AND fecha_devolucion IS NULL)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw error("consultar si el libro está prestado", e);
        }
    }

    // ---------- métodos auxiliares (privados) ----------

    /** Ejecuta un SELECT sobre libro. El fragmento WHERE/ORDER BY es fijo y viene del código. */
    private List<Libro> consultar(String condicionYOrden, Object... parametros) {
        String sql = "SELECT " + COLUMNAS + " FROM libro " + condicionYOrden;
        List<Libro> libros = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw error("consultar libros", e);
        }
        return libros;
    }

    /** Convierte la fila actual del ResultSet en un objeto Libro. */
    private Libro mapear(ResultSet rs) throws SQLException {
        int anio = rs.getInt("anio_publicacion");
        Integer anioPublicacion = rs.wasNull() ? null : anio; // getInt devuelve 0 si era NULL
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                anioPublicacion,
                rs.getBoolean("activo"));
    }

    private RuntimeException error(String accion, SQLException e) {
        return new RuntimeException("Error de base de datos al " + accion + ": " + e.getMessage(), e);
    }
}