package biblioteca.modelo;

import java.time.LocalDate;

public class Prestamo {
    private int id;
    private final Socio socio;
    private final Libro libro;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion; // null = todavía sin devolver

    /** Para un préstamo NUEVO: sin id (lo asigna la base) y sin devolución. */
    public Prestamo(Socio socio, Libro libro, LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
    }

    /** Para un préstamo que ya existe: lo usa el DAO al leer una fila de la tabla. */
    public Prestamo(int id, Socio socio, Libro libro, LocalDate fechaPrestamo,
                    LocalDate fechaVencimiento, LocalDate fechaDevolucion) {
        this.id = id;
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Socio getSocio() { return socio; }
    public Libro getLibro() { return libro; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    /** Un préstamo está devuelto cuando tiene fecha de devolución. */
    public boolean estaDevuelto() {
        return fechaDevolucion != null;
    }

    /** RN-07: vencido = sin devolver y con la fecha de vencimiento ya superada (hoy). */
    public boolean estaVencido() {
        return estaVencido(LocalDate.now());
    }

    /** Misma regla, pero con la fecha "de hoy" como parámetro: permite probarla en tests. */
    public boolean estaVencido(LocalDate hoy) {
        return !estaDevuelto() && hoy.isAfter(fechaVencimiento);
    }

    @Override
    public String toString() {
        return "Préstamo N° " + id + " | Socio: " + socio.getNombre() + " " + socio.getApellido()
                + " (N° " + socio.getNumeroSocio() + ") | Libro: \"" + libro.getTitulo() + "\""
                + " | Prestado: " + fechaPrestamo + " | Vence: " + fechaVencimiento
                + " | Devuelto: " + (fechaDevolucion == null ? "-" : fechaDevolucion)
                + " | Estado: " + (estaDevuelto() ? "Devuelto" : (estaVencido() ? "Vencido" : "Vigente"));
    }
}