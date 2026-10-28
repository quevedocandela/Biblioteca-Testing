package biblioteca.datos;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    /** Une prestamo con socio y libro, para armar el objeto completo en una sola consulta. */
    private static final String SELECT_BASE =
            "SELECT p.id, p.fecha_prestamo, p.fecha_vencimiento, p.fecha_devolucion, "
                    + "s.id AS s_id, s.numero_socio, s.dni, s.nombre, s.apellido, s.activo AS s_activo, "
                    + "l.id AS l_id, l.titulo, l.autor, l.editorial, l.anio_publicacion, l.activo AS l_activo "
                    + "FROM prestamo p "
                    + "JOIN socio s ON s.id = p.socio_id "
                    + "JOIN libro l ON l.id = p.libro_id ";

    /** Guarda un préstamo nuevo y le asigna el id que generó la base. */
    public void insertar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamo (socio_id, libro_id, fecha_prestamo, fecha_vencimiento) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, prestamo.getSocio().getId());
            ps.setInt(2, prestamo.getLibro().getId());
            ps.setObject(3, prestamo.getFechaPrestamo());
            ps.setObject(4, prestamo.getFechaVencimiento());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                prestamo.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            // 23505 = violación de restricción única: el índice ux_libro_prestado
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalStateException("El libro ya tiene un préstamo sin devolver.", e);
            }
            throw error("guardar el préstamo", e);
        }
    }

    /** Guarda la fecha de devolución. Solo actualiza si todavía no estaba devuelto. */
    public boolean actualizarDevolucion(Prestamo prestamo) {
        String sql = "UPDATE prestamo SET fecha_devolucion = ? WHERE id = ? AND fecha_devolucion IS NULL";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, prestamo.getFechaDevolucion());
            ps.setInt(2, prestamo.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw error("registrar la devolución", e);
        }
    }

    public Prestamo buscarPorId(int id) {
        List<Prestamo> resultado = consultar("WHERE p.id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    /** Historial completo del socio (id interno), del más reciente al más antiguo. */
    public List<Prestamo> listarPorSocio(int socioId) {
        return consultar("WHERE p.socio_id = ? ORDER BY p.fecha_prestamo DESC, p.id DESC", socioId);
    }

    /** Todos los préstamos sin devolver (vigentes y vencidos). */
    public List<Prestamo> listarSinDevolver() {
        return consultar("WHERE p.fecha_devolucion IS NULL ORDER BY p.fecha_vencimiento, p.id");
    }

    /** Préstamos sin devolver de un socio (id interno). */
    public List<Prestamo> listarSinDevolverPorSocio(int socioId) {
        return consultar("WHERE p.socio_id = ? AND p.fecha_devolucion IS NULL "
                + "ORDER BY p.fecha_vencimiento, p.id", socioId);
    }

    // ---------- métodos auxiliares (privados) ----------

    /** Ejecuta el SELECT base con un fragmento WHERE/ORDER BY fijo del código. */
    private List<Prestamo> consultar(String condicionYOrden, Object... parametros) {
        String sql = SELECT_BASE + condicionYOrden;
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw error("consultar préstamos", e);
        }
        return prestamos;
    }

    /** Convierte la fila actual en un Prestamo con su Socio y su Libro. */
    private Prestamo mapear(ResultSet rs) throws SQLException {
        Socio socio = new Socio(
                rs.getInt("s_id"),
                rs.getInt("numero_socio"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getBoolean("s_activo"));

        int anio = rs.getInt("anio_publicacion");
        Integer anioPublicacion = rs.wasNull() ? null : anio;
        Libro libro = new Libro(
                rs.getInt("l_id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                anioPublicacion,
                rs.getBoolean("l_activo"));

        return new Prestamo(
                rs.getInt("id"),
                socio,
                libro,
                rs.getObject("fecha_prestamo", LocalDate.class),
                rs.getObject("fecha_vencimiento", LocalDate.class),
                rs.getObject("fecha_devolucion", LocalDate.class)); // null si no fue devuelto
    }

    private RuntimeException error(String accion, SQLException e) {
        return new RuntimeException("Error de base de datos al " + accion + ": " + e.getMessage(), e);
    }
}