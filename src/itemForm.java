import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class itemForm extends JFrame {
    private JPanel panel;
    private JTextField[] textFields;
    private String[] columnNames;
    private ResultSetMetaData metaData;
    private JButton cancelarButton;
    private JButton guardarCambiosButton;
    private JPanel botonesPanel;
    private JPanel panel1;
    private TableUpdateListener tableUpdateListener;
    private int columnCount;



    public itemForm(int id, String tableName, String addOrEdit, TableUpdateListener tableUpdateListener) {
        this.tableUpdateListener = tableUpdateListener;
        setTitle(addOrEdit.equals("add") ? "Añadir " + tableName : "Editar " + tableName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        panel = new JPanel(new GridBagLayout());
        Border border = BorderFactory.createEmptyBorder(2, 50, 2, 50);
        panel.setBorder(border);
        add(panel);

        cargarInfo(id, tableName, addOrEdit);
        setVisible(true);

        cancelarButton.addActionListener(e -> dispose());

        guardarCambiosButton.addActionListener(e -> {
            try (Connection connection = DatabaseConnection.getConnection()) {
                StringBuilder query;

                if (addOrEdit.equals("edit")) {
                    // Editing operation
                    query = new StringBuilder("UPDATE " + tableName + " SET ");
                    int nonNullColumnCount = 0;

                    for (int i = 0; i < textFields.length; i++) {
                        String columnName = columnNames[i];
                        int esAutoGenerado = metaData.isNullable(i + 1);
                        boolean esAutoIncremental = metaData.isAutoIncrement(i + 1);

                        if (esAutoGenerado == 1 || esAutoIncremental == true) {
                            continue;
                        }

                        String columnValue = textFields[i] != null ? textFields[i].getText().trim() : null;

                        if (columnValue != null && !columnValue.isEmpty()) {
                            if (id > 0) {
                                // Editing operation
                                query.append("`").append(columnName).append("` = ?");
                            } else {
                                // Adding operation
                                query.append("`").append(columnName).append("`");
                                switch (columnName) {
                                    case "ejemplares_prestados":
                                        query.append("");
                                        break;
                                    default:
                                        query.append(", ");
                                        break;
                                }


                            }

                            if (i < textFields.length - 1) {
                                switch (columnName) {
                                    case "ejemplares_prestados":
                                        query.append("");
                                        break;
                                    default:
                                        query.append(", ");
                                        break;
                                }
                            }
                            System.out.println("Generated query 1: " + query);

                        } else {
                            System.out.println("Column '" + columnName + "' has a null or empty value.");
                        }
                    }

                    if (id > 0 && nonNullColumnCount >= 0) {
                        // Editing operation
                        System.out.println("Generated query 2: " + query);
                        query.append(" WHERE `id` = " + id);
                        System.out.println("Generated query 3: " + query);

                        // execute the query
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
                            int parameterIndex = 1;

                            for (int i = 0; i < textFields.length; i++) {

                                String columnValue = textFields[i] != null ? textFields[i].getText().trim() : null;

                                if (columnValue != null && !columnValue.isEmpty()) {
                                    preparedStatement.setString(parameterIndex, columnValue);
                                    parameterIndex++;
                                }
                            }

                            preparedStatement.executeUpdate();
                        }
                    } else if (id == 0 && nonNullColumnCount >= 0) {
                        // Adding operation
                        System.out.println("Generated query 2: " + query + ") VALUES (");



                        for (int i = 0; i < textFields.length; i++) {
                            String columnValue = textFields[i] != null ? textFields[i].getText().trim() : null;


                            if (columnValue != null && !columnValue.isEmpty()) {
                                query.append("?");
                                if (i < textFields.length - 1) {
                                    switch (textFields[i].getName()) {
                                        case "ejemplares_prestados":
                                            System.out.println("Genesdadas: " + textFields[i].getName());
                                            query.append("");
                                            break;
                                        default:
                                            query.append(", ");
                                            break;
                                    }
                                }
                            }
                        }

                        query.append(")");

                        // execute the query
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
                            int parameterIndex = 1;

                            for (int i = 0; i < textFields.length; i++) {
                                String columnValue = textFields[i] != null ? textFields[i].getText().trim() : null;

                                if (columnValue != null && !columnValue.isEmpty()) {
                                    preparedStatement.setString(parameterIndex, columnValue);
                                    parameterIndex++;
                                }
                            }

                            preparedStatement.executeUpdate();
                        }
                    } else {
                        System.out.println("No valid columns to update or insert.");
                    }
                } else {
                    // Adding operation
                    query = new StringBuilder("INSERT INTO " + tableName + " (");

                    int nonNullColumnCount = 0;

                    for (int i = 0; i < textFields.length; i++) {
                        String columnName = columnNames[i];
                        int esAutoGenerado = metaData.isNullable(i + 1);
                        boolean esAutoIncremental = metaData.isAutoIncrement(i + 1);

                        if (esAutoGenerado == 1 || esAutoIncremental == true) {
                            continue;
                        }

                        String columnValue = textFields[i] != null ? textFields[i].getText().trim() : null;

                        if (columnValue != null && !columnValue.isEmpty()) {
                            query.append("`").append(columnName).append("`");

                            if (i < textFields.length - 1) {
                                switch (columnName) {
                                    case "ejemplares_prestados":
                                        query.append("");
                                        break;
                                    default:
                                        query.append(", ");
                                        break;
                                }
                            }

                            nonNullColumnCount++;
                        }
                    }

                    query.append(") VALUES (");

                    for (int i = 0; i < nonNullColumnCount; i++) {
                        query.append("?");

                        if (i < nonNullColumnCount - 1) {
                            query.append(", ");
                        }
                    }

                    query.append(")");

                    // execute the query
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
                        int parameterIndex = 1;

                        for (int i = 0; i < textFields.length; i++) {
                            String columnValue = textFields[i] != null ? textFields[i].getText().trim() : null;
                            System.out.println("query: " + query);

                            if (columnValue != null && !columnValue.isEmpty()) {
                                System.out.println("query2: " + query);
                                preparedStatement.setString(parameterIndex, columnValue);
                                parameterIndex++;
                                System.out.println("query3: " + query);
                            }
                        }
                        System.out.println("query2: " + query);
                        preparedStatement.executeUpdate();
                    }
                }


            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(itemForm.this, "Error al guardar datos en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }

            disposeAndUpdateTable();
        });
    }

    private void cargarInfo(int id, String tableName, String addOrEdit) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE id = '" + id + "'";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    textFields = new JTextField[columnCount];
                    columnNames = new String[columnCount];

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    switch (addOrEdit) {
                        case "add":
                            for (int i = 0; i < columnCount; i++) {
                                int esAutoGenerado = metaData.isNullable(i + 1);
                                boolean esAutoIncremental = metaData.isAutoIncrement(i + 1);

                                if (esAutoGenerado == 1 || esAutoIncremental) {
                                    continue;
                                }

                                String columnName = metaData.getColumnName(i + 1);
                                columnNames[i] = columnName;

                                System.out.println(columnName);

                                JLabel label = new JLabel(columnName);
                                label.setHorizontalAlignment(SwingConstants.RIGHT);
                                label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));

                                JTextField textField = new JTextField(50);

                                gbc.gridx = 0;
                                gbc.weightx = 0.3;
                                panel.add(label, gbc);

                                gbc.gridx = 1;
                                gbc.weightx = 0.7;
                                panel.add(textField, gbc);
                                panel.add(botonesPanel, gbc);
                                textFields[i] = textField;
                            }


                            break;
                        case "edit":
                            for (int i = 0; i < columnCount; i++) {
                                int esAutoGenerado = metaData.isNullable(i + 1);
                                boolean esAutoIncremental = metaData.isAutoIncrement(i + 1);

                                if (esAutoGenerado == 1 || esAutoIncremental) {
                                    continue;
                                }

                                String columnName = metaData.getColumnName(i + 1);
                                columnNames[i] = columnName;

                                System.out.println(columnName);

                                JLabel label = new JLabel(columnName);
                                label.setHorizontalAlignment(SwingConstants.RIGHT);
                                label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));

                                JTextField textField = new JTextField(50);

                                gbc.gridx = 0;
                                gbc.weightx = 0.3;
                                panel.add(label, gbc);

                                gbc.gridx = 1;
                                gbc.weightx = 0.7;
                                panel.add(textField, gbc);
                                panel.add(botonesPanel, gbc);
                                textField.setText(resultSet.getString(i + 1));

                                textFields[i] = textField;
                            }

                            break;
                    }


                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos desde la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void disposeAndUpdateTable() {
        dispose();
        tableUpdateListener.updateTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new itemForm(1, "documentos", "add", null));
    }
}