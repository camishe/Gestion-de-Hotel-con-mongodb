package Modelo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Clase para gestionar la conexión a MongoDB y obtener colecciones.
 */
public class ConexionBD {
    public static final String URI = "mongodb://localhost:27017";
    public static final String NOMBRE_BD = "Hotel"; // Cambia el nombre si lo deseas
    private MongoClient mongoClient;
    private MongoDatabase database;

    // Constructor que inicializa la conexión a la base de datos
    public ConexionBD() {
        conectar();
    }

    private void conectar() {
        try {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase(NOMBRE_BD);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.toString());
        }
    }

    /**
     * Obtiene una colección de la base de datos por su nombre.
     * @param nombreColeccion Nombre de la colección ("habitaciones", "clientes", "reservas", etc.)
     * @return La colección solicitada
     */
    public MongoCollection<Document> getColeccion(String nombreColeccion) {
        return database.getCollection(nombreColeccion);
    }

    // Métodos de acceso rápido para colecciones comunes
    public MongoCollection<Document> getHabitacionesCollection() {
        return getColeccion("habitaciones");
    }
    public MongoCollection<Document> getClientesCollection() {
        return getColeccion("clientes");
    }
    public MongoCollection<Document> getReservasCollection() {
        return getColeccion("reservas");
    }

    // Método para cerrar la conexión a la base de datos
    public void cerrar() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
