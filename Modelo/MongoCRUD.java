package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;


public class MongoCRUD {
    private final ConexionBD conexion;

    public MongoCRUD(ConexionBD conexion) {
        this.conexion = conexion;
    }

    // Insertar un documento en la colección
    public void insertar(String nombreColeccion, Document doc) {
        MongoCollection<Document> col = conexion.getColeccion(nombreColeccion);
        col.insertOne(doc);
    }

    // Buscar un documento por ID
    public Document buscarPorId(String nombreColeccion, String id) {
        MongoCollection<Document> col = conexion.getColeccion(nombreColeccion);
        return col.find(Filters.eq("_id", id)).first();
    }

    // Actualizar un documento por ID
    public boolean actualizarPorId(String nombreColeccion, String id, Bson actualizacion) {
        MongoCollection<Document> col = conexion.getColeccion(nombreColeccion);
        UpdateResult res = col.updateOne(Filters.eq("_id", id), actualizacion);
        return res.getModifiedCount() > 0;
    }

    // Eliminar un documento por ID
    public boolean eliminarPorId(String nombreColeccion, String id) {
        MongoCollection<Document> col = conexion.getColeccion(nombreColeccion);
        DeleteResult res = col.deleteOne(Filters.eq("_id", id));
        return res.getDeletedCount() > 0;
    }

    // Listar todos los documentos de una colección
    public List<Document> listarTodos(String nombreColeccion) {
        MongoCollection<Document> col = conexion.getColeccion(nombreColeccion);
        List<Document> lista = new ArrayList<>();
        for (Document doc : col.find()) {
            lista.add(doc);
        }
        return lista;
    }
}
