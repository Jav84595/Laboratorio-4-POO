package controlador;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import modelo.AcopioGTModel;
import modelo.Bodega;
import modelo.CafeOrganico;
import modelo.CafePergamino;
import modelo.Calidad;
import modelo.Cardamomo;
import modelo.Despacho;
import modelo.Hortaliza;
import modelo.Lote;
import modelo.Pedido;
import modelo.QuesoFresco;
import modelo.TipoAlmacenamiento;
import vista.AcopioGTView;

public class AcopioGTController {

    private final AcopioGTModel model;
    private final AcopioGTView view;

    public AcopioGTController(AcopioGTModel model, AcopioGTView view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException("El modelo y la vista son obligatorios.");
        }
        this.model = model;
        this.view = view;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            try {
                view.mostrarMenu(model.getFechaOperacion());
                int opcion = view.pedirEntero("Seleccione una opcion: ");
                switch (opcion) {
                    case 1: preliquidarLote(); break;
                    case 2: recibirLote(); break;
                    case 3: registrarBodega(); break;
                    case 4: despacharPedido(); break;
                    case 5: avanzarFecha(); break;
                    case 6: consultarLotes(); break;
                    case 7: consultarBodegas(); break;
                    case 8: mostrarReportes(); break;
                    case 9: consultarDespachos(); break;
                    case 10: continuar = false; break;
                    default: view.mostrarError("Opcion no disponible.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                view.mostrarError(e.getMessage());
            } catch (DateTimeException e) {
                view.mostrarError("La fecha esta fuera del rango permitido.");
            } catch (NoSuchElementException e) {
                view.mostrarMensaje("No hay mas datos de entrada.");
                continuar = false;
            }
        }
        view.mostrarMensaje("Programa finalizado.");
    }

    private Lote solicitarLote() {
        view.mostrarMensaje("Categorias:");
        view.mostrarMensaje("1. Cafe en pergamino");
        view.mostrarMensaje("2. Cafe organico");
        view.mostrarMensaje("3. Cardamomo");
        view.mostrarMensaje("4. Hortaliza");
        view.mostrarMensaje("5. Queso fresco");
        int tipo = view.pedirEntero("Categoria: ");
        if (tipo < 1 || tipo > 5) {
            throw new IllegalArgumentException("Categoria no valida.");
        }

        String codigo = view.pedirTexto("Codigo del lote: ");
        String productor = view.pedirTexto("Productor: ");
        double peso = view.pedirDouble("Peso recibido en kilogramos: ");
        Calidad calidad = solicitarCalidad();
        LocalDate ingreso = model.getFechaOperacion();

        switch (tipo) {
            case 1:
                return new CafePergamino(codigo, productor, ingreso, peso, calidad,
                        view.pedirDouble("Humedad en porcentaje: "));
            case 2:
                double humedad = view.pedirDouble("Humedad en porcentaje: ");
                String entidad = view.pedirTexto("Entidad certificadora: ");
                LocalDate validez = view.pedirFecha("Fecha de validez del sello");
                return new CafeOrganico(codigo, productor, ingreso, peso, calidad,
                        humedad, entidad, validez);
            case 3:
                return new Cardamomo(codigo, productor, ingreso, peso, calidad,
                        view.pedirDouble("Porcentaje de grano verde: "));
            case 4:
                return new Hortaliza(codigo, productor, ingreso, peso, calidad,
                        view.pedirEntero("Vida util en dias: "));
            default:
                return new QuesoFresco(codigo, productor, ingreso, peso, calidad,
                        view.pedirDouble("Porcentaje de grasa: "));
        }
    }

    private Calidad solicitarCalidad() {
        int opcion = view.pedirEntero("Calidad (1. A, 2. B, 3. C): ");
        if (opcion < 1 || opcion > 3) {
            throw new IllegalArgumentException("Calidad no valida.");
        }
        return Calidad.values()[opcion - 1];
    }

    private String solicitarCategoriaPedido() {
        List<String> categorias = model.getCategorias();
        for (int i = 0; i < categorias.size(); i++) {
            view.mostrarMensaje((i + 1) + ". " + categorias.get(i));
        }
        int opcion = view.pedirEntero("Categoria: ");
        if (opcion < 1 || opcion > categorias.size()) {
            throw new IllegalArgumentException("Categoria no valida.");
        }
        return categorias.get(opcion - 1);
    }

    private void preliquidarLote() {
        Lote lote = solicitarLote();
        view.mostrarMonto("Pago estimado al productor", model.preliquidar(lote));
        LocalDate vencimiento = lote.getFechaVencimiento();
        view.mostrarMensaje("Vencimiento: "
                + (vencimiento == null ? "No vence" : vencimiento.toString()));
        if (view.pedirSiNo("Desea aceptar y recibir el lote? (si/no): ")) {
            String bodega = view.pedirTexto("Codigo de bodega: ");
            view.mostrarMonto("Pago registrado", model.recibirLote(lote, bodega));
            view.mostrarMensaje("Lote recibido correctamente.");
        }
    }

