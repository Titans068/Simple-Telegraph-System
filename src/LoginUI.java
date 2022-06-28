import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.Scanner;

public class LoginUI {
    private JTextField textField1;
    private JTextField passwordField1;
    private JButton button1;
    private JPanel JPanel1;
    private JLabel username;
    private JLabel Password;
    private JLabel loginlbl;
    private JButton button2;
    private JTextField reguser;
    private JPasswordField regpass;
    private JLabel lblpass;
    private JLabel lbluser;
    private JLabel lblreg;
    private JButton registerButton;
    private JPanel jp2;
    private JPanel jp1;
    private JButton logInButton;
    public static JLabel lbl1 = new JLabel();
    static String url = Client.url;
    static String user = Client.user;
    static String passwd = Client.passwd;
    static JFrame frame = new JFrame("LoginUI");

    String key = "saintsmarchingin";
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher cipher = Cipher.getInstance("AES");

    public LoginUI() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        disappear(true);
        button1.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (login()) {
                    File fx = new File("lo.db");
                    //This file tells the client whether the user is a first time user
                    try {
                        fx.createNewFile();
                        fx.setReadOnly();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    System.out.println(lbl1.getText());
                    frame.dispose();
                    try {
                        ClientUI.run();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (!login()) {
                    System.out.println("Incorrect Username or Password\n----");
                    JOptionPane.showMessageDialog(null, "Incorrect Username or Password", "Invalid Credentials", JOptionPane.ERROR_MESSAGE);
                    textField1.setText("");
                    passwordField1.setText("");
                    textField1.requestFocusInWindow();
                }
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //register();
                if (register()) {
                    JOptionPane.showMessageDialog(null, "Sign into your account", "Log in", JOptionPane.INFORMATION_MESSAGE);
                    disappear(true);
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Client.there()) {
                    disappear(false);
                } else {
                    JOptionPane.showMessageDialog(null, "User already exists.", "Registration unsuccessful.", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disappear(true);
            }
        });
    }

    void disappear(boolean choice) {
        // TODO: Mar 14 2021 true = login
        if (choice) {
            lblpass.setVisible(false);
            lbluser.setVisible(false);
            reguser.setVisible(false);
            regpass.setVisible(false);
            lblreg.setVisible(false);
            button2.setVisible(false);
            logInButton.setVisible(false);

            Password.setVisible(true);
            passwordField1.setVisible(true);
            button1.setVisible(true);
            textField1.setVisible(true);
            username.setVisible(true);
            loginlbl.setVisible(true);
            registerButton.setVisible(true);
        }
        // TODO: Mar 14 2021 false = register
        if (!choice) {
            Password.setVisible(false);
            passwordField1.setVisible(false);
            button1.setVisible(false);
            textField1.setVisible(false);
            username.setVisible(false);
            loginlbl.setVisible(false);
            registerButton.setVisible(false);

            lblpass.setVisible(true);
            lbluser.setVisible(true);
            reguser.setVisible(true);
            regpass.setVisible(true);
            lblreg.setVisible(true);
            button2.setVisible(true);
            logInButton.setVisible(true);
        }

    }

    boolean login() {
        String usern = null;
        boolean validate = false;
        try {
            validate = true;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            //System.out.println("Connection established.");
            Scanner s = new Scanner(System.in);
            String username = "";
            usern = this.textField1.getText();
            String password = "";

            String pass = this.passwordField1.getText();
            pass = Base64.getEncoder().encodeToString(cipher.doFinal(pass.getBytes(StandardCharsets.UTF_8)));

            PreparedStatement stmt = conn.prepareStatement("SELECT username,password FROM telegraph WHERE username=? && password=?");

            stmt.setString(1, usern);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
            }
            if (this.textField1.getText().isBlank() || this.passwordField1.getText().isBlank()) {
                this.textField1.setText("");
                this.passwordField1.setText("");
                this.textField1.requestFocusInWindow();
            }
            if (usern.equals(username) && pass.equals(password) && (!this.textField1.getText().isBlank() || !this.passwordField1.getText().isBlank())) {
                System.out.println("Successful Login!\n----");
                lbl1.setText(usern);
                lbl1.setVisible(false);
                Client.create(usern);
                return true;
            } else {
                validate = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean register() {
        try {
            if (!this.reguser.getText().isBlank() || !this.regpass.getText().isBlank()) {
                disappear(false);
                Scanner sc = new Scanner(System.in);
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, user, passwd);
                String username = this.reguser.getText();
                String password = this.regpass.getText();
                PreparedStatement st = conn.prepareStatement("insert into telegraph (username,password) values(?,?)");

                password = Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));

                st.setString(1, username);
                st.setString(2, password);
                st.executeUpdate();
                JOptionPane.showMessageDialog(null, "Successful registration. Make sure to remember your username and your password.", "Successful registration", JOptionPane.INFORMATION_MESSAGE);
                Client.create(username);
            } else {
                this.reguser.setText("");
                this.regpass.setText("");
                this.reguser.requestFocusInWindow();
                return false;
            }
            return true;
        } catch (ClassNotFoundException | SQLException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            frame.setContentPane(new LoginUI().JPanel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
