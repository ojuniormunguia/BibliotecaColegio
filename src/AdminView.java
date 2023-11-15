import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class AdminView extends JFrame {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTable tablaEjemplares;
    private JButton eliminarBoton;
    private JButton editarBoton;
    private JTextField buscarTextField;
    private JButton nuevoButton;
    private JButton nuevoButton1;
    private JButton eliminarButton;
    private JButton editarButton;
    private JTextField textField1;
    private JTable tablaUsuarios;

    public AdminView() {
        setTitle("Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Crear modelos de tabla para cada tabla
        DefaultTableModel ejemplaresTableModel = new DefaultTableModel();
        DefaultTableModel usuariosTableModel = new DefaultTableModel();

        // Establecer modelos en las tablas
        tablaEjemplares.setModel(ejemplaresTableModel);
        tablaUsuarios.setModel(usuariosTableModel);

        // Cargar datos desde la base de datos
        cargarDatos("documentos", ejemplaresTableModel);

        setVisible(true);
        add(panel1);
        tabbedPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatos("usuarios", usuariosTableModel);
                super.mouseClicked(e);
            }
        });
    }

    private void cargarDatos(String tableName, DefaultTableModel tableModel) {
        // Conectar a la base de datos y ejecutar la consulta
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + tableName;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                // Limpiar la tabla antes de agregar nuevos datos
                tableModel.setRowCount(0);
                tableModel.setColumnCount(0); // Clear existing columns

                // Imprimir los nombres de las columnas
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Agregar columnas al modelo de tabla
                for (int i = 1; i <= columnCount; i++) {
                    tableModel.addColumn(metaData.getColumnName(i));
                }

                // Agregar filas a la tabla con los datos de la base de datos
                while (resultSet.next()) {
                    // Usar los nombres reales de las columnas al acceder a los datos
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = resultSet.getString(i);
                    }
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos desde la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminView();
            }
        });
    }
}
