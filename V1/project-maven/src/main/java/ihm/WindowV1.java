package ihm;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import connectionpool.DataSource;
import connectionpool.JDBCConnectionPool;


public class WindowV1 extends JFrame implements ActionListener {
	public static boolean test = true;
	private JDBCConnectionPool jdb;
	private Connection connect;

	JFrame window = new JFrame();

	JPanel container = new JPanel();

	
	JPanel panelSelect = new JPanel();
	Button buttonSelect = new Button("Affichage des patients");
	JTextArea textResultSelect = new JTextArea(30,50);

	
	JPanel panelResult = new JPanel();
	JLabel labelResult = new JLabel("Resultat");


	JPanel panelInsert = new JPanel();
	Button buttonInsert = new Button("Ajouter un patient");
	Button buttonDeleteAll = new Button("Supprimer tous les patients");
	JLabel labelFirstname = new JLabel("Prénom");
	JTextField firstname = new JTextField(10);
	JLabel labelLastname = new JLabel("Nom");
	JTextField lastname = new JTextField(10);

	
	
	public WindowV1(JDBCConnectionPool jdbc) throws ClassNotFoundException, SQLException {
		jdb = jdbc;
		connect = OpenDatabase.databaseConnection(jdbc);
		System.out.println("Connection Used : " + connect);
		this.setTitle("Test BDD");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				DataSource.backConnection(jdb, connect);
				System.out.println("Giving back the connection");
			}
		}); 


		buttonSelect.addActionListener(this);
		panelSelect.add(buttonSelect, BorderLayout.SOUTH);
		textResultSelect.setLineWrap(true);

		
		buttonInsert.addActionListener(this);
		buttonDeleteAll.addActionListener(this);
		panelInsert.add(buttonInsert, BorderLayout.CENTER);
		panelInsert.add(buttonDeleteAll, BorderLayout.CENTER);
		panelInsert.add(labelFirstname, BorderLayout.CENTER);
		panelInsert.add(firstname, BorderLayout.CENTER);
		panelInsert.add(labelLastname, BorderLayout.CENTER);
		panelInsert.add(lastname, BorderLayout.CENTER);

		
		Font police = new Font("Trebuchet", Font.BOLD, 14);  
		textResultSelect.setFont(police);  
		textResultSelect.setForeground(Color.black); 
		panelResult.add(textResultSelect, BorderLayout.SOUTH);

		container.add(BorderLayout.NORTH, panelInsert);
		container.add(BorderLayout.SOUTH, panelSelect);
		container.add(BorderLayout.SOUTH, panelResult);


		container.setBackground(Color.white);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.setContentPane(container); 
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonSelect) {
			
			BDDSelect bddSelect = new BDDSelect(jdb, connect);
			textResultSelect.setText(bddSelect.toString());
		}

		if (e.getSource() == buttonInsert) {
			
			if (firstname.getText().isEmpty() || lastname.getText().isEmpty()){
				textResultSelect.setText("Un ou plusieurs champs sont manquants, veuillez réessayer");
			}
			
			else {
				new BDDInsert(jdb, connect, firstname.getText(), lastname.getText());
			}
			
		} 

		if (e.getSource() == buttonDeleteAll) {
			new BDDEraseAll(jdb, connect);
		} 
		
		this.setVisible(true);
	}
}