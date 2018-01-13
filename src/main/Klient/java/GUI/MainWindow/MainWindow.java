package GUI.MainWindow;

import Camera.CameraCapture;
import Connection.Connection;
import Connection.Message;
import GUI.Conference.Conference;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainWindow {
    private JTree contacts;
    private JButton exitButton;
    private JButton connectButton;
    private JButton addContactButton;
    private JButton deleteContactButton;
    private JPanel generalPanel;
    private JButton addFolderButton;
    private JLabel output;
    private JButton deleteFolderButton;
    private static CameraCapture camera;
    private Connection connection;
    private DefaultTreeModel model;


    static private Conference conference;
    static private JFrame frame;

    public MainWindow(JFrame father, final Connection connection) {
        this.connection = connection;
        frame = new JFrame("Connection to the server");
        frame.setContentPane(generalPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        camera = new CameraCapture(connection);
        model = (DefaultTreeModel) contacts.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.setUserObject("Contacts");
        root.removeAllChildren();
        model.reload();

        connectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) contacts.getLastSelectedPathComponent();
                    if (node == null)
                        throw new Exception("Select a contact");
                    if (node.getAllowsChildren() == true)
                        throw new Exception("Select a contact");

                    Message targetname = new Message();
                    String targetuser = (String) node.getUserObject();
                    targetname.setString(targetuser);
                    connection.write(targetname);
                    Message msg = connection.readMassage();
                    msg.updateMessage(connection.getInputBuffer());
                    if (msg.getString().equals("Polaczono!")) ;
                    conference = new Conference(frame, camera, connection);
                    camera.setGui(conference);
                    frame.setVisible(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    output.setText("Cannot establish connection");
                    refreshOutput();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    output.setText(e1.getMessage());
                    refreshOutput();
                }
            }
        });


        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.exit(0);
            }
        });
        addFolderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) contacts.getLastSelectedPathComponent();
                    if (node == null)
                        throw new Exception("Nothing is selected");
                    if (node.getAllowsChildren() == false)
                        throw new Exception("Select a folder");
                    AddContact addContact = new AddContact(contacts, node, false);
                    addContact.pack();
                    addContact.setVisible(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    output.setText(e1.getMessage());
                    refreshOutput();
                }
            }
        });
        addContactButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) contacts.getLastSelectedPathComponent();
                    if (node == null)
                        throw new Exception("Nothing is selected");
                    if (node.getAllowsChildren() == false)
                        throw new Exception("Select a folder");
                    AddContact addContact = new AddContact(contacts, node, true);
                    addContact.pack();
                    addContact.setVisible(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    output.setText(e1.getMessage());
                    refreshOutput();
                }
            }
        });
        deleteContactButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    DefaultTreeModel model = (DefaultTreeModel) contacts.getModel();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) contacts.getLastSelectedPathComponent();
                    if (node == null)
                        throw new Exception("Nothing is selected");
                    if (node.getAllowsChildren() == true)
                        throw new Exception("Select a contact");
                    if (node.getParent() == null)
                        throw new Exception("Cannot delete root");
                    model.removeNodeFromParent(node);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    output.setText(e1.getMessage());
                    refreshOutput();
                }
            }
        });
        deleteFolderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    DefaultTreeModel model = (DefaultTreeModel) contacts.getModel();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) contacts.getLastSelectedPathComponent();
                    if (node == null)
                        throw new Exception("Nothing is selected");
                    if (node.getAllowsChildren() == false)
                        throw new Exception("Select a folder");
                    if (node.getParent() == null)
                        throw new Exception("Cannot delete root");
                    model.removeNodeFromParent(node);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    output.setText(e1.getMessage());
                    refreshOutput();
                }
            }
        });
        generalPanel.addComponentListener(new ComponentAdapter() {
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        });
    }

    private void refreshOutput() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    output.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        frame.repaint();
    }

    private byte[] contactsToBytes() {

        return new byte[0];
    }
}
