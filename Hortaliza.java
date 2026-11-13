package modelo;

import java.time.LocalDate;

public class Hortaliza extends Lote implements Perecedero {

    private final int vidaUtilDias;

    public Hortaliza(String codigo, String productor, LocalDate fechaIngreso,
                     double pesoRecibido, Calidad calidad, int vidaUtilDias) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        if (vidaUtilDias < 3 || vidaUtilDias > 14) {
            throw new IllegalArgumentException("La vida util debe estar entre 3 y 14 dias.");
        }
        this.vidaUtilDias = vidaUtilDias;
    }

    public int getVidaUtilDias() {
        return vidaUtilDias;
    }

    @Override
    public String getCategoria() {
        return "Hortaliza";
    }

    @Override
    public double calcularPago() {
        double tarifa;
        switch (getCalidad()) {
            case A: tarifa = 4.0; break;
            case B: tarifa = 3.0; break;
            default: tarifa = 2.0;
        }
        return getPesoRecibido() * 0.95 * tarifa;
    }

    @Override
    public LocalDate getFechaVencimiento() {
        return getFechaIngreso().plusDays(vidaUtilDias);
    }

    @Override
    public TipoAlmacenamiento getTipoAlmacenamientoRequerido() {
        return TipoAlmacenamiento.REFRIGERADO;
    }
}
