package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Bodega implements Iterable<Lote> {

    private final String codigo;
    private final double capacidadMaxima;
    private final TipoAlmacenamiento tipoAlmacenamiento;
    private final ArrayList<Lote> lotes;

    public Bodega(String codigo, double capacidadMaxima,
                   TipoAlmacenamiento tipoAlmacenamiento) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la bodega no puede estar vacio.");
        }
        if (!Double.isFinite(capacidadMaxima) || capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima debe ser positiva y finita.");
        }
        if (tipoAlmacenamiento == null) {
            throw new IllegalArgumentException("El tipo de almacenamiento es obligatorio.");
        }
        this.codigo = codigo.trim();
        this.capacidadMaxima = capacidadMaxima;
        this.tipoAlmacenamiento = tipoAlmacenamiento;
        this.lotes = new ArrayList<>();
    }

    public String getCodigo() { return codigo; }
    public double getCapacidadMaxima() { return capacidadMaxima; }
    public TipoAlmacenamiento getTipoAlmacenamiento() { return tipoAlmacenamiento; }
    public int getCantidadLotes() { return lotes.size(); }

    public double getKilogramosAlmacenados() {
        return calcularPesoAlmacenado().doubleValue();
    }

    private BigDecimal calcularPesoAlmacenado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Lote lote : lotes) {
            total = total.add(BigDecimal.valueOf(lote.getPesoDisponible()));
        }
        return total;
    }

    public double getCapacidadDisponible() {
        return BigDecimal.valueOf(capacidadMaxima).subtract(calcularPesoAlmacenado())
                .max(BigDecimal.ZERO).doubleValue();
    }

    public boolean esCompatibleCon(Almacenable producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        return tipoAlmacenamiento == producto.getTipoAlmacenamientoRequerido();
    }

    void almacenarLote(Lote lote, LocalDate fechaOperacion) {
        if (lote == null) {
            throw new IllegalArgumentException("El lote es obligatorio.");
        }
        if (fechaOperacion == null) {
            throw new IllegalArgumentException("La fecha de operacion es obligatoria.");
        }
        if (lote.getEstado() != EstadoLote.DISPONIBLE
                || lote.getPesoDisponible() != lote.getPesoRecibido()) {
            throw new IllegalStateException("Solo se pueden almacenar lotes nuevos y disponibles.");
        }
        if (lote.getFechaIngreso().isAfter(fechaOperacion)) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser futura.");
        }
        if (lote.estaVencido(fechaOperacion)) {
            throw new IllegalArgumentException("No se puede almacenar un lote vencido.");
        }
        for (Lote actual : lotes) {
            if (actual.getCodigo().equals(lote.getCodigo())) {
                throw new IllegalArgumentException("El lote ya se encuentra en esta bodega.");
            }
        }
        if (!esCompatibleCon(lote)) {
            throw new IllegalArgumentException("El lote no es compatible con esta bodega.");
        }
        BigDecimal pesoResultante = calcularPesoAlmacenado()
                .add(BigDecimal.valueOf(lote.getPesoRecibido()));
        if (pesoResultante.compareTo(BigDecimal.valueOf(capacidadMaxima)) > 0) {
            throw new IllegalArgumentException("La bodega no tiene capacidad suficiente.");
        }
        lotes.add(lote);
        Collections.sort(lotes);
    }

    @Override
    public Iterator<Lote> iterator() {
        Iterator<Lote> recorrido = lotes.iterator();
        return new Iterator<Lote>() {
            private Lote ultimo;

            @Override
            public boolean hasNext() {
                return recorrido.hasNext();
            }

            @Override
            public Lote next() {
                ultimo = recorrido.next();
                return ultimo;
            }

            @Override
            public void remove() {
                if (ultimo == null) {
                    throw new IllegalStateException("Debe avanzar el recorrido antes de retirar un lote.");
                }
                if (ultimo.getEstado() == EstadoLote.DISPONIBLE) {
                    throw new IllegalStateException("Solo se pueden retirar lotes agotados o descartados.");
                }
                recorrido.remove();
                ultimo = null;
            }
        };
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US,
                "Bodega %s | Tipo: %s | Ocupado: %.2f/%.2f kg | Disponible: %.2f kg | Lotes: %d",
                codigo, tipoAlmacenamiento, getKilogramosAlmacenados(),
                capacidadMaxima, getCapacidadDisponible(), getCantidadLotes());
    }
}
