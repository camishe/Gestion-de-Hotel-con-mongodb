package Controlador;

import Modelo.*;
import Vista.VentanaPrinciapl;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;

public class ControladorVentanaPrincipal {
    private final VentanaPrinciapl vista;
    private final MongoCRUD crud;
    private final ConexionBD conexion;

    public ControladorVentanaPrincipal(VentanaPrinciapl vista) {
        this.vista = vista;
        this.conexion = new ConexionBD();
        this.crud = new MongoCRUD(conexion);
        inicializarHabitaciones(); // <-- Inicializa las 20 habitaciones si no existen
        inicializarEventos();
        cargarHabitaciones();
    }

    // Inicializa 20 habitaciones si la colección está vacía
    private void inicializarHabitaciones() {
        List<Document> habitaciones = crud.listarTodos("habitaciones");
        if (habitaciones.size() < 20) {
            crud.listarTodos("habitaciones").forEach(doc -> crud.eliminarPorId("habitaciones", doc.getString("_id")));
            for (int i = 1; i <= 20; i++) {
                String id = java.util.UUID.randomUUID().toString();
                String numero = String.format("%03d", i);
                String tipo = (i <= 5) ? "Suite" : (i <= 12) ? "Doble" : "Simple";
                double precio = (i <= 5) ? 120.0 : (i <= 12) ? 80.0 : 50.0;
                Habitacion h = new Habitacion(id, numero, tipo, false, precio);
                crud.insertar("habitaciones", h.toDocument());
            }
        }
    }

