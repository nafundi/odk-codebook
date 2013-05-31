
package com.nafundi.taskforce.codebook.ui;

import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static String APP_NAME = "Task Force Codebook";
    private JFrame frame;
    private JFormattedTextField filePath;
    private JTextArea statusLog;
    private HashMap<String, String> metadata;

    public Main() {
        initialize();
    }

    public static void main(String[] args) {

        if (System.getProperty("os.name").contains("Mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name", APP_NAME);

        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize() {

        frame = new JFrame(APP_NAME);
        frame.setResizable(false);
        frame.setBounds(100, 100, 450, 175);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setLocationRelativeTo(null);

        ImageIcon mainLogo = new javax.swing.ImageIcon("res/taskforce-main-logo.png");
        Image appLogo = Toolkit.getDefaultToolkit().getImage("res/taskforce-app-logo.png");

        if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
            Application app = Application.getApplication();
            app.setDockIconImage(appLogo);
        }

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel logo = new JLabel(mainLogo);
        logo.setBounds(21, 10, 408, 58);
        panel.add(logo);

        filePath = new JFormattedTextField();
        filePath.setEnabled(false);
        filePath.setEditable(false);
        filePath.setBounds(140, 76, 282, 28);
        panel.add(filePath);

        JButton selectForm = new JButton("Select form");
        selectForm.setBounds(21, 77, 122, 29);
        selectForm.addActionListener(new FileChooser());

        panel.add(selectForm);

        JLabel helpText = new JLabel(
                "Select a form to generate a coding guide in the same directory.");
        helpText.setBounds(21, 104, 408, 29);
        helpText.setHorizontalAlignment(SwingConstants.CENTER);
        helpText.setFont(new Font("Dialog", Font.PLAIN, 11));
        panel.add(helpText);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setIconImage(appLogo);

        //TODO: where do we put error messages?
    }

    private void makeCodebook(File inputFile) {

        String filenameWithExtension = inputFile.getName();
        String inputFilename = filenameWithExtension.substring(0, filenameWithExtension.lastIndexOf('.'));
        String outputFolderpath = inputFile.getParentFile().getAbsolutePath();

        CodebookEngine ce = new CodebookEngine();
        HashMap<String, ArrayList<CodebookEntry>> entries = ce
                .loadForm(inputFile.getAbsolutePath());

        for (Map.Entry<String, ArrayList<CodebookEntry>> entry : entries.entrySet()) {

            CodebookMaker cm = new CodebookMaker(entry.getValue(), entry.getKey(), inputFilename, outputFolderpath);
            cm.makeCodebook();

        }


    }

    class FileChooser implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int rVal = fileChooser.showOpenDialog(frame);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                //TODO: This doesn't update quickly
                filePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                makeCodebook(new File(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        }
    }


}
