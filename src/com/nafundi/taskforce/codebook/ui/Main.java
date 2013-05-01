package com.nafundi.taskforce.codebook.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.apple.eawt.Application;

public class Main {

	public static String APP_NAME = "Task Force Codebook";
	private JFrame frame;
	private JFormattedTextField filePath;
	private JTextArea statusLog;
	
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

	public Main() {
		initialize();
	}

	private void initialize() {

		frame = new JFrame(APP_NAME);
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setLocationRelativeTo(null);

		ImageIcon mainLogo = new javax.swing.ImageIcon(getClass().getResource(
				"/taskforce-main-logo.png"));
		Image appLogo = Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/taskforce-app-logo.png"));

		if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
			Application app = Application.getApplication();
			app.removeAboutMenuItem();
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

		filePath = new JFormattedTextField();
		filePath.setEnabled(false);
		filePath.setEditable(false);
		filePath.setBounds(140, 310, 282, 28);
		panel.add(filePath);

		JButton selectForm = new JButton("Select form");
		selectForm.setBounds(21, 311, 122, 29);
		selectForm.addActionListener(new FileChooser());

		panel.add(selectForm);

		JLabel helpText = new JLabel(
				"Select a form to generate a coding guide in the same directory.");
		helpText.setBounds(21, 338, 408, 29);
		helpText.setHorizontalAlignment(SwingConstants.CENTER);
		helpText.setFont(new Font("Dialog", Font.PLAIN, 11));
		panel.add(helpText);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setIconImage(appLogo);

	}

	private void appendToStatus(String text){
		statusLog.setText(statusLog.getText() + text + "\n");
	}
	
	
	class FileChooser implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			// Demonstrate "Open" dialog:
			int rVal = fileChooser.showOpenDialog(frame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				filePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
				appendToStatus("Selected file: " + fileChooser.getSelectedFile().getAbsolutePath());
//				selectedDir.setText(fileChooser.getCurrentDirectory().toString());
			}
		}
	}

}
