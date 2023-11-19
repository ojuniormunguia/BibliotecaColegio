import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;


public class Main extends JFrame {
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;



    public Main() {


        setTitle("Inicio de Sesión");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel labelUsuario = new JLabel("Usuario:");
        campoUsuario = new JTextField();
        JLabel labelContrasena = new JLabel("Contraseña:");
        campoContrasena = new JPasswordField();

        JButton loginBoton = new JButton("Iniciar Sesión");

        panel.add(labelUsuario);
        panel.add(campoUsuario);
        panel.add(labelContrasena);
        panel.add(campoContrasena);
        panel.add(new JLabel()); // Espacio en blanco
        panel.add(loginBoton);

        add(panel);

        loginBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] loginResult = validarLogin();

                if (loginResult != null) {
                    // Credenciales correctas, dirigir a la vista correspondiente
                    dispose(); // Cierra la ventana de inicio de sesión

                    String userID = (String) loginResult[0];
                    String userType = (String) loginResult[1];

                    // Redirige a la vista según el tipo de usuario
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            switch (userType) {
                                case "Administrador":
                                    new AdminView(userID);
                                    break;
                                case "Profesor":
                                    new ProfesorView(userID);
                                    break;
                                case "Alumno":
                                    new AlumnoView(userID);
                                    break;
                                default:
                                    throw new RuntimeException("Tipo de usuario no válido");
                            }
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(Main.this, "Credenciales incorrectas", "Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }



    private Object[] validarLogin() {
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id, tipo_usuario FROM usuarios WHERE usuario = ? AND contrasena = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, usuario);
                preparedStatement.setString(2, contrasena);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Login successful, return the user ID and type as Object array
                    return new Object[]{resultSet.getString("id"), resultSet.getString("tipo_usuario")};
                } else {
                    // Login failed, return null or throw an exception
                    return null; // or throw new RuntimeException("Login failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or throw new RuntimeException("Error connecting to the database");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