    private void inicializarEventos() {
        vista.BtnCheckin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarCheckin();
            }
        });
        vista.BtnLimpiarCampos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCamposCheckin();
            }
        });
        vista.jButton2.addActionListener(new ActionListener() { // Botón Anular Reserva
            @Override
            public void actionPerformed(ActionEvent e) {
                anularReserva();
            }
        });
    }

    // Cargar habitaciones en tablas y ComboBox
    private void cargarHabitaciones() {
        DefaultTableModel modeloDisp = (DefaultTableModel) vista.TablaHabitacionesDisponibles.getModel();
        DefaultTableModel modeloOcup = (DefaultTableModel) vista.TablaHabitacionesOcupadas.getModel();
        modeloDisp.setRowCount(0);
        modeloOcup.setRowCount(0);
        vista.CmboxHabitaciones.removeAllItems();
        List<Document> habitaciones = crud.listarTodos("habitaciones");
        for (Document doc : habitaciones) {
            Habitacion h = Habitacion.fromDocument(doc);
            String display = h.getNumero() + " - " + h.getTipo() + " - $" + h.getPrecio();
            if (!h.isOcupada()) {
                modeloDisp.addRow(new Object[]{h.getNumero()});
                vista.CmboxHabitaciones.addItem(display);
            } else {
                // Buscar reserva activa para la habitación
                List<Document> reservas = crud.listarTodos("reservas");
                Document reserva = reservas.stream().filter(r -> h.getId().equals(r.getString("idHabitacion")) && r.get("fechaSalida") == null).findFirst().orElse(null);
                String cliente = "";
                if (reserva != null) {
                    Document cli = crud.buscarPorId("clientes", reserva.getString("idCliente"));
                    if (cli != null) cliente = cli.getString("nombre") + " " + cli.getString("apellido");
                }
                modeloOcup.addRow(new Object[]{h.getNumero(), cliente});
            }
        }
        // Si no hay habitaciones disponibles, deshabilita el ComboBox
        vista.CmboxHabitaciones.setEnabled(vista.CmboxHabitaciones.getItemCount() > 0);
    }

    // Check-in: valida, inserta cliente y reserva, actualiza habitación
    private void realizarCheckin() {
        String nombre = vista.TxtNombre.getText().trim();
        String apellido = vista.TxtApellido.getText().trim();
        String cedula = vista.TxtCedula.getText().trim();
        String telefono = vista.TxtTelefono.getText().trim();
        String habitacionDisplay = (String) vista.CmboxHabitaciones.getSelectedItem();
        // Validaciones de campos vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || habitacionDisplay == null) {
            JOptionPane.showMessageDialog(vista, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Extraer número de habitación del display
        String habitacion = habitacionDisplay.split(" - ")[0];
        // Validar cédula y teléfono: 10 dígitos numéricos
        if (!cedula.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(vista, "La cédula debe tener exactamente 10 dígitos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!telefono.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(vista, "El teléfono debe tener exactamente 10 dígitos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validar unicidad de cédula y teléfono
        List<Document> clientes = crud.listarTodos("clientes");
        boolean cedulaExiste = clientes.stream().anyMatch(c -> cedula.equals(c.getString("cedula")));
        boolean telefonoExiste = clientes.stream().anyMatch(c -> telefono.equals(c.getString("telefono")));
        if (cedulaExiste) {
            JOptionPane.showMessageDialog(vista, "La cédula ya está registrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (telefonoExiste) {
            JOptionPane.showMessageDialog(vista, "El teléfono ya está registrado a otro cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Crear cliente
        String idCliente = UUID.randomUUID().toString();
        Cliente cli = new Cliente(idCliente, nombre, apellido, cedula, telefono);
        crud.insertar("clientes", cli.toDocument());
        // Buscar id de la habitación
        Document docHab = crud.listarTodos("habitaciones").stream().filter(d -> habitacion.equals(d.getString("numero"))).findFirst().orElse(null);
        if (docHab == null) {
            JOptionPane.showMessageDialog(vista, "Habitación no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String idHab = docHab.getString("_id");
        // Crear reserva
        String idReserva = UUID.randomUUID().toString();
        Reserva reserva = new Reserva(idReserva, idCliente, idHab, new java.util.Date(), null, docHab.getDouble("precio"));
        crud.insertar("reservas", reserva.toDocument());
        // Marcar habitación como ocupada
        crud.actualizarPorId("habitaciones", idHab, new Document("$set", new Document("ocupada", true)));
        cargarHabitaciones();
        mostrarFactura(cli, docHab, reserva);
        limpiarCamposCheckin();
    }

    // Mostrar factura con JOptionPane
    private void mostrarFactura(Cliente cli, Document hab, Reserva reserva) {
        StringBuilder sb = new StringBuilder();
        sb.append("Factura de Check-in\n");
        sb.append("Cliente: ").append(cli.getNombre()).append(" ").append(cli.getApellido()).append("\n");
        sb.append("Cédula: ").append(cli.getCedula()).append("\n");
        sb.append("Habitación: ").append(hab.getString("numero")).append(" (Tipo: ").append(hab.getString("tipo")).append(")\n");
        sb.append("Precio: $").append(hab.getDouble("precio")).append("\n");
        sb.append("Fecha ingreso: ").append(reserva.getFechaIngreso()).append("\n");
        JOptionPane.showMessageDialog(vista, sb.toString(), "Factura", JOptionPane.INFORMATION_MESSAGE);
    }

    // Limpiar campos del formulario de check-in
    private void limpiarCamposCheckin() {
        vista.TxtNombre.setText("");
        vista.TxtApellido.setText("");
        vista.TxtCedula.setText("");
        vista.TxtTelefono.setText("");
        vista.CmboxHabitaciones.setSelectedIndex(-1);
    }

    // Anular reserva por cédula
    private void anularReserva() {
        String cedula = vista.TxtBuscarCedulaAnular.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese su cédula.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Buscar cliente
        List<Document> clientes = crud.listarTodos("clientes");
        Document cliente = clientes.stream().filter(c -> cedula.equals(c.getString("cedula"))).findFirst().orElse(null);
        if (cliente == null) {
            JOptionPane.showMessageDialog(vista, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Buscar reserva activa
        List<Document> reservas = crud.listarTodos("reservas");
        Document reserva = reservas.stream().filter(r -> cliente.getString("_id").equals(r.getString("idCliente")) && r.get("fechaSalida") == null).findFirst().orElse(null);
        if (reserva == null) {
            JOptionPane.showMessageDialog(vista, "No se encontró una reserva activa para este cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Liberar habitación
        String idHab = reserva.getString("idHabitacion");
        crud.actualizarPorId("habitaciones", idHab, new Document("$set", new Document("ocupada", false)));
        // Marcar reserva como finalizada (fecha de salida)
        crud.actualizarPorId("reservas", reserva.getString("_id"), new Document("$set", new Document("fechaSalida", new java.util.Date())));
        cargarHabitaciones();
        JOptionPane.showMessageDialog(vista, "Reserva anulada correctamente.", "Anulación", JOptionPane.INFORMATION_MESSAGE);
        vista.TxtBuscarCedulaAnular.setText("");
    }
}
