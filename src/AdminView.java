import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class AdminView extends JFrame implements TableUpdateListener{
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTable tablaEjemplares;
    private JButton eliminarEjemplarBoton;
    private JButton editarEjemplarBoton;
    private JTextField buscarEjemp;
    private JButton nuevoButton;
    private JButton nuevoButton1;
    private JButton eliminarUsuarioButton;
    private JButton editarUsuarioButton;
    private JTextField buscarUsuario;
    private JTable tablaUsuarios;
    private JTable tablaDisponibles;
    private JButton rentar;
    private JTable tablaMultas;
    private JTable tablaPrestados;
    private JButton regresarBoton;
    private JPanel upperMenu;
    private JButton cerrarSesiónButton;
    private JLabel bienvenidoUserLabel;
    private JTextField buscarDisp;
    private JTextField buscarPrestados;
    private JTextField buscarMora;
    private JScrollPane scrollEjemp;
    private JScrollPane scrollUsuarios;
    private JScrollPane scrollDisponibles;
    private JScrollPane scrollPrestados;
    private JScrollPane scrollMora;
    private JPanel panelEjemp;
    private JTable tablaTest;
    private String userID;

    private String tablaActual;

    public int getSelectedTabIndex() {
        return tabbedPane1.getSelectedIndex();
    } //En que pestaña está

    public AdminView(String userID) {
        this.userID = String.valueOf(userID);
        int userIDInt = Integer.parseInt(userID);

        setTitle("Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        panelEjemp = new JPanel(new BorderLayout()); // Initialize panelEjemp with BorderLayout

        DefaultTableModel model = new DefaultTableModel();
        DefaultTableModel ejemplaresTableModel = new DefaultTableModel();
        DefaultTableModel usuariosTableModel = new DefaultTableModel();
        DefaultTableModel disponiblesTableModel = new DefaultTableModel();
        DefaultTableModel prestamosTableModel = new DefaultTableModel();
        DefaultTableModel multasTableModel = new DefaultTableModel();


        tablaEjemplares.setModel(ejemplaresTableModel);
        tablaUsuarios.setModel(usuariosTableModel);
        tablaDisponibles.setModel(disponiblesTableModel);
        tablaPrestados.setModel(prestamosTableModel);
        tablaMultas.setModel(multasTableModel);



        cargarDatos("documentos", ejemplaresTableModel);

        tabbedPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (tabbedPane1.getSelectedIndex()) {
                    case 0:
                        cargarDatos("documentos", ejemplaresTableModel);
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    case 1:
                        cargarDatos("usuarios", usuariosTableModel);
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    case 2:
                        cargarProcedimiento("Disponibles", disponiblesTableModel,"");
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    case 3:
                        cargarProcedimiento("prestamos", prestamosTableModel, userID);
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    case 4:
                        cargarProcedimiento("CalculateUserPenalty", multasTableModel, userID);
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    default:
                        cargarDatos("documentos", ejemplaresTableModel);
                        System.out.println("Error: No se seleccionó nada");
                        break;
                }

                super.mouseClicked(e);
            }
        });

        setVisible(true);
        add(panel1);

        nuevoButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new itemForm(userIDInt, "usuarios", "add", AdminView.this);
            }
        });

        editarUsuarioButton.addActionListener(e -> openItemForm(usuariosTableModel, tablaUsuarios, "usuarios"));

        editarEjemplarBoton.addActionListener(e -> openItemForm(ejemplaresTableModel, tablaEjemplares, "documentos"));

        nuevoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new itemForm(userIDInt, "documentos", "add", AdminView.this);
            }
        });
        eliminarEjemplarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = tablaEjemplares.getSelectedRow();
                    if (selectedRow == -1) {
                        // No row is selected, handle accordingly (show a message, etc.)
                        System.out.println("No se seleccionó nada");
                        return;
                    }

                    String idString = tablaEjemplares.getValueAt(selectedRow, 0).toString();
                    int idSeleccionado = Integer.parseInt(idString);

                    Connection connection = DatabaseConnection.getConnection();

                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM documentos WHERE id = ?");
                    preparedStatement.setInt(1, idSeleccionado);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Item borrado correctamente!");
                        cargarDatos("documentos", ejemplaresTableModel);
                    } else {
                        System.out.println("el ID no existe");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        eliminarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = tablaUsuarios.getSelectedRow();
                    if (selectedRow == -1) {
                        // No row is selected, handle accordingly (show a message, etc.)
                        System.out.println("No se seleccionó nada");
                        return;
                    }

                    String idString = tablaUsuarios.getValueAt(selectedRow, 0).toString();
                    int idSeleccionado = Integer.parseInt(idString);

                    Connection connection = DatabaseConnection.getConnection();

                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM usuarios WHERE id = ?");
                    preparedStatement.setInt(1, idSeleccionado);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Objeto eliminado correctamete!");
                        cargarDatos("usuarios", usuariosTableModel);
                    } else {
                        System.out.println("Este ID no existe");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        rentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaDisponibles.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminView.this, "No se seleccionó ningún ejemplar", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String daysInput = JOptionPane.showInputDialog(AdminView.this, "Ingrese el número de días para alquilar:");


                if (daysInput == null || daysInput.trim().isEmpty()) {
                    return;
                }

                try {
                    int daysToRent = Integer.parseInt(daysInput);
                    if (daysToRent <= 0) {
                        JOptionPane.showMessageDialog(AdminView.this, "Ingrese un número de días válido", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String ejemplar = tablaDisponibles.getValueAt(selectedRow, 0).toString();
                    String usuario = userID;
                    String paraQuery = usuario + "," + ejemplar + "," + daysToRent;
                    ejecutarProcedimientos("InitPrestamos", paraQuery);
                    cargarProcedimiento("Disponibles", disponiblesTableModel,"");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AdminView.this, "Ingrese un número válido para los días", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        regresarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaPrestados.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminView.this, "No se seleccionó ningún ejemplar", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String ejemplar = tablaPrestados.getValueAt(selectedRow, 0).toString();
                ejecutarProcedimientos("DevolverPrestamo", ejemplar);
                cargarProcedimiento("prestamos", prestamosTableModel, userID);
            }
        });
        cerrarSesiónButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new Main().setVisible(true);
                    }
                });
            }
        });
        buscarEjemp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tablaEjemplares.getModel());
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + buscarEjemp.getText()));
                tablaEjemplares.setRowSorter(sorter);

                super.keyReleased(e);
            }
        });
        buscarUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tablaUsuarios.getModel());
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + buscarUsuario.getText()));
                tablaUsuarios.setRowSorter(sorter);

                super.keyReleased(e);
            }
        });
        buscarDisp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tablaDisponibles.getModel());
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + buscarDisp.getText()));
                tablaDisponibles.setRowSorter(sorter);
                super.keyReleased(e);
            }
        });
        buscarPrestados.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tablaPrestados.getModel());
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + buscarPrestados.getText()));
                tablaPrestados.setRowSorter(sorter);
                super.keyReleased(e);
            }
        });
        buscarMora.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tablaMultas.getModel());
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + buscarMora.getText()));
                tablaMultas.setRowSorter(sorter);
                super.keyReleased(e);
            }
        });
    }

    private void openItemForm(DefaultTableModel selectedTableModel, JTable selectedTable, String tablaActual) {
        int selectedRow = selectedTable.getSelectedRow();

        if (selectedRow != -1) {
            try {
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + (tablaActual.equals("usuarios") ? "usuarios" : "documentos"));
                ResultSet resultSet = preparedStatement.executeQuery();

                ResultSetMetaData metaData = resultSet.getMetaData();
                int contadorDeColumnas = metaData.getColumnCount();


                Object[] rowData = new Object[contadorDeColumnas];
                for (int i = 0; i < contadorDeColumnas; i++) {
                    rowData[i] = selectedTableModel.getValueAt(selectedRow, i);
                }

                // Assuming the ID is in the first column (index 0)
                int idSeleccionado = Integer.parseInt(rowData[0].toString());

                // Print or use the selected data
                System.out.println("ID: " + idSeleccionado);

                // Open the itemForm with the selected data
                new itemForm(idSeleccionado, tablaActual, "edit", AdminView.this);
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminView.this, "Error al cargar datos desde la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(AdminView.this, "No row selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarProcedimientos(String Procedimiento, String Id){
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "CALL " + Procedimiento + "(" + Id +")";
            System.out.println(query);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos desde la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cargarProcedimiento(String Procedimiento, DefaultTableModel tableModel, String Id) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "CALL " + Procedimiento + "(" + Id +")";
            System.out.println(query);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);

                ResultSetMetaData metaData = resultSet.getMetaData();
                int contadorDeColumnas = metaData.getColumnCount();

                for (int i = 1; i <= contadorDeColumnas; i++) {
                    tableModel.addColumn(metaData.getColumnName(i));
                }

                while (resultSet.next()) {
                    Object[] rowData = new Object[contadorDeColumnas];
                    for (int i = 1; i <= contadorDeColumnas; i++) {
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

    private void cargarDatos(String tableName, DefaultTableModel tableModel) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + tableName;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);

                ResultSetMetaData metaData = resultSet.getMetaData();
                int contadorDeColumnas = metaData.getColumnCount();

                for (int i = 1; i <= contadorDeColumnas; i++) {
                    tableModel.addColumn(metaData.getColumnName(i));
                }

                while (resultSet.next()) {
                    Object[] rowData = new Object[contadorDeColumnas];
                    for (int i = 1; i <= contadorDeColumnas; i++) {
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

    public void updateTable() {
        // Reload data for the current table
        if (getSelectedTabIndex() == 0) {
            cargarDatos("documentos", (DefaultTableModel) tablaEjemplares.getModel());
        } else if (getSelectedTabIndex() == 1) {
            cargarDatos("usuarios", (DefaultTableModel) tablaUsuarios.getModel());
        }
    }

    public String getUserID() {
        return userID;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminView("1");
            }
        });
    }
}
