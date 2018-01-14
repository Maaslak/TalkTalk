package GUI.MainWindow;

import Camera.CameraCapture;
import Connection.Connection;
import Connection.Message;
import GUI.Conference.Conference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.awt.Mutex;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.xml.soap.Node;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MainWindow implements Runnable {
    private JTree contacts;
    private JButton exitButton;
    private JButton connectButton;
    private JButton addContactButton;
    private JButton deleteContactButton;
    private JPanel generalPanel;
    private JButton addFolderButton;
    private JLabel output;
    private JButton deleteFolderButton;
    private JButton generateJSONButton;
    private JButton readContactsButton;
    private static CameraCapture camera;
    private Connection connection;
    private DefaultTreeModel model;
    private boolean inConference = false;
    private Message contactsMsg;


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
        deserialize(); // getting contact list from server

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
                    targetuser += '\0';
                    targetname.setString(targetuser);
                    connection.write(targetname);
                    Message msg = connection.readMassage();
                    msg.updateMessage(connection.getInputBuffer());
                    if (msg.getString().equals("Polaczono!")) {
                        initConference();
                    } else {
                        throw new IOException("Err");
                    }
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
        generateJSONButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                serialize();
            }
        });
        readContactsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                deserialize();
            }
        });
    }

    public void addNodesToMap(Map<String, DefaultMutableTreeNode> modelMap, Node node) {

    }

    public void initConference() {
        conference = new Conference(frame, camera, connection);
        Thread thread = new Thread(conference);
        thread.start();
        camera.setGui(conference);
        frame.setVisible(false);
        inConference = true;
    }

    private void serialize() {
        model = (DefaultTreeModel) contacts.getModel();
        ObjectMapper mapper = new ObjectMapper();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        /*
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("/contacts.ser");


            //mapper.writeValue(System.out, model);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(model);
            out.close();
            fileOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        /*
        Map<String, DefaultMutableTreeNode> modelMap = new HashMap<String, DefaultMutableTreeNode>();

        for (DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getFirstChild(); !child.equals(root.getLastChild()); child = root.getNextNode()){
            modelMap.put("contact", child);
        }

        try {
            mapper.writeValue(System.out, modelMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        int count = root.getChildCount();
        String result = new String("");
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) (root.getChildAt(i));
                result += (String) child.getUserObject() + '\n';
            }
        } else {
            result = "---";
        }
        System.out.println(result);
        this.contactsMsg.setString(result);
        this.contactsMsg.setType('c');
    }

    private void deserialize() {

        /*
        try {
            FileInputStream fileIn = new FileInputStream("/contacts.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            model = (DefaultTreeModel) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
        }
        */
        try {
            contactsMsg = connection.readMassage();
            if (contactsMsg.getType() != 's') throw new IOException();
            String contactsString = this.contactsMsg.getString();
            //String contactsString = new String("maaslak\nMaaslak");
            BufferedReader bufReader = new BufferedReader(new StringReader(contactsString));
            String line;
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            while ((line = bufReader.readLine()) != null) {
                if (line.equals("---")) break;
                DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
                newnode.setAllowsChildren(false);
                model.insertNodeInto(newnode, root, root.getChildCount());
            }
            model.removeNodeFromParent((MutableTreeNode) root.getLastChild());

        } catch (IOException e) {
            output.setText("Could not fetch contacts data");
            e.printStackTrace();
        }
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

    /*
     If someone trying to connect
     */
    public void run() {
        while (!inConference)
            try {
                Message incommingConnection = connection.readMassage();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
    }

    public Connection getConnection() {
        return connection;
    }
}
