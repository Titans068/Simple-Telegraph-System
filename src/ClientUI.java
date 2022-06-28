import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Random;

import static java.lang.System.out;

public class ClientUI extends JFrame implements ActionListener {
    String uname;
    PrintWriter pw;
    BufferedReader br;
    JTextArea taMessages;
    JTextField tfInput;
    JButton btnSend, btnExit, btnView, btnHide, btnRld;
    Socket client;
    static long mill = System.currentTimeMillis();
    static Date d = new Date(mill);
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String[] greet = {"Welcome ", "Wilkommen ", "Bonjour ", "Hello There ", "Hi ", "Wassup ", "G'Day ", "Yo' ", "Hey ", "How are you today "};
    Random rn = new Random();
    int r = rn.nextInt(greet.length);
    static String name = LoginUI.lbl1.getText();
    static boolean run = true;
    JTextArea jt = new JTextArea();
    JPanel jp = new JPanel(new FlowLayout());
    JPanel panel = new JPanel(new GridLayout(1, 2));
    JScrollPane scroll = new JScrollPane(jt);

    public ClientUI(String uname, String servername) throws Exception {
        super(uname);  // set title for frame
        this.uname = uname;
        client = new Socket(servername, 5000);
        br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        pw = new PrintWriter(client.getOutputStream(), true);
        //pw.println(uname);  // send name to server
        buildInterface();

        btnView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TitledBorder title;
                    String[] tl = {"Showing messages...\n", "No messages yet..."};
                    var loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

                    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(LoginUI.url, LoginUI.user, LoginUI.passwd);
                    Statement stm = conn.createStatement();
                    String query = "select * from telegraph where password is null";
                    Statement stm0 = conn.createStatement();
                    ResultSet rst = stm.executeQuery(query);
                    if (counter(name) > 0) {
                        while (rst.next()) {
                            String message = rst.getString("message");
                            String sender = rst.getString("sender");
                            String time = rst.getString("msgtime");
                            if (message != null) {
                                btnView.setVisible(false);
                                btnRld.setVisible(true);
                                btnHide.setVisible(true);
                                panel.add(scroll);
                                setSize(getWidth(), getHeight());
                                title = BorderFactory.createTitledBorder(loweredetched, tl[0]);
                                title.setTitleJustification(TitledBorder.RIGHT);
                                title.setTitleColor(Color.BLUE);
                                //jComp10.setBorder(title);
                                panel.setBorder(title);
                                jt.append("Sender: " + sender + "\nMessage: " + message + "\nTime: " + time + "\n\n");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No messages here...", "╯︿╰", JOptionPane.INFORMATION_MESSAGE);
                    }
                    stm.close();


                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnHide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jt.setText("");
                panel.remove(scroll);
                setSize(getWidth(), getHeight());
                btnHide.setVisible(false);
                btnRld.setVisible(false);
                btnView.setVisible(true);
                panel.setBorder(null);
            }
        });

