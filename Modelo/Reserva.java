package Modelo;
import org.bson.Document;
import java.util.Date;

public class Reserva {
    private String id;
    private String idCliente;
    private String idHabitacion;
    private Date fechaIngreso;
    private Date fechaSalida;
    private double total;

    public Reserva() {}

    public Reserva(String id, String idCliente, String idHabitacion, Date fechaIngreso, Date fechaSalida, double total) {
        this.id = id;
        this.idCliente = idCliente;
        this.idHabitacion = idHabitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.total = total;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }
    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public Date getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Date fechaSalida) { this.fechaSalida = fechaSalida; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // Conversión a Document para MongoDB
    public Document toDocument() {
        return new Document("_id", id)
                .append("idCliente", idCliente)
                .append("idHabitacion", idHabitacion)
                .append("fechaIngreso", fechaIngreso)
                .append("fechaSalida", fechaSalida)
                .append("total", total);
    }

    // Conversión desde Document
    public static Reserva fromDocument(Document doc) {
        return new Reserva(
            doc.getString("_id"),
            doc.getString("idCliente"),
            doc.getString("idHabitacion"),
            doc.getDate("fechaIngreso"),
            doc.getDate("fechaSalida"),
            doc.getDouble("total")
        );
    }
}
