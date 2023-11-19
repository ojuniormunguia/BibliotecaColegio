import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.sql.*;

public class ProfesorView extends JFrame{
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTable tablaDisponibles;
    private JButton rentar;
    private JTable tablaMultas;
    private JTable tablaPrestados;
    private JButton regresarBoton;
    private JButton cerrarSesiónButton;
    private JPanel upperMenu;
    private JTextField buscarDisp;
    private JTextField buscarPrestados;
    private JTextField buscarMora;
    private JLabel bienvenidoUserLabel;
    private String userID;

    private String tablaActual;

    public int getSelectedTabIndex() {
        return tabbedPane1.getSelectedIndex();
    } //En que pestaña está

    public ProfesorView(String userID) {
        this.userID = String.valueOf(userID);
        int userIDInt = Integer.parseInt(userID);

        setTitle("Profesor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);



        DefaultTableModel disponiblesTableModel = new DefaultTableModel();
        DefaultTableModel prestamosTableModel = new DefaultTableModel();
        DefaultTableModel multasTableModel = new DefaultTableModel();


        tablaDisponibles.setModel(disponiblesTableModel);
        tablaPrestados.setModel(prestamosTableModel);
        tablaMultas.setModel(multasTableModel);




        cargarProcedimiento("Disponibles", disponiblesTableModel,"");
        tabbedPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (tabbedPane1.getSelectedIndex()) {

                    case 0:
                        cargarProcedimiento("Disponibles", disponiblesTableModel,"");
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    case 1:
                        cargarProcedimiento("prestamos", prestamosTableModel, userID);
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    case 2:
                        cargarProcedimiento("CalculateUserPenalty", multasTableModel, userID);
                        System.out.println(tabbedPane1.getSelectedIndex());
                        break;
                    default:
                        cargarProcedimiento("Disponibles", disponiblesTableModel,"");
                        System.out.println("Error: No se seleccionó nada");
                        break;
                }

                super.mouseClicked(e);
            }
        });
        setVisible(true);
        add(panel1);





        rentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaDisponibles.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ProfesorView.this, "No se seleccionó ningún ejemplar", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String daysInput = JOptionPane.showInputDialog(ProfesorView.this, "Ingrese el número de días para alquilar:");


                if (daysInput == null || daysInput.trim().isEmpty()) {
                    return;
                }

                try {
                    int daysToRent = Integer.parseInt(daysInput);
                    if (daysToRent <= 0) {
                        JOptionPane.showMessageDialog(ProfesorView.this, "Ingrese un número de días válido", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String ejemplar = tablaDisponibles.getValueAt(selectedRow, 0).toString();
                    String usuario = userID;
                    String paraQuery = usuario + "," + ejemplar + "," + daysToRent;
                    ejecutarProcedimientos("InitPrestamos", paraQuery);
                    cargarProcedimiento("Disponibles", disponiblesTableModel,"");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ProfesorView.this, "Ingrese un número válido para los días", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        regresarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaPrestados.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ProfesorView.this, "No se seleccionó ningún ejemplar", "Error", JOptionPane.ERROR_MESSAGE);
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

    public String getUserID() {
        return userID;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProfesorView("1");
            }
        });
    }
}
