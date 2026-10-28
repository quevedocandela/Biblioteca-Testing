package biblioteca.datos;

import biblioteca.modelo.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {

    private static final String COLUMNAS = "id, numero_socio, dni, nombre, apellido, activo";

    /** Guarda un socio nuevo y le asigna el id que generó la base. */
    public void insertar(Socio socio) {
        String sql = "INSERT INTO socio (numero_socio, dni, nombre, apellido, activo) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, socio.getNumeroSocio());
            ps.setString(2, socio.getDni());
            ps.setString(3, socio.getNombre());
            ps.setString(4, socio.getApellido());
            ps.setBoolean(5, socio.isActivo());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                socio.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw error("guardar el socio", e);
        }
    }

    /** Guarda los cambios de un socio existente (datos y estado activo). */
    public void actualizar(Socio socio) {
        String sql = "UPDATE socio SET numero_socio = ?, dni = ?, nombre = ?, apellido = ?, activo = ? "
                + "WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, socio.getNumeroSocio());
            ps.setString(2, socio.getDni());
            ps.setString(3, socio.getNombre());
            ps.setString(4, socio.getApellido());
            ps.setBoolean(5, socio.isActivo());
            ps.setInt(6, socio.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw error("actualizar el socio", e);
        }
    }

    public Socio buscarPorId(int id) {
        return buscarUno("id = ?", id);
    }

    public Socio buscarPorNumero(int numeroSocio) {
        return buscarUno("numero_socio = ?", numeroSocio);
    }

    public Socio buscarPorDni(String dni) {
        return buscarUno("dni = ?", dni);
    }

    public List<Socio> listarTodos() {
        String sql = "SELECT " + COLUMNAS + " FROM socio ORDER BY numero_socio";
        List<Socio> socios = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                socios.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw error("listar los socios", e);
        }
        return socios;
    }

    // ---------- métodos auxiliares (privados) ----------

    /** Busca un socio con una condición fija del código (por ejemplo "dni = ?"). */
    private Socio buscarUno(String condicion, Object valor) {
        String sql = "SELECT " + COLUMNAS + " FROM socio WHERE " + condicion;
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw error("buscar el socio", e);
        }
    }

    /** Convierte la fila actual del ResultSet en un objeto Socio. */
    private Socio mapear(ResultSet rs) throws SQLException {
        return new Socio(
                rs.getInt("id"),
                rs.getInt("numero_socio"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getBoolean("activo"));
    }

    private RuntimeException error(String accion, SQLException e) {
        return new RuntimeException("Error de base de datos al " + accion + ": " + e.getMessage(), e);
    }
}