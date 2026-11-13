package modelo;

import java.time.LocalDate;

public interface Perecedero {

    LocalDate getFechaVencimiento();

    default boolean estaVencido(LocalDate fechaOperacion) {
        if (fechaOperacion == null) {
            throw new IllegalArgumentException(
                    "La fecha de operacion es obligatoria.");
        }
        return fechaOperacion.isAfter(getFechaVencimiento());
    }
}
