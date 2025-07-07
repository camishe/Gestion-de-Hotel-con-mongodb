package Modelo;
import org.bson.Document;

public class Habitacion {
    private String id;
    private String numero;
    private String tipo;
    private boolean ocupada;
    private double precio;

    public Habitacion() {}

    public Habitacion(String id, String numero, String tipo, boolean ocupada, double precio) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.ocupada = ocupada;
        this.precio = precio;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    // Conversión a Document para MongoDB
    public Document toDocument() {
        return new Document("_id", id)
                .append("numero", numero)
                .append("tipo", tipo)
                .append("ocupada", ocupada)
                .append("precio", precio);
    }

    // Conversión desde Document
    public static Habitacion fromDocument(Document doc) {
        return new Habitacion(
            doc.getString("_id"),
            doc.getString("numero"),
            doc.getString("tipo"),
            doc.getBoolean("ocupada", false),
            doc.getDouble("precio")
        );
    }
}
