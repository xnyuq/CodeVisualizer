package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MainGUI extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton convertButton;
    private JTextField textField1;
    private JButton convertButton1;
    private JTextField textField2;

    public MainGUI() {
        add(panel1);
        textField1.setForeground(Color.GRAY);
        textField1.setText("Enter source folder path");
        textField1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField1.setText("");
                textField1.setForeground(Color.BLACK);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField1.getText().isEmpty()) {
                    textField1.setForeground(Color.GRAY);
                    textField1.setText("Enter source folder path");
                }

            }
        });
        setTitle("Java Visualizer");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainGUI();
    }
}