        btnRld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TitledBorder title;
                    String[] tl = {"Showing messages...\n", "No messages yet..."};
                    var loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

                    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    jt.setText("");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(LoginUI.url, LoginUI.user, LoginUI.passwd);
                    Statement stm = conn.createStatement();
                    String query = "select * from telegraph where password is null";
                    Statement stm0 = conn.createStatement();
                    ResultSet rst = stm.executeQuery(query);
                    if (counter(name) > 0) {
                        while (rst.next()) {
                            String message = rst.getString("message");
                            String sender = rst.getString("sender");
                            String time = rst.getString("msgtime");
                            if (message != null) {
                                btnView.setVisible(false);
                                btnRld.setVisible(true);
                                btnHide.setVisible(true);
                                panel.add(scroll);
                                setSize(getWidth(), getHeight());
                                title = BorderFactory.createTitledBorder(loweredetched, tl[0]);
                                title.setTitleJustification(TitledBorder.RIGHT);
                                title.setTitleColor(Color.BLUE);
                                //jComp10.setBorder(title);
                                panel.setBorder(title);
                                jt.append("Sender: " + sender + "\nMessage: " + message + "\nTime: " + time + "\n\n");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No messages here...", "╯︿╰", JOptionPane.INFORMATION_MESSAGE);
                    }
                    stm.close();


                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        tfInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // send message to server
                    if (!tfInput.getText().isBlank()) {
                        if (tfInput.getText().equals("/quit")) {
                            File f = new File("sts.bin");
                            f.setWritable(true);
                            f.delete();
                            System.exit(0);
                            dispose();
                        }
                        pw.println(tfInput.getText());
                        btnRld.doClick();
                        tfInput.setText("");
                        tfInput.requestFocusInWindow();
                    }
                }
            }
        });
        new MessagesThread().start();  // create thread for listening for messages
    }

    public void buildInterface() {
        btnSend = new JButton("Send");
        btnExit = new JButton("Exit");
        btnView = new JButton("View Messages");
        btnHide = new JButton("Hide");
        btnRld = new JButton("Refresh");
        taMessages = new JTextArea();
        taMessages.setRows(10);
        taMessages.setColumns(50);
        taMessages.setEditable(false);
        tfInput = new JTextField(50);
        tfInput.setToolTipText("Type /quit to leave conversation");
        taMessages.append(greet[r] + name + "\n");
        /*
        boldFont=new Font(taMessages.getFont().getName(), Font.PLAIN, taMessages.getFont().getSize());
        taMessages.setFont(boldFont);
        */
        JScrollPane sp = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(sp);


        JPanel bp = new JPanel(new FlowLayout());
        bp.add(tfInput);
        bp.add(btnSend);
        bp.add(btnExit);
        bp.add(btnView);
        bp.add(btnHide);
        bp.add(btnRld);
        btnHide.setVisible(false);
        btnRld.setVisible(false);
        add(bp, BorderLayout.SOUTH);

        //add(jp, "East");

        jt.setRows(15);
        jt.setColumns(30);
        jt.setEditable(false);

        btnSend.addActionListener(this);
        btnExit.addActionListener(this);
        setTitle("Simple Telegraph System");
        getContentPane().add(panel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnExit) {
            pw.println("/quit");  // send end to server so that server know about the termination
            File f = new File("sts.bin");
            f.setWritable(true);
            f.delete();
            System.exit(0);
        }
        if (evt.getSource() == btnSend) {
            // send message to server
            if (!tfInput.getText().isBlank()) {
                if (tfInput.getText().equals("/quit")) {
                    System.exit(0);
                    dispose();
                }
                pw.println(tfInput.getText());
                btnRld.doClick();
                tfInput.setText("");
                tfInput.requestFocusInWindow();
            }

        }
    }
    //formerly the main()
    public static void run() {
        try {
            new Socket("localhost", 5000);
        } catch (Exception ex) {
            if (ex instanceof ConnectException) {
                JOptionPane.showMessageDialog(null, "Connect to Server first", "Simple Telegraph System", JOptionPane.ERROR_MESSAGE);
                System.err.println("Connect to Server first");
                System.exit(0);
            } else {
                ex.printStackTrace();
            }
        }
        // take username from user

        String servername = "localhost";
        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            new ClientUI(name, servername);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Simple Telegraph System", JOptionPane.ERROR_MESSAGE);
            out.println("Error --> " + ex.getMessage());
        }

    } // end of main

    public static int counter(String user) {
        int count = 0;
        try {
            //Registering the Driver
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            //Getting the connection
            String mysqlUrl = "jdbc:mysql://localhost/sakila";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "");
            //System.out.println("Connection established......");
            //Creating the Statement object
            PreparedStatement stmt = con.prepareStatement("select count(*) from telegraph where sender=?");
            //Query to get the number of rows in a table
            stmt.setString(1,user);

            //Executing the query
            ResultSet rs = stmt.executeQuery();
            //Retrieving the result
            rs.next();
            count = rs.getInt(1);
            //System.out.println("Number of records in the telegraph table: "+count);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return count;
    }

    // inner class for Messages Thread
    class MessagesThread extends Thread {
        public void run() {
            String line;
            try {
                while (true) {
                    if (br.readLine() != null || !br.readLine().equals("/quit") || !tfInput.getText().isBlank()) {
                        line = br.readLine();
                        taMessages.append(line + "\n");
                    } else {
                        stop();
                    }
                } // end of while
            } catch (Exception ex) {
                /*if (!(ex instanceof NullPointerException)) {
                    ex.printStackTrace();
                }
                else if (!(ex instanceof SocketException))
                {
                    ex.printStackTrace();
                }*/
                if (ex instanceof SocketException) {
                    JOptionPane.showMessageDialog(null, "The Server may have been reset...", "Simple Telegraph System", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        }
    }
}