package Modelo;
import org.bson.Document;

public class Cliente {
    private String id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;

    public Cliente() {}

    public Cliente(String id, String nombre, String apellido, String cedula, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // Conversión a Document para MongoDB
    public Document toDocument() {
        return new Document("_id", id)
                .append("nombre", nombre)
                .append("apellido", apellido)
                .append("cedula", cedula)
                .append("telefono", telefono);
    }

    // Conversión desde Document
    public static Cliente fromDocument(Document doc) {
        return new Cliente(
            doc.getString("_id"),
            doc.getString("nombre"),
            doc.getString("apellido"),
            doc.getString("cedula"),
            doc.getString("telefono")
        );
    }
}
