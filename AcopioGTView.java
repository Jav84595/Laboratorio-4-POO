package vista;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class AcopioGTView {

    private final Scanner scanner;

    public AcopioGTView() {
        scanner = new Scanner(System.in);
    }

    public void mostrarMenu(LocalDate fechaOperacion) {
        System.out.println("\nAcopioGT - Menu principal");
        System.out.println("Fecha de operacion: " + fechaOperacion);
        System.out.println("1. Pre-liquidar un lote");
        System.out.println("2. Recibir un lote");
        System.out.println("3. Registrar bodega");
        System.out.println("4. Registrar pedido y despachar");
        System.out.println("5. Avanzar fecha de operacion");
        System.out.println("6. Consultar lotes");
        System.out.println("7. Consultar bodegas y su contenido");
        System.out.println("8. Generar reportes");
        System.out.println("9. Consultar despachos");
        System.out.println("10. Salir");
    }

    public String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public int pedirEntero(String mensaje) {
        String entrada = pedirTexto(mensaje);
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un numero entero valido.");
        }
    }

    public double pedirDouble(String mensaje) {
        String entrada = pedirTexto(mensaje);
        try {
            double valor = Double.parseDouble(entrada);
            if (!Double.isFinite(valor)) {
                throw new IllegalArgumentException("Debe ingresar un numero finito.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Debe ingresar un numero valido; use punto para los decimales.");
        }
    }

    public LocalDate pedirFecha(String mensaje) {
        String entrada = pedirTexto(mensaje + " (AAAA-MM-DD): ");
        try {
            return LocalDate.parse(entrada);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Debe ingresar una fecha valida con formato AAAA-MM-DD.");
        }
    }

    public boolean pedirSiNo(String mensaje) {
        String respuesta = pedirTexto(mensaje);
        if (respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si")
                || respuesta.equalsIgnoreCase("sí")) {
            return true;
        }
        if (respuesta.equalsIgnoreCase("n") || respuesta.equalsIgnoreCase("no")) {
            return false;
        }
        throw new IllegalArgumentException("Debe responder si o no.");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("Error: " + mensaje);
    }

    public void mostrarMonto(String concepto, double monto) {
        System.out.printf(Locale.US, "%s: Q%.2f%n", concepto, monto);
    }

    public void mostrarKilogramos(String concepto, double kilogramos) {
        System.out.printf(Locale.US, "%s: %.2f kg%n", concepto, kilogramos);
    }
}
