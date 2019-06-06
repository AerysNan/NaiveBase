package client;

import global.Global;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

class ClientUILogin extends JFrame implements ActionListener {

    private JButton exit = new JButton("exit");
    private JTextField JName = new JTextField(20);
    private JPasswordField JPassword = new JPasswordField(20);

    ClientUILogin() {
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(3, 2));

        JButton login = new JButton("login");
        JLabel name = new JLabel("username");
        JLabel password = new JLabel("password");

        name.setHorizontalAlignment(SwingConstants.CENTER);
        password.setHorizontalAlignment(SwingConstants.CENTER);

        jp.add(name);
        jp.add(JName);
        jp.add(password);
        jp.add(JPassword);
        jp.add(login);
        jp.add(exit);

        login.addActionListener(this);
        exit.addActionListener(this);

        this.add(jp, BorderLayout.CENTER);

        init();
    }

    private void init() {
        this.setResizable(false);
        this.setTitle("Client");
        this.setLocation(500, 300);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        Client.clean();
                        super.windowClosed(e);
                    }
                }
        );
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exit) {
            int i = JOptionPane.showConfirmDialog(null, "Are you sure to exit?", "confirm", JOptionPane.YES_NO_OPTION);
            if (i == JOptionPane.YES_OPTION) {
                Client.clean();
                System.exit(0);
            }
        } else {
            Client.write(JName.getText() + " " + Global.encrypt(String.valueOf(JPassword.getPassword())));
            String response = Client.read();
            if (response.startsWith("OK")) {
                this.dispose();
                new ClientUIConnect();
            } else {
                JOptionPane.showMessageDialog(null, "Username or password error.");
                JPassword.setText("");
            }
        }
    }
}