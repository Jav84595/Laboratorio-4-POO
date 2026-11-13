package modelo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Despacho {

    private final Pedido pedido;
    private final LocalDate fecha;
    private final Map<String, Double> kilogramosPorLote;
    private final double totalCobrado;

    Despacho(Pedido pedido, LocalDate fecha,
             Map<String, Double> kilogramosPorLote, double totalCobrado) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del despacho es obligatoria.");
        }
        if (kilogramosPorLote == null || kilogramosPorLote.isEmpty()) {
            throw new IllegalArgumentException("El despacho debe incluir al menos un lote.");
        }
        if (!Double.isFinite(totalCobrado) || totalCobrado <= 0) {
            throw new IllegalArgumentException("El total cobrado debe ser positivo y finito.");
        }
        Map<String, Double> detalle = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entrada : kilogramosPorLote.entrySet()) {
            String codigo = entrada.getKey();
            Double kilogramos = entrada.getValue();
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException("El codigo del lote no puede estar vacio.");
            }
            if (kilogramos == null || !Double.isFinite(kilogramos) || kilogramos <= 0) {
                throw new IllegalArgumentException("Los kilogramos deben ser positivos y finitos.");
            }
            detalle.put(codigo, kilogramos);
        }
        this.pedido = pedido;
        this.fecha = fecha;
        this.kilogramosPorLote = Collections.unmodifiableMap(detalle);
        this.totalCobrado = totalCobrado;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Map<String, Double> getKilogramosPorLote() {
        return kilogramosPorLote;
    }

    public double getTotalCobrado() {
        return totalCobrado;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US,
                "Fecha: %s | %s | Total cobrado: Q%.2f",
                fecha, pedido, totalCobrado);
    }
}
