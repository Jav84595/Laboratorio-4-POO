package modelo;

public class Pedido {

    private final String comprador;
    private final String categoria;
    private final double kilogramos;

    public Pedido(String comprador, String categoria, double kilogramos) {
        if (comprador == null || comprador.trim().isEmpty()) {
            throw new IllegalArgumentException("El comprador no puede estar vacio.");
        }
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoria no puede estar vacia.");
        }
        if (!Double.isFinite(kilogramos) || kilogramos <= 0) {
            throw new IllegalArgumentException("Los kilogramos deben ser positivos y finitos.");
        }
        this.comprador = comprador.trim();
        this.categoria = categoria.trim();
        this.kilogramos = kilogramos;
    }

    public String getComprador() {
        return comprador;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getKilogramos() {
        return kilogramos;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US,
                "Comprador: %s | Categoria: %s | Solicitado: %.2f kg",
                comprador, categoria, kilogramos);
    }
}
