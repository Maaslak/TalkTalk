package GUI.MainWindow;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.*;

public class AddContact extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JLabel output;
    private JLabel usernameLabel;
    private JTree contacts;
    private DefaultMutableTreeNode selected;
    private boolean iscontact;

    public AddContact(JTree contacts, DefaultMutableTreeNode node, boolean iscontact) {
        this.contacts = contacts;
        this.selected = node;
        this.iscontact = iscontact;
        setContentPane(contentPane);
        if (iscontact)
            usernameLabel.setText("username");
        else
            usernameLabel.setText("Folder name");
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOK();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    output.setText(e1.getMessage());
                }
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

    private void onOK() throws Exception {
        // add your code here
        DefaultTreeModel model = (DefaultTreeModel) contacts.getModel();
        String username = textField1.getText();
        if (username.length() > 15 || username.length() == 0) throw new Exception("Wrong username length");
        DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(username);
        if (iscontact)
            newnode.setAllowsChildren(false);
        model.insertNodeInto(newnode, selected, selected.getChildCount());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        //AddContact dialog = new AddContact();
        //dialog.pack();
        //dialog.setVisible(true);
        System.exit(0);
    }
}
