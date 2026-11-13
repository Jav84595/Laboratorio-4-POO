package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Lote implements Almacenable, Comparable<Lote> {

    private final String codigo;
    private final String productor;
    private final LocalDate fechaIngreso;
    private final double pesoRecibido;
    private final Calidad calidad;
    private double pesoDisponible;
    private EstadoLote estado;

    protected Lote(String codigo, String productor, LocalDate fechaIngreso,
                   double pesoRecibido, Calidad calidad) {
        this.codigo = validarTexto(codigo, "El codigo");
        this.productor = validarTexto(productor, "El productor");
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
        }
        if (!Double.isFinite(pesoRecibido) || pesoRecibido <= 0) {
            throw new IllegalArgumentException("El peso recibido debe ser positivo y finito.");
        }
        if (calidad == null) {
            throw new IllegalArgumentException("La calidad es obligatoria.");
        }
        this.fechaIngreso = fechaIngreso;
        this.pesoRecibido = pesoRecibido;
        this.calidad = calidad;
        this.pesoDisponible = pesoRecibido;
        this.estado = EstadoLote.DISPONIBLE;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacio.");
        }
        return valor.trim();
    }

    public String getCodigo() { return codigo; }
    public String getProductor() { return productor; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public double getPesoRecibido() { return pesoRecibido; }
    public Calidad getCalidad() { return calidad; }
    public double getPesoDisponible() { return pesoDisponible; }
    public EstadoLote getEstado() { return estado; }

    public abstract String getCategoria();

    public abstract double calcularPago();

    @Override
    public abstract TipoAlmacenamiento getTipoAlmacenamientoRequerido();

    public LocalDate getFechaVencimiento() {
        return null;
    }

    public final boolean estaVencido(LocalDate fechaOperacion) {
        if (fechaOperacion == null) {
            throw new IllegalArgumentException("La fecha de operacion es obligatoria.");
        }
        LocalDate vencimiento = getFechaVencimiento();
        return vencimiento != null && fechaOperacion.isAfter(vencimiento);
    }

    public final double getCostoPorKilogramo() {
        return calcularPago() / pesoRecibido;
    }

    final void descontarPeso(double kilogramos) {
        if (estado != EstadoLote.DISPONIBLE) {
            throw new IllegalStateException("El lote no esta disponible.");
        }
        if (!Double.isFinite(kilogramos) || kilogramos <= 0) {
            throw new IllegalArgumentException("Los kilogramos deben ser positivos y finitos.");
        }
        if (kilogramos > pesoDisponible) {
            throw new IllegalArgumentException("El lote no tiene suficientes kilogramos.");
        }
        pesoDisponible = BigDecimal.valueOf(pesoDisponible)
                .subtract(BigDecimal.valueOf(kilogramos)).doubleValue();
        if (pesoDisponible == 0) {
            estado = EstadoLote.AGOTADO;
        }
    }

    final double descartar(LocalDate fechaOperacion) {
        if (estado != EstadoLote.DISPONIBLE) {
            throw new IllegalStateException("El lote no esta disponible.");
        }
        if (!estaVencido(fechaOperacion)) {
            throw new IllegalStateException("El lote no esta vencido.");
        }
        double perdida = pesoDisponible * getCostoPorKilogramo();
        if (!Double.isFinite(perdida) || perdida < 0) {
            throw new IllegalArgumentException("La perdida del lote no es valida.");
        }
        pesoDisponible = 0;
        estado = EstadoLote.DESCARTADO;
        return perdida;
    }

    @Override
    public final int compareTo(Lote otro) {
        if (otro == null) {
            throw new NullPointerException("El lote a comparar es obligatorio.");
        }
        LocalDate vencimiento = getFechaVencimiento();
        LocalDate otroVencimiento = otro.getFechaVencimiento();

        if (vencimiento != null && otroVencimiento == null) return -1;
        if (vencimiento == null && otroVencimiento != null) return 1;

        int resultado;
        if (vencimiento != null) {
            resultado = vencimiento.compareTo(otroVencimiento);
        } else {
            resultado = fechaIngreso.compareTo(otro.fechaIngreso);
        }
        if (resultado != 0) return resultado;

        resultado = calidad.compareTo(otro.calidad);
        if (resultado != 0) return resultado;

        return codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString() {
        LocalDate vencimiento = getFechaVencimiento();
        return String.format(java.util.Locale.US,
                "Lote %s | Categoria: %s | Productor: %s | Ingreso: %s | "
                + "Calidad: %s | Recibido: %.2f kg | Disponible: %.2f kg | "
                + "Estado: %s | Vencimiento: %s",
                codigo, getCategoria(), productor, fechaIngreso, calidad,
                pesoRecibido, pesoDisponible, estado,
                vencimiento == null ? "No vence" : vencimiento.toString());
    }
}
