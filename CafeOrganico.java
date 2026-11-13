package modelo;

import java.time.LocalDate;

public class CafeOrganico extends CafePergamino {

    private final String entidadCertificadora;
    private final LocalDate fechaValidezSello;

    public CafeOrganico(String codigo, String productor, LocalDate fechaIngreso,
                        double pesoRecibido, Calidad calidad, double humedad,
                        String entidadCertificadora, LocalDate fechaValidezSello) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad, humedad);
        if (entidadCertificadora == null || entidadCertificadora.trim().isEmpty()) {
            throw new IllegalArgumentException("La entidad certificadora es obligatoria.");
        }
        if (fechaValidezSello == null) {
            throw new IllegalArgumentException("La fecha de validez del sello es obligatoria.");
        }
        if (fechaValidezSello.isBefore(fechaIngreso)) {
            throw new IllegalArgumentException("El sello no puede estar vencido en la fecha de ingreso.");
        }
        this.entidadCertificadora = entidadCertificadora.trim();
        this.fechaValidezSello = fechaValidezSello;
    }

    public String getEntidadCertificadora() {
        return entidadCertificadora;
    }

    public LocalDate getFechaValidezSello() {
        return fechaValidezSello;
    }

    @Override
    public String getCategoria() {
        return "Cafe organico";
    }

    @Override
    public double calcularPago() {
        return super.calcularPago() + getPesoRecibido() * 0.75;
    }
}
