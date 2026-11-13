package modelo;

import java.time.LocalDate;

public class CafePergamino extends Lote {

    private final double humedad;

    public CafePergamino(String codigo, String productor, LocalDate fechaIngreso,
                         double pesoRecibido, Calidad calidad, double humedad) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        if (!Double.isFinite(humedad) || humedad < 8 || humedad > 14) {
            throw new IllegalArgumentException("La humedad debe estar entre 8 y 14 por ciento.");
        }
        this.humedad = humedad;
    }

    public double getHumedad() {
        return humedad;
    }

    @Override
    public String getCategoria() {
        return "Cafe en pergamino";
    }

    @Override
    public double calcularPago() {
        double tarifa;
        switch (getCalidad()) {
            case A: tarifa = 12.0; break;
            case B: tarifa = 9.0; break;
            default: tarifa = 6.0;
        }
        double pagoBase = getPesoRecibido() * tarifa;
        double puntosExceso = Math.ceil(Math.max(0.0, humedad - 12.0));
        return pagoBase * (1.0 - puntosExceso * 0.03);
    }

    @Override
    public TipoAlmacenamiento getTipoAlmacenamientoRequerido() {
        return TipoAlmacenamiento.SECO;
    }
}
