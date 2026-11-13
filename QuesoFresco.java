package modelo;

import java.time.LocalDate;

public class QuesoFresco extends Lote implements Perecedero {

    private final double porcentajeGrasa;

    public QuesoFresco(String codigo, String productor, LocalDate fechaIngreso,
                       double pesoRecibido, Calidad calidad, double porcentajeGrasa) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        if (calidad == Calidad.C) {
            throw new IllegalArgumentException("No se acepta queso fresco de calidad C.");
        }
        if (!Double.isFinite(porcentajeGrasa)
                || porcentajeGrasa < 10 || porcentajeGrasa > 40) {
            throw new IllegalArgumentException(
                    "El porcentaje de grasa debe estar entre 10 y 40.");
        }
        this.porcentajeGrasa = porcentajeGrasa;
    }

    public double getPorcentajeGrasa() {
        return porcentajeGrasa;
    }

    @Override
    public String getCategoria() {
        return "Queso fresco";
    }

    @Override
    public double calcularPago() {
        double tarifa = getCalidad() == Calidad.A ? 38.0 : 30.0;
        return getPesoRecibido() * (tarifa - 1.50);
    }

    @Override
    public LocalDate getFechaVencimiento() {
        return getFechaIngreso().plusDays(21);
    }

    @Override
    public TipoAlmacenamiento getTipoAlmacenamientoRequerido() {
        return TipoAlmacenamiento.REFRIGERADO;
    }
}
