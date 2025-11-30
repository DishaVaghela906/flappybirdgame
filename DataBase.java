package flappybirdgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DataBase extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private int highScore = 0;
    private String username;

    public DataBase() {
        setSize(new Dimension(900, 800));
        setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(350, 300, 150, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(450, 300, 150, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(350, 350, 150, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(450, 350, 150, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(400, 400, 100, 25);
        loginButton.addActionListener(this);
        add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(400, 450, 100, 25);
        registerButton.addActionListener(this);
        add(registerButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (validateLogin(username, password)) {
                retrieveAndDisplayScore(username);
                this.dispose();  
                new StartFrame(username,highScore);  
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        } else if (e.getSource() == registerButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            registerUser(username, password);
            JOptionPane.showMessageDialog(this, "User registered successfully!");
        }
    }

    private boolean validateLogin(String username, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/flappybird", "root", "root");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void registerUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/flappybird", "root", "root");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void retrieveAndDisplayScore(String username) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/flappybird", "root", "root");
             PreparedStatement statement = connection.prepareStatement("SELECT score FROM users WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                highScore = resultSet.getInt("score");
                JOptionPane.showMessageDialog(this, "Welcome " + username + "! Your previous score is: " + highScore);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DataBase();
    }
}

