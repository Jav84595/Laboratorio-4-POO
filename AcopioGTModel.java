package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AcopioGTModel {

    private final ArrayList<Lote> lotes;
    private final ArrayList<Bodega> bodegas;
    private final ArrayList<Despacho> despachos;
    private final Map<String, Double> pagosPorCategoria;
    private final Map<String, Double> cobrosPorCategoria;
    private final Map<String, Double> perdidasPorCategoria;
    private LocalDate fechaOperacion;

    public AcopioGTModel() {
        this(LocalDate.of(2026, 11, 1));
    }

    public AcopioGTModel(LocalDate fechaOperacion) {
        if (fechaOperacion == null) {
            throw new IllegalArgumentException("La fecha de operacion es obligatoria.");
        }
        this.fechaOperacion = fechaOperacion;
        lotes = new ArrayList<>();
        bodegas = new ArrayList<>();
        despachos = new ArrayList<>();
        pagosPorCategoria = new LinkedHashMap<>();
        cobrosPorCategoria = new LinkedHashMap<>();
        perdidasPorCategoria = new LinkedHashMap<>();
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        registrarBodega(new Bodega("B01", 2000.0, TipoAlmacenamiento.SECO));
        registrarBodega(new Bodega("B02", 2000.0, TipoAlmacenamiento.SECO));
        registrarBodega(new Bodega("B03", 1000.0, TipoAlmacenamiento.REFRIGERADO));

        agregarLoteInicial(new CafePergamino("C01", "Ana Lopez", fechaOperacion.minusDays(10),
                500.0, Calidad.A, 13.4), "B01");
        agregarLoteInicial(new CafePergamino("C02", "Luis Perez", fechaOperacion.minusDays(10),
                300.0, Calidad.B, 12.0), "B02");
        agregarLoteInicial(new CafeOrganico("O01", "Maria Ruiz", fechaOperacion.minusDays(8),
                200.0, Calidad.A, 13.4, "Certificadora Verde", fechaOperacion.plusDays(365)), "B01");
        agregarLoteInicial(new CafeOrganico("O02", "Pedro Diaz", fechaOperacion.minusDays(5),
                150.0, Calidad.C, 10.0, "Sello Natural", fechaOperacion.plusDays(180)), "B02");
        agregarLoteInicial(new Cardamomo("CA01", "Sofia Garcia", fechaOperacion.minusDays(179),
                80.0, Calidad.B, 90.0), "B01");
        agregarLoteInicial(new Cardamomo("CA02", "Jose Morales", fechaOperacion.minusDays(20),
                100.0, Calidad.A, 80.0), "B02");
        agregarLoteInicial(new Hortaliza("H01", "Carla Mendez", fechaOperacion.minusDays(6),
                200.0, Calidad.A, 7), "B03");
        agregarLoteInicial(new Hortaliza("H02", "Diego Castro", fechaOperacion.minusDays(2),
                100.0, Calidad.B, 10), "B03");
        agregarLoteInicial(new QuesoFresco("Q01", "Elena Gomez", fechaOperacion.minusDays(20),
                40.0, Calidad.A, 25.0), "B03");
        agregarLoteInicial(new QuesoFresco("Q02", "Mario Reyes", fechaOperacion.minusDays(5),
                60.0, Calidad.B, 30.0), "B03");
    }

    private void agregarLoteInicial(Lote lote, String codigoBodega) {
        if (buscarLote(lote.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un lote con ese codigo.");
        }
        validarPago(lote);
        requerirBodega(codigoBodega).almacenarLote(lote, fechaOperacion);
        lotes.add(lote);
        prepararCategoria(lote.getCategoria());
    }

    private void prepararCategoria(String categoria) {
        pagosPorCategoria.putIfAbsent(categoria, 0.0);
        cobrosPorCategoria.putIfAbsent(categoria, 0.0);
        perdidasPorCategoria.putIfAbsent(categoria, 0.0);
    }

    public LocalDate getFechaOperacion() { return fechaOperacion; }

    public List<Lote> getLotes() {
        ArrayList<Lote> resultado = new ArrayList<>(lotes);
        Collections.sort(resultado);
        return Collections.unmodifiableList(resultado);
    }

    public List<Bodega> getBodegas() {
        return Collections.unmodifiableList(bodegas);
    }

    public List<Despacho> getDespachos() {
        return Collections.unmodifiableList(despachos);
    }

    public List<String> getCategorias() {
        return Collections.unmodifiableList(new ArrayList<>(pagosPorCategoria.keySet()));
    }

    public Lote buscarLote(String codigo) {
        for (Lote lote : lotes) {
            if (lote.getCodigo().equals(codigo)) return lote;
        }
        return null;
    }

    public Bodega buscarBodega(String codigo) {
        for (Bodega bodega : bodegas) {
            if (bodega.getCodigo().equals(codigo)) return bodega;
        }
        return null;
    }

    private Bodega requerirBodega(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de bodega es obligatorio.");
        }
        Bodega bodega = buscarBodega(codigo.trim());
        if (bodega == null) {
            throw new IllegalArgumentException("La bodega no existe.");
        }
        return bodega;
    }

    public void registrarBodega(Bodega bodega) {
        if (bodega == null) {
            throw new IllegalArgumentException("La bodega es obligatoria.");
        }
        if (buscarBodega(bodega.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe una bodega con ese codigo.");
        }
        if (bodega.getCantidadLotes() != 0) {
            throw new IllegalArgumentException("La bodega debe registrarse vacia.");
        }
        bodegas.add(bodega);
    }

    private double validarPago(Lote lote) {
        if (lote == null) {
            throw new IllegalArgumentException("El lote es obligatorio.");
        }
        String categoria = lote.getCategoria();
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoria del lote es obligatoria.");
        }
        double pago = lote.calcularPago();
        double costo = lote.getCostoPorKilogramo();
        if (!Double.isFinite(pago) || pago <= 0 || !Double.isFinite(costo) || costo <= 0) {
            throw new IllegalArgumentException("El pago y el costo por kilogramo deben ser positivos y finitos.");
        }
        return pago;
    }

    public double preliquidar(Lote lote) {
        double pago = validarPago(lote);
        if (buscarLote(lote.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un lote con ese codigo.");
        }
        if (!lote.getFechaIngreso().equals(fechaOperacion)) {
            throw new IllegalArgumentException("Los lotes nuevos deben ingresar en la fecha de operacion.");
        }
        if (lote.getEstado() != EstadoLote.DISPONIBLE
                || lote.getPesoDisponible() != lote.getPesoRecibido()) {
            throw new IllegalStateException("El lote debe ser nuevo y estar disponible.");
        }
        if (lote.estaVencido(fechaOperacion)) {
            throw new IllegalArgumentException("El lote no puede estar vencido.");
        }
        return pago;
    }

    public double recibirLote(Lote lote, String codigoBodega) {
        double pago = preliquidar(lote);
        Bodega bodega = requerirBodega(codigoBodega);
        String categoria = lote.getCategoria();
        validarAcumulacion(pagosPorCategoria, categoria, pago);
        bodega.almacenarLote(lote, fechaOperacion);
        lotes.add(lote);
        prepararCategoria(categoria);
        pagosPorCategoria.put(categoria, pagosPorCategoria.get(categoria) + pago);
        return pago;
    }

    public List<Lote> getExistencias() {
        ArrayList<Lote> resultado = new ArrayList<>();
        for (Bodega bodega : bodegas) {
            for (Lote lote : bodega) {
                if (lote.getEstado() == EstadoLote.DISPONIBLE && !lote.estaVencido(fechaOperacion)) {
                    resultado.add(lote);
                }
            }
        }
        Collections.sort(resultado);
        return Collections.unmodifiableList(resultado);
    }

    public List<Lote> getLotesDisponibles(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoria es obligatoria.");
        }
        ArrayList<Lote> resultado = new ArrayList<>();
        for (Lote lote : getExistencias()) {
            if (lote.getCategoria().equals(categoria.trim())) resultado.add(lote);
        }
        return Collections.unmodifiableList(resultado);
    }

    public Map<String, Double> getExistenciasPorCategoria() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        for (String categoria : getCategorias()) resultado.put(categoria, 0.0);
        for (Lote lote : getExistencias()) {
            resultado.merge(lote.getCategoria(), lote.getPesoDisponible(), Double::sum);
        }
        return Collections.unmodifiableMap(resultado);
    }

    public Despacho despachar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio.");
        }
        for (Despacho despacho : despachos) {
            if (despacho.getPedido() == pedido) {
                throw new IllegalStateException("Este pedido ya fue despachado.");
            }
        }
        Map<Lote, Double> cantidades = new LinkedHashMap<>();
        Map<String, Double> detalle = new LinkedHashMap<>();
        BigDecimal restante = BigDecimal.valueOf(pedido.getKilogramos());
        double total = 0;
        for (Lote lote : getLotesDisponibles(pedido.getCategoria())) {
            if (restante.signum() == 0) break;
            double cantidad = restante.min(BigDecimal.valueOf(lote.getPesoDisponible())).doubleValue();
            cantidades.put(lote, cantidad);
            detalle.put(lote.getCodigo(), cantidad);
            total += cantidad * lote.getCostoPorKilogramo() * 1.35;
            restante = restante.subtract(BigDecimal.valueOf(cantidad));
        }
        if (restante.signum() > 0) {
            throw new IllegalArgumentException("No hay existencias suficientes para completar el pedido.");
        }
        validarAcumulacion(cobrosPorCategoria, pedido.getCategoria(), total);
        Despacho despacho = new Despacho(pedido, fechaOperacion, detalle, total);

        for (Map.Entry<Lote, Double> entrada : cantidades.entrySet()) {
            entrada.getKey().descontarPeso(entrada.getValue());
        }
        retirarLotesAgotados();
        cobrosPorCategoria.merge(pedido.getCategoria(), total, Double::sum);
        despachos.add(despacho);
        return despacho;
    }

    private void retirarLotesAgotados() {
        for (Bodega bodega : bodegas) {
            Iterator<Lote> recorrido = bodega.iterator();
            while (recorrido.hasNext()) {
                if (recorrido.next().getEstado() == EstadoLote.AGOTADO) recorrido.remove();
            }
        }
    }

    public double avanzarFecha(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Debe avanzar al menos un dia.");
        }
        return avanzarFecha(fechaOperacion.plusDays(dias));
    }

    public double avanzarFecha(LocalDate nuevaFecha) {
        if (nuevaFecha == null || !nuevaFecha.isAfter(fechaOperacion)) {
            throw new IllegalArgumentException("La nueva fecha debe ser posterior a la fecha de operacion.");
        }
        Map<String, Double> nuevasPerdidas = new LinkedHashMap<>();
        for (Bodega bodega : bodegas) {
            for (Lote lote : bodega) {
                if (lote.estaVencido(nuevaFecha)) {
                    double perdida = lote.getPesoDisponible() * lote.getCostoPorKilogramo();
                    nuevasPerdidas.merge(lote.getCategoria(), perdida, Double::sum);
                }
            }
        }
        double totalPerdida = sumar(nuevasPerdidas);
        for (Map.Entry<String, Double> entrada : nuevasPerdidas.entrySet()) {
            validarAcumulacion(perdidasPorCategoria, entrada.getKey(), entrada.getValue());
        }
        if (!Double.isFinite(getTotalPerdidas() + totalPerdida)) {
            throw new IllegalArgumentException("El total de perdidas excede el limite permitido.");
        }

        for (Bodega bodega : bodegas) {
            Iterator<Lote> recorrido = bodega.iterator();
            while (recorrido.hasNext()) {
                Lote lote = recorrido.next();
                if (lote.estaVencido(nuevaFecha)) {
                    lote.descartar(nuevaFecha);
                    recorrido.remove();
                }
            }
        }
        for (Map.Entry<String, Double> entrada : nuevasPerdidas.entrySet()) {
            perdidasPorCategoria.merge(entrada.getKey(), entrada.getValue(), Double::sum);
        }
        fechaOperacion = nuevaFecha;
        return totalPerdida;
    }

    public List<Lote> getLotesProximosAVencer() {
        ArrayList<Lote> resultado = new ArrayList<>();
        LocalDate limite = fechaOperacion.plusDays(3);
        for (Lote lote : getExistencias()) {
            LocalDate vencimiento = lote.getFechaVencimiento();
            if (vencimiento != null && !vencimiento.isBefore(fechaOperacion)
                    && !vencimiento.isAfter(limite)) {
                resultado.add(lote);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public Map<String, Double> getPagosPorCategoria() {
        return Collections.unmodifiableMap(pagosPorCategoria);
    }

    public Map<String, Double> getCobrosPorCategoria() {
        return Collections.unmodifiableMap(cobrosPorCategoria);
    }

    public Map<String, Double> getPerdidasPorCategoria() {
        return Collections.unmodifiableMap(perdidasPorCategoria);
    }

    public double getTotalPagado() { return sumar(pagosPorCategoria); }
    public double getTotalCobrado() { return sumar(cobrosPorCategoria); }
    public double getTotalPerdidas() { return sumar(perdidasPorCategoria); }

    private double sumar(Map<String, Double> valores) {
        double total = 0;
        for (double valor : valores.values()) total += valor;
        return total;
    }

    private void validarAcumulacion(Map<String, Double> valores, String categoria, double monto) {
        double acumulado = valores.getOrDefault(categoria, 0.0) + monto;
        if (!Double.isFinite(monto) || monto < 0 || !Double.isFinite(acumulado)
                || !Double.isFinite(sumar(valores) + monto)) {
            throw new IllegalArgumentException("El monto excede el limite permitido.");
        }
    }
}
