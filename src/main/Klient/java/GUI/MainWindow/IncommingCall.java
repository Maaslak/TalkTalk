package GUI.MainWindow;

import Connection.Connection;
import Connection.Message;
import GUI.Connect.Connect;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class IncommingCall extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel username;
    private MainWindow father;
    private Message message;
    private Connection connection;

    public IncommingCall(String username, MainWindow father) {
        this.father = father;
        this.username.setText(username);
        this.message = new Message();
        connection = father.getConnection();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        message.setString("accept");
        try {
            connection.write(message);
            father.initConference(username.getText());
        } catch (IOException e) {
            e.printStackTrace();
            if(e.getMessage().equals("Disconnected"))
                System.exit(-1);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        message.setString("decline");
        try {
            connection.write(message);
            father.initConference(username.getText());
        } catch (IOException e) {
            e.printStackTrace();
            if(e.getMessage().equals("Disconnected"))
                System.exit(-1);
        }
        dispose();
    }
}
