import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    static final String url = "jdbc:mysql://localhost:3306/sakila";
    static final String user = "root";
    static final String passwd = "";
    final static int ServerPort = 5000;
    static String serverAddress;
    static Scanner in;
    static PrintWriter out;
    static String userd, passd;
    static Cipher cipher;

    static {
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    static String key = "saintsmarchingin";
    static Key aesKey = new SecretKeySpec(key.getBytes(), "AES");

    static int choice, choice0, choice1;

    public Client(String serverAddress) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.serverAddress = serverAddress;
    }

    static void mysqlconn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            //System.out.println("Connection established.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean existence(String x) {
        try {
            File f = new File("sts.bin");
            if (!f.createNewFile()) {
                Scanner sc = new Scanner(f);
                while (sc.hasNextLine()) {
                    if (sc.nextLine().equals(x)) {
                        return true;
                    }
                    if (!(sc.nextLine().equals(x))) {
                        return false;
                    }
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Invalid user credentials. Exiting...");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean create(String x) {
        try {
            File f = new File("sts.bin");
            if (f.createNewFile()) { //if file doesn't exist
                FileWriter fw = new FileWriter("sts.bin");
                fw.write(x);
                fw.close();
                f.setReadOnly();
                return true;
            }
            if(!f.createNewFile()) //if file exists
            {
                f.setWritable(true);
                if(f.delete()) {
                    create(x);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();//return x;
            return false;
        }
        return false;
    }

    public static boolean there() {
        var filePathString = "C:\\wamp64\\www\\Simple Telegraph System\\sts.bin";
        File f = new File(filePathString);
        return f.exists() && !f.isDirectory();
    }

    public static String read(String x) {
        try {
            File f = new File(x);
            if (!f.createNewFile()) {
                Scanner sc = new Scanner(f);
                while (sc.hasNextLine()) {
                    return sc.nextLine();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Files may have been modified...";
        }
        return "False";
    }

    static void login() {
        boolean validate = false;
        //if (!read("sts.bin").equals("False") || !read("sts.bin").equals("Files may have been modified...")) {
            do {
                try {
                    validate = true;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, user, passwd);
                    //System.out.println("Connection established.");
                    Scanner s = new Scanner(System.in);
                    System.out.print("Enter username: ");
                    String username = "";
                    String usern = s.nextLine();
                    System.out.print("Enter password: ");
                    String password = "";
                    String pass = s.nextLine();

                    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                    pass = Base64.getEncoder().encodeToString(cipher.doFinal(pass.getBytes(StandardCharsets.UTF_8)));

                    PreparedStatement stmt = conn.prepareStatement("SELECT username,password FROM telegraph WHERE username=? && password=?");
                    stmt.setString(1,usern);
                    stmt.setString(2,pass);

                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        username = rs.getString("username");
                        password = rs.getString("password");
                    }

                    if (usern.equals(username) && pass.equals(password)/* && existence(usern)*/) {
                        System.out.println("Successful Login!\n----");
                        Client.create(usern);
                        userd = username;
                        passd = password;
                        File f=new File("lo.db");
                        f.createNewFile();
                        f.setReadOnly();
                    } else {
                        System.out.println("Incorrect Username or Password\n----");
                        validate = false;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (!validate);
        /*} else if (read("sts.bin").equals("False")) {
            register();
        }*/
    }

    static void register() {
        try {
            Scanner sc = new Scanner(System.in);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            System.out.println("Register for a new account.");
            PreparedStatement st = conn.prepareStatement("insert into telegraph (username,password) values(?,?)");
            System.out.println("Enter username");
            String username = sc.nextLine();
            if (!there()) {
                System.out.println("Enter password");
                String password = sc.nextLine();
                password = Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
                st.setString(1,username);
                st.setString(2,password);

                st.executeUpdate();
                if (create(username)) {
                    System.out.println("Successful registration. Make sure to remember your username and your password.");
                }
            } else {
                System.out.println("User exists... Failed creating account.");
                System.exit(0);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    static void mymsgs() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Showing only your messages...\n");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, passwd);
            PreparedStatement stm = conn.prepareStatement("select * from telegraph where sender=?");
            stm.setString(1,userd);

            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                String message = rst.getString("message");
                String sender = rst.getString("sender");
                String time = rst.getString("msgtime");
                if (message != null) {
                    System.out.format("Sender: %s\nMessage: %s\nTime: %s\n\n", sender, message, time);
                } else {
                    System.out.println("No messages yet...");
                }
            }
            stm.close();
            menuloop();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static void menu() {
        Scanner s = new Scanner(System.in);
        System.out.println("Welcome to the Simple Telegraph System\n1. Log in\n2. Register(for new users).\n3. Exit.");
        choice0 = s.nextInt();
    }

    static void run() {
        try {
            var socket = new Socket(serverAddress, 5000);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"telnet localhost 5000 && title Simple Telegraph System\"");
            menuloop();
        } catch (Exception ex) {
            if (ex instanceof ConnectException) {
                System.out.println("Server unavailable or experiencing downtime");
            } else {
                ex.printStackTrace();
            }
        } finally {

        }
    }

    static void menuloop() {
        boolean menuloop = true;
        Scanner scan = new Scanner(System.in);
        System.out.println("\nChoose any of these options:\n1. Send message.\n2. Show my messages\n3. Exit");
        choice = scan.nextInt();
        scan.nextLine();
        switch (choice) {
            case 1: {
                run();
                break;
            }
            case 2: {
                mymsgs();
                break;
            }
            case 3: {
                System.exit(0);
            }
            default: {
                System.out.println("Invalid input. Try again");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        boolean menuloop = true;
        mysqlconn();
        while (menuloop = true) {
            menu();
            switch (choice0) {
                case 1: {
                    login();
                    /* try {

                        URI uri= new URI("http://localhost/Simple%20Telegraph%20System/messagelogin.php");

                        java.awt.Desktop.getDesktop().browse(uri);
                        System.out.println("Log in from browser");

                    } catch (Exception e) {

                        e.printStackTrace();
                    } */
                    menuloop();
                    break;
                }
                case 2: {
                    register();
                    /* try {

                        URI uri= new URI("http://localhost/Simple%20Telegraph%20System/messageregister.php");

                        java.awt.Desktop.getDesktop().browse(uri);
                        System.out.println("Register from browser");

                    } catch (Exception e) {

                        e.printStackTrace();
                    } */
                    //loop
                    menuloop();
                    break;
                }
                case 3: {
                    File f=new File("sts.bin");
                    f.setWritable(true);
                    f.delete();
                    System.exit(0);
                }
                default: {
                    System.out.println("Invalid input. Try again");
                    choice0 = scan.nextInt();
                }
            }
        }
    }
}