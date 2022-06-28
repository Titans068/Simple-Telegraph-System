import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

//TODO: Remember telnet 192.168.100.25 5000
public class Server {
    static final String url = "jdbc:mysql://localhost:3306/sakila";
    static final String user = "root";
    static final String passwd = "";
    static long mill = System.currentTimeMillis();
    static Date d = new Date(mill);
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    JPanel jp = new JPanel(new BorderLayout());
    static boolean ru=false;
    JTextArea ta;
    JTextArea ta2;
    JButton stop = new JButton("Stop Server");
    JButton clear = new JButton("Clear");
    JScrollPane scr;
    JScrollPane scr2;
    JFrame jf;
    static Server s = new Server();

    public Server() {
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ta.setText("");
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private static Set<String> names = new HashSet<>();

    private static Set<PrintWriter> writers = new HashSet<>();

    public void __init__() throws Exception {
        System.out.println("Server is running...");
        s.buildUI();
        var pool = Executors.newFixedThreadPool(500);
        try (var listener = new ServerSocket(5000)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        s.__init__();
    }

    public void buildUI() {
        var loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder title0 = BorderFactory.createTitledBorder(loweredetched, "Server operations");
        TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "Active users");
        Border bor = BorderFactory.createCompoundBorder(title0, title);
        title.setTitleJustification(TitledBorder.RIGHT);
        title.setTitleColor(Color.BLUE);
        title0.setTitleColor(Color.BLUE);
        jf = new JFrame("Server");
        ta2 = new JTextArea();
        scr2 = new JScrollPane(ta2);
        jp.setBorder(bor);

        ta = new JTextArea();
        ta.setRows(10);
        ta.setColumns(50);
        ta.setEditable(false);
        ta.append("Server is running...\n");
        scr = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jp.add(scr, BorderLayout.CENTER);
        JPanel bp = new JPanel(new FlowLayout());
        bp.add(clear);
        bp.add(stop);
        jf.add(bp, BorderLayout.SOUTH);

        scr2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scr2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jp.add(scr2, BorderLayout.EAST);

        ta2.setRows(15);
        ta2.setColumns(25);
        ta2.setEditable(false);
        //ta2.append("System.String is immutable, this is where it cannot be modified once it has been created whereas System.StringBuilder class is useful where you find yourself in a continuous operation with a String object since it is mutable, that is, it can be modified after creation.");

        jf.getContentPane().add(jp, BorderLayout.CENTER);
        jf.setSize(800, 600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        jf.pack();
        jf.setLocationRelativeTo(null);
    }


    private class Handler implements Runnable {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        String[] greet = {"Welcome ", "Wilkommen ", "Bonjour ", "Bievenue ", "Hello There ", "Hi ", "Wassup ", "G'Day ", "Hey ", "How are you today "};
        Random rn = new Random();
        int r = rn.nextInt(greet.length);
        File f1 = new File("lo.db");

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                name = Client.read("sts.bin");

                if (!f1.exists()) {
                    boolean validate = false;
                    //if (!read("sts.bin").equals("False") || !read("sts.bin").equals("Files may have been modified...")) {
                    do {
                        try {
                            validate = true;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection conn = DriverManager.getConnection(url, user, passwd);
                            out.println("\n\nConnection established. Log in. Enter username then password each in new lines. To register use either our official GUI or Console client");
                            String username = "";
                            String usern = in.nextLine();
                            String password = "";
                            String pass = in.nextLine();
                            PreparedStatement stmt = conn.prepareStatement("SELECT username,password FROM telegraph WHERE username=? && password=?");
                            stmt.setString(1, usern);
                            stmt.setString(2, pass);

                            ResultSet rs = stmt.executeQuery();

                            while (rs.next()) {
                                username = rs.getString("username");
                                password = rs.getString("password");
                            }

                            if (usern.equals(username) && pass.equals(password)/* && existence(usern)*/) {
                                out.println("Successful Login!\n----");
                                name = usern;
                            } else {
                                out.println("Incorrect Username or Password\n----");
                                validate = false;
                            }
                        } catch (Exception throwables) {
                            throwables.printStackTrace();
                            out.println(throwables.getMessage());
                        }
                    } while (!validate);
                }
                out.println(greet[r] + name);
                String last;
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                for (PrintWriter writer : writers) {
                    writer.println("> " + name + " has joined at " + timeStamp);
                    System.out.println(name + " is joining at " + timeStamp);
                    ta.append(name + " is joining at " + timeStamp + "\n");
                }
                if (!ta2.getText().contains(name)) {
                    if(!ru) {
                        ta2.append(name + "\n");
                        ta2.setText(ta2.getText().substring(ta2.getText().indexOf("\n") + 1).trim());
                        ru = true;
                    }
                }
                writers.add(out);
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, user, passwd);
                //System.out.println("Connected to database");
                while (true) {
                    //out.printf("> ");
                    String input = in.nextLine();
                    String timeStamp1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                    System.out.println(timeStamp1 + " " + name + " : " + input);
                    ta.append(timeStamp1 + " " + name + " : " + input + "\n");
                    if (input.toLowerCase().startsWith("/quit")) {
                        out.println("Exiting conversation...");
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("> " + timeStamp1 + " MESSAGE FROM " + name + ": " + input);
                    }
                    input = input.replace("'", "\\'");
                    input = input.replace("\"", "\\\"");
                    PreparedStatement st = conn.prepareStatement("insert into telegraph(username,message, sender, msgtime)  values (?,?,?,?)");
                    st.setString(1, name);
                    st.setString(2, input);
                    st.setString(3, name);
                    st.setString(4, timeStamp1);
                    st.executeUpdate();
                }

            } catch (Exception e) {
                if (!(e instanceof NoSuchElementException)) {
                    e.printStackTrace();
                }
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    if (name != null) {
                        names.remove(name);
                        for (PrintWriter writer : writers) {
                            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                            writer.println("> " + name + " has exited this conversation at " + timeStamp);
                        }
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        System.out.println(name + " is exiting this conversation at " + timeStamp);
                        ta.append(name + " is exiting this conversation at " + timeStamp + "\n");
                        String ta2txt = ta2.getText().replace(name + "\n", "");
                        ta2.setText(ta2txt);
                        f1.delete();
                    }
                    socket.close();
                } catch (IOException | ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}