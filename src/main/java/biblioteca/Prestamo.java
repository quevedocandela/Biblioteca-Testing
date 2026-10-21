package biblioteca;

import java.time.LocalDate;

public class Prestamo {
    public enum Estado { ACTIVO, DEVUELTO }
    private final int id;
    private final Socio socio;
    private final Libro libro;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaPrevista;
    private LocalDate fechaDevolucion;
    private Estado estado;

    public Prestamo(int id, Socio socio, Libro libro, LocalDate fechaPrestamo, LocalDate fechaPrevista) {
        this.id = id;
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaPrevista = fechaPrevista;
        this.estado = Estado.ACTIVO;
    }

    public int getId() { return id;}
    public Socio getSocio() { return socio; }
    public Libro getLibro() { return libro; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaPrevista() { return fechaPrevista; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public Estado getEstado() { return estado;}

    public boolean isActivo() { return estado == Estado.ACTIVO; }

    public void devolver(LocalDate fecha) {
        this.fechaDevolucion = fecha;
        this.estado = Estado.DEVUELTO;
    }

    @Override
    public String toString() {
        return "Préstamo N° " + id + " | Socio: " + socio.getNombre() + " " + socio.getApellido()
                + " (DNI " + socio.getDni() + ") | Libro: \"" + libro.getTitulo() + "\""
                + " | Prestado: " + fechaPrestamo + " Devolución prevista: " + fechaPrevista
                + " | Devuelto: " + (fechaDevolucion == null ? "-" : fechaDevolucion)
                + " | Estado: " + (estado == Estado.ACTIVO ? "Activo" : "Devuelto");
    }
}
