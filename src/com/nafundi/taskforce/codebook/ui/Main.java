
package com.nafundi.taskforce.codebook.ui;

import com.apple.eawt.Application;
import com.nafundi.taskforce.codebook.logic.CodebookEngine;
import com.nafundi.taskforce.codebook.logic.CodebookEntry;
import com.nafundi.taskforce.codebook.logic.CodebookMaker;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static String APP_NAME = "Task Force Codebook RC1";
    private JFrame frame;
    private JTextArea statusLog;
    private String filePath = null;

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
        frame.setBounds(100, 100, 450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setLocationRelativeTo(null);

        // TODO: Run app via double click and images don't work. java -jar
        // app.jar works though
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

        statusLog = new JTextArea();
        statusLog.setEditable(false);
        statusLog.setFont(new Font("Dialog", Font.PLAIN, 11));
        statusLog.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(21, 76, 408, 222);
        scrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(statusLog);
        panel.add(scrollPane);

        JButton selectForm = new JButton("1. Select form");
        selectForm.setBounds(40, 311, 175, 29);
        selectForm.addActionListener(new FileChooser());
        panel.add(selectForm);

        JButton generateCodebook = new JButton("2. Make codebook");
        generateCodebook.setBounds(235, 311, 175, 29);
        generateCodebook.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (filePath == null) {
                    appendToStatus("Please select a form first.");
                    return;
                } else {
                    MyTask process = new MyTask(new File(filePath));
                    process.execute();

                }
            }
        });
        panel.add(generateCodebook);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setIconImage(appLogo);

    }

    private void makeCodebook(File inputFile) {

        String filenameWithExtension = inputFile.getName();
        String inputFilename = filenameWithExtension.substring(0,
                filenameWithExtension.lastIndexOf('.'));
        String outputFolderpath = inputFile.getParentFile().getAbsolutePath();

        CodebookEngine ce = new CodebookEngine(inputFile.getAbsolutePath()) {
            @Override
            protected void process(java.util.List<String> messages) {
                for (String message : messages) {
                    appendToStatus(message);
                }
            }
        };
        HashMap<String, ArrayList<CodebookEntry>> entries = ce.doInBackground();

        if (entries != null && entries.size() > 0) {
            for (Map.Entry<String, ArrayList<CodebookEntry>> entry : entries.entrySet()) {
                CodebookMaker maker = new CodebookMaker(entry.getValue(), entry.getKey(),
                        inputFilename, outputFolderpath) {
                    @Override
                    protected void process(java.util.List<String> messages) {
                        for (String message : messages) {
                            appendToStatus(message);
                        }
                    }
                };
                maker.doInBackground();
            }
        }

    }

    private void appendToStatus(String text) {
        statusLog.setText(statusLog.getText() + text + "\n");
    }

    class FileChooser implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
                    "XML files (*.xml)", "xml");
            fileChooser.setFileFilter(xmlfilter);
            int rVal = fileChooser.showOpenDialog(frame);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                statusLog.setText("");
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                appendToStatus("Selected form: " + filePath);

                String outputFolderpath = new File(filePath).getParentFile().getAbsolutePath();
                appendToStatus("Output folder: " + outputFolderpath);

            }
        }
    }

    class MyTask extends SwingWorker {
        private File selected;

        public MyTask(File f) {
            super();
            selected = f;
        }

        protected Object doInBackground() throws Exception {
            makeCodebook(selected);
            return null;
        }
    }

}
