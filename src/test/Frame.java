package test;

import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JTextField;

public class Frame {

	private JFrame frame;
	private JTextField javaSourcePathField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame window = new Frame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Frame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 0));
		
		JPanel leftPanel = new JPanel();
		frame.getContentPane().add(leftPanel);
		leftPanel.setLayout(null);
		
		JButton javaSourceButton = new JButton("Choose Java file");
		javaSourceButton.setBounds(10, 11, 89, 23);
		leftPanel.add(javaSourceButton);
		
		javaSourcePathField = new JTextField();
		javaSourcePathField.setEditable(false);
		javaSourcePathField.setEnabled(true);
		javaSourcePathField.setBounds(109, 12, 86, 20);
		leftPanel.add(javaSourcePathField);
		javaSourcePathField.setColumns(10);
		
		JPanel rightPanel = new JPanel();
		frame.getContentPane().add(rightPanel);
		rightPanel.setLayout(new GridLayout(2, 1, 0, 0));
		
		JScrollPane upperScrollPane = new JScrollPane();
		rightPanel.add(upperScrollPane);
		
		JTextArea upperTextArea = new JTextArea();
		upperScrollPane.setViewportView(upperTextArea);
		
		JScrollPane lowerScrollPane = new JScrollPane();
		rightPanel.add(lowerScrollPane);
		
		JTextArea lowerTextArea = new JTextArea();
		lowerScrollPane.setViewportView(lowerTextArea);
	}
}
