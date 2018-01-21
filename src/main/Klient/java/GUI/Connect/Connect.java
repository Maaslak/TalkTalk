package GUI.Connect;

import Connection.Connection;
import GUI.MainWindow.MainWindow;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class Connect {
    private JFrame frame;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton connectButton;
    private JPanel connectionPanel;
    private JLabel message;
    private JTextField usernameText;
    private JTextField portText;
    private String ip;
    private int ip_tab[];
    private boolean ifCorrect;
    private boolean bypass = true;
    private String username;

    public Connect() {
        frame = new JFrame("Connection to the server");
        frame.setContentPane(connectionPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        ip_tab = new int[4];
        connectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int port;
                    if (bypass = false) {
                        ifCorrect = true;
                        message.setText("");
                        String[] content = {textField1.getText(), textField2.getText(), textField3.getText(), textField4.getText()};
                        for (int i = 0; i < ip_tab.length; i++) {
                            ip_tab[i] = Integer.parseInt(content[i]);
                            if (ip_tab[i] < 0 || ip_tab[i] > 255) throw new NumberFormatException("Out of range");
                        }
                        ip = "";
                        for (int i = 0; i < 4; i++) {
                            ip += String.valueOf(ip_tab[i]);
                            if (i != 3)
                                ip += '.';
                        }
                        if (portText.getText().length() > 4) throw new NumberFormatException("Username error");
                        port = Integer.parseInt(portText.getText());
                        username = usernameText.getText();
                    } else {
                        ip = "25.49.235.96";
                        //ip = "25.49.78.74";
                        //ip = "25.67.207.151";
                        ip = "192.168.0.12";
                        port = 1314;
                    }
                    username = usernameText.getText();
                    Connection connection = new Connection(ip, username, port);
                    MainWindow main = new MainWindow(frame, connection);
                    frame.setVisible(false);
                } catch (NumberFormatException ex) {
                    message.setText("Input error");
                    ifCorrect = false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    message.setText("Could not connect");
                }

                super.mouseClicked(e);
            }
        });
    }
}
