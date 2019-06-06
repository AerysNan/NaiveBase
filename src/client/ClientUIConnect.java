package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;


class ClientUIConnect extends JFrame implements ActionListener {
    class MyFormModel extends AbstractTableModel {
        private String[] columnName;
        private Object[][] data;

        MyFormModel(String message) {
            reset(message);
        }

        void reset(String message) {
            String[] info = message.split("\n");
            timeLabel.setText(info[info.length - 1]);
            if (info.length >= 7) {
                String[] header = info[1].split("\\|");
                columnName = new String[header.length - 1];
                for (int i = 0; i < header.length - 1; i++) {
                    columnName[i] = header[i + 1].trim();
                }
                data = new Object[info.length - 6][];
                for (int i = 3; i < info.length - 3; i++) {
                    String[] body = info[i].split("\\|");
                    Object[] entries = new Object[body.length - 1];
                    for (int j = 0; j < entries.length; j++) {
                        entries[j] = body[j + 1].trim();
                    }
                    data[i - 3] = entries;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect data from server!");
            }
        }

        public int getColumnCount() {
            return columnName.length;
        }


        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnName[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
    }

    private JTextArea inputTextArea;
    private JPanel result;
    private JLabel timeLabel;

    ClientUIConnect() {
        JPanel jpInput = new JPanel();
        jpInput.setPreferredSize(new Dimension(500, 120));
        inputTextArea = new JTextArea("");
        JScrollPane jspInput = new JScrollPane(inputTextArea);
        jspInput.setPreferredSize(new Dimension(400, 100));
        JButton jbt_right = new JButton("execute");
        jbt_right.setPreferredSize(new Dimension(100, 100));
        jbt_right.addActionListener(this);
        jpInput.add(jspInput, BorderLayout.CENTER);
        jpInput.add(jbt_right, BorderLayout.EAST);
        this.add(jpInput, BorderLayout.NORTH);

        result = new JPanel();
        result.setBackground(Color.white);
        result.setPreferredSize(new Dimension(600, 305));
        this.add(result, BorderLayout.CENTER);

        JPanel timePanel = new JPanel();
        timePanel.setPreferredSize(new Dimension(600, 25));
        timeLabel = new JLabel("");
        timePanel.add(timeLabel);
        this.add(timePanel, BorderLayout.SOUTH);

        init();
    }

    private void formShow(String message) {
        MyFormModel myForm = new MyFormModel(message);
        JTable jt = new JTable(myForm);
        JScrollPane jspResult = new JScrollPane(jt);
        jspResult.setPreferredSize(new Dimension(600, 280));
        result.removeAll();
        result.add(jspResult);
        result.updateUI();
    }

    private void textShow(String message) {
        int index = message.lastIndexOf('\n');
        JTextArea jt;
        try {
            timeLabel.setText(message.substring(index));
            jt = new JTextArea(message.substring(0, index));
            jt.setPreferredSize(new Dimension(600, 280));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Incorrect data from server.");
            return;
        }
        jt.setEditable(false);
        jt.setLineWrap(true);
        result.removeAll();
        result.add(jt);
        result.updateUI();
    }


    private void init() {
        this.setResizable(false);
        this.setTitle("Client");
        this.setLocation(500, 300);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 450);
        this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        Client.write("quit;");
                        Client.clean();
                        super.windowClosed(e);
                    }
                }
        );
    }

    public void actionPerformed(ActionEvent e) {
        String message = inputTextArea.getText();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please input your command.");
            return;
        }
        if (message.contains("import")) {
            String request = Client.importSQLText(message);
            if (request.length() == 0) {
                timeLabel.setText("");
                result.removeAll();
                result.updateUI();
                JOptionPane.showMessageDialog(null, "An error occurred while executing the import command.");
                return;
            } else message = request;
        }
        Client.write(message.trim());
        String responses = Client.read();
        String[] response = responses.split("\n\n");
        if (response[response.length - 1].startsWith("+-")) {
            formShow(response[response.length - 1].trim());
        } else {
            if (response[response.length - 1].contains("Quited.")) {
                Client.clean();
                this.dispose();
            }
            textShow(response[response.length - 1].trim());
        }
    }
}