    private void recibirLote() {
        Lote lote = solicitarLote();
        String bodega = view.pedirTexto("Codigo de bodega: ");
        view.mostrarMonto("Pago registrado", model.recibirLote(lote, bodega));
        view.mostrarMensaje("Lote recibido correctamente.");
    }

    private void registrarBodega() {
        String codigo = view.pedirTexto("Codigo de bodega: ");
        double capacidad = view.pedirDouble("Capacidad maxima en kilogramos: ");
        int opcion = view.pedirEntero("Tipo (1. Seca, 2. Camara fria): ");
        if (opcion != 1 && opcion != 2) {
            throw new IllegalArgumentException("Tipo de almacenamiento no valido.");
        }
        TipoAlmacenamiento tipo = opcion == 1
                ? TipoAlmacenamiento.SECO : TipoAlmacenamiento.REFRIGERADO;
        model.registrarBodega(new Bodega(codigo, capacidad, tipo));
        view.mostrarMensaje("Bodega registrada correctamente.");
    }

    private void despacharPedido() {
        String comprador = view.pedirTexto("Comprador: ");
        String categoria = solicitarCategoriaPedido();
        double kilogramos = view.pedirDouble("Kilogramos solicitados: ");
        Pedido pedido = new Pedido(comprador, categoria, kilogramos);
        Despacho despacho = model.despachar(pedido);
        view.mostrarMensaje("Pedido despachado correctamente.");
        mostrarDespacho(despacho);
    }

    private void avanzarFecha() {
        int dias = view.pedirEntero("Cantidad de dias a avanzar: ");
        double perdida = model.avanzarFecha(dias);
        view.mostrarMensaje("Nueva fecha de operacion: " + model.getFechaOperacion());
        view.mostrarMonto("Perdida por lotes vencidos retirados", perdida);
    }

    private void consultarLotes() {
        List<Lote> lotes = model.getLotes();
        if (lotes.isEmpty()) {
            view.mostrarMensaje("No hay lotes registrados.");
        }
        for (Lote lote : lotes) {
            view.mostrarMensaje(lote.toString());
        }
    }

    private void consultarBodegas() {
        if (model.getBodegas().isEmpty()) {
            view.mostrarMensaje("No hay bodegas registradas.");
        }
        for (Bodega bodega : model.getBodegas()) {
            view.mostrarMensaje(bodega.toString());
            if (bodega.getCantidadLotes() == 0) {
                view.mostrarMensaje("Sin lotes almacenados.");
            }
            for (Lote lote : bodega) {
                view.mostrarMensaje(lote.toString());
            }
        }
    }

    private void mostrarReportes() {
        view.mostrarMensaje("\nExistencias por categoria:");
        Map<String, Double> existencias = model.getExistenciasPorCategoria();
        for (String categoria : model.getCategorias()) {
            view.mostrarKilogramos(categoria, existencias.get(categoria));
            for (Lote lote : model.getLotesDisponibles(categoria)) {
                view.mostrarMensaje(lote.toString());
            }
        }

        view.mostrarMensaje("\nLotes que vencen desde hoy hasta los proximos 3 dias:");
        List<Lote> proximos = model.getLotesProximosAVencer();
        if (proximos.isEmpty()) {
            view.mostrarMensaje("No hay lotes proximos a vencer.");
        }
        for (Lote lote : proximos) {
            view.mostrarMensaje(lote.toString());
        }

        view.mostrarMensaje("\nMovimientos por categoria:");
        for (String categoria : model.getCategorias()) {
            view.mostrarMensaje(categoria + ":");
            view.mostrarMonto("Pagado a productores", model.getPagosPorCategoria().get(categoria));
            view.mostrarMonto("Cobrado en despachos", model.getCobrosPorCategoria().get(categoria));
            view.mostrarMonto("Perdidas por vencimiento", model.getPerdidasPorCategoria().get(categoria));
        }
        view.mostrarMonto("Total pagado a productores", model.getTotalPagado());
        view.mostrarMonto("Total cobrado en despachos", model.getTotalCobrado());
        view.mostrarMonto("Total de perdidas por vencimiento", model.getTotalPerdidas());
    }

    private void consultarDespachos() {
        if (model.getDespachos().isEmpty()) {
            view.mostrarMensaje("No hay despachos registrados.");
        }
        for (Despacho despacho : model.getDespachos()) {
            mostrarDespacho(despacho);
        }
    }

    private void mostrarDespacho(Despacho despacho) {
        view.mostrarMensaje(despacho.toString());
        for (Map.Entry<String, Double> entrada : despacho.getKilogramosPorLote().entrySet()) {
            view.mostrarKilogramos("Lote " + entrada.getKey(), entrada.getValue());
        }
    }
}
