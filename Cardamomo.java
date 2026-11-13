package modelo;

import java.time.LocalDate;

public class Cardamomo extends Lote implements Perecedero {

    private final double porcentajeGranoVerde;

    public Cardamomo(String codigo, String productor, LocalDate fechaIngreso,
                     double pesoRecibido, Calidad calidad, double porcentajeGranoVerde) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        if (!Double.isFinite(porcentajeGranoVerde)
                || porcentajeGranoVerde < 0 || porcentajeGranoVerde > 100) {
            throw new IllegalArgumentException(
                    "El porcentaje de grano verde debe estar entre 0 y 100.");
        }
        this.porcentajeGranoVerde = porcentajeGranoVerde;
    }

    public double getPorcentajeGranoVerde() {
        return porcentajeGranoVerde;
    }

    @Override
    public String getCategoria() {
        return "Cardamomo";
    }

    @Override
    public double calcularPago() {
        double tarifa;
        switch (getCalidad()) {
            case A: tarifa = 60.0; break;
            case B: tarifa = 48.0; break;
            default: tarifa = 35.0;
        }
        double pagoBase = getPesoRecibido() * tarifa;
        return porcentajeGranoVerde >= 85 ? pagoBase * 1.10 : pagoBase;
    }

    @Override
    public LocalDate getFechaVencimiento() {
        return getFechaIngreso().plusDays(180);
    }

    @Override
    public TipoAlmacenamiento getTipoAlmacenamientoRequerido() {
        return TipoAlmacenamiento.SECO;
    }
}
