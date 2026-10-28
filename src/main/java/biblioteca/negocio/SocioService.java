package biblioteca.negocio;

import biblioteca.datos.PrestamoDAO;
import biblioteca.datos.SocioDAO;
import biblioteca.modelo.Socio;

import java.util.List;

public class SocioService {

    private final SocioDAO socioDAO = new SocioDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /** RF-01 / RN-11: registra un socio validando obligatorios y evitando duplicados. */
    public Socio registrar(int numeroSocio, String dni, String nombre, String apellido) {
        if (numeroSocio <= 0) {
            throw new IllegalArgumentException("El número de socio debe ser un número positivo.");
        }
        String dniLimpio = validarDni(dni);
        String nombreLimpio = validarTexto(nombre, "El nombre", 50);
        String apellidoLimpio = validarTexto(apellido, "El apellido", 50);

        if (socioDAO.buscarPorNumero(numeroSocio) != null) {
            throw new IllegalArgumentException("Ya existe un socio con el número " + numeroSocio + ".");
        }
        if (socioDAO.buscarPorDni(dniLimpio) != null) {
            throw new IllegalArgumentException("Ya existe un socio con el DNI " + dniLimpio + ".");
        }

        Socio socio = new Socio(numeroSocio, dniLimpio, nombreLimpio, apellidoLimpio);
        socioDAO.insertar(socio);
        return socio;
    }

    /** RF-02: modifica DNI, nombre y apellido. El número de socio no se modifica. */
    public Socio modificar(int numeroSocio, String dni, String nombre, String apellido) {
        Socio socio = buscarPorNumero(numeroSocio);
        String dniLimpio = validarDni(dni);
        String nombreLimpio = validarTexto(nombre, "El nombre", 50);
        String apellidoLimpio = validarTexto(apellido, "El apellido", 50);

        Socio otro = socioDAO.buscarPorDni(dniLimpio);
        if (otro != null && otro.getId() != socio.getId()) {
            throw new IllegalArgumentException("Ya existe otro socio con el DNI " + dniLimpio + ".");
        }

        socio.setDni(dniLimpio);
        socio.setNombre(nombreLimpio);
        socio.setApellido(apellidoLimpio);
        socioDAO.actualizar(socio);
        return socio;
    }

    /** Devuelve el socio con ese número o falla si no existe. */
    public Socio buscarPorNumero(int numeroSocio) {
        Socio socio = socioDAO.buscarPorNumero(numeroSocio);
        if (socio == null) {
            throw new IllegalArgumentException("No existe un socio con el número " + numeroSocio + ".");
        }
        return socio;
    }

    /** Devuelve el socio con ese DNI o falla si no existe. */
    public Socio buscarPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
        Socio socio = socioDAO.buscarPorDni(dni.trim());
        if (socio == null) {
            throw new IllegalArgumentException("No existe un socio con el DNI " + dni.trim() + ".");
        }
        return socio;
    }

    public List<Socio> listarTodos() {
        return socioDAO.listarTodos();
    }

    /** RN-09 / RN-10: baja lógica. No se permite si tiene préstamos sin devolver. */
    public Socio darDeBaja(int numeroSocio) {
        Socio socio = buscarPorNumero(numeroSocio);
        if (!socio.isActivo()) {
            throw new IllegalStateException("El socio N° " + numeroSocio + " ya está dado de baja.");
        }
        int pendientes = prestamoDAO.listarSinDevolverPorSocio(socio.getId()).size();
        if (pendientes > 0) {
            throw new IllegalStateException("No se puede dar de baja al socio N° " + numeroSocio
                    + ": tiene " + pendientes + " préstamo(s) sin devolver.");
        }
        socio.setActivo(false);
        socioDAO.actualizar(socio);
        return socio;
    }

    // ---------- validaciones de entrada (privadas) ----------

    private String validarTexto(String valor, String campo, int maximo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new IllegalArgumentException(campo + " no puede superar los " + maximo + " caracteres.");
        }
        return limpio;
    }

    private String validarDni(String dni) {
        String limpio = validarTexto(dni, "El DNI", 15);
        if (!limpio.matches("\\d+")) {
            throw new IllegalArgumentException("El DNI debe contener solo números.");
        }
        return limpio;
    }
}