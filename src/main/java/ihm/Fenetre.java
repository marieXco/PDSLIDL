
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Fenetre extends JFrame implements ActionListener{
	int id = 0;
	JFrame window = new JFrame();

	//Main Panel
	JPanel container = new JPanel();

	//Panel that contains the tools for the select instruction 
	JPanel panelSelect = new JPanel();
	Button buttonSelect = new Button("Affichage des patients");
	JTextArea textResultSelect = new JTextArea(30,50);

	//Panel that contains the result of the select instruction
	JPanel panelResult = new JPanel();
	JLabel labelResult = new JLabel("Resultat");

	//Panel that contains the tools for the insert and delete instructions
	JPanel panelInsert = new JPanel();
	Button buttonInsert = new Button("Ajouter un patient");
	Button buttonDeleteAll = new Button("Supprimer tous les patients");
	JLabel labelFirstname = new JLabel("Prénom");
	JTextField firstname = new JTextField(10);
	JLabel labelLastname = new JLabel("Nom");
	JTextField lastname = new JTextField(10);

	//Panel that contains the tools for the update instructions
	JPanel panelUpdate = new JPanel();
	Button buttonUpdate = new Button("Effectuer une modification");
	Button buttonMakeUpdate = new Button("Modifier");
	JLabel labelFirstnameMAJ = new JLabel("Prénom");
	JTextField firstnameMAJ = new JTextField(10);
	JLabel labelLastnameMAJ = new JLabel("Nom");
	JTextField lastnameMAJ = new JTextField(10);


	public Fenetre() {
		// Window initialisation
		this.setTitle("Test BDD");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		// Listener for the button that triggers our select instruction
		buttonSelect.addActionListener(this);
		panelSelect.add(buttonSelect, BorderLayout.SOUTH);
		textResultSelect.setLineWrap(true);

		// Listener for the button that triggers our insert and delete instruction
		buttonInsert.addActionListener(this);
		buttonDeleteAll.addActionListener(this);
		panelInsert.add(buttonInsert, BorderLayout.CENTER);
		panelInsert.add(buttonDeleteAll, BorderLayout.CENTER);
		panelInsert.add(labelFirstname, BorderLayout.CENTER);
		panelInsert.add(firstname, BorderLayout.CENTER);
		panelInsert.add(labelLastname, BorderLayout.CENTER);
		panelInsert.add(lastname, BorderLayout.CENTER);

		//Adding style to the result of the select and adding it to our panelResult
		Font police = new Font("Tahoma", Font.BOLD, 14);  
		textResultSelect.setFont(police);  
		textResultSelect.setForeground(Color.black); 
		panelResult.add(textResultSelect, BorderLayout.SOUTH);

		//miseAJour.add(boutonMAJ, BorderLayout.SOUTH);
		//boutonMAJ.addActionListener(this);

		//Adding our panels to the main one
		container.add(BorderLayout.NORTH, panelInsert);
		container.add(BorderLayout.SOUTH, panelSelect);
		//container.add(BorderLayout.SOUTH, panelUpdate);
		container.add(BorderLayout.SOUTH, panelResult);


		// Styling our main panel
		container.setBackground(Color.white);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.setContentPane(container); 

		this.setVisible(true);   
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonSelect) {
			// Calling BDDSelect that contains our sql select instruction 
			BDDSelect bddSelect = new BDDSelect();

			// Adding the result of the select to our TextArea
			textResultSelect.setText(bddSelect.toString());
		}

		if (e.getSource() == buttonInsert) {
			if(firstname.getText().isEmpty() || lastname.getText().isEmpty()){
				textResultSelect.setText("Un ou plusieurs champs sont manquants, veuillez réessayer");
			}
			else {
				String first;
				String last;
				id++;

				first = firstname.getText();
				last = lastname.getText();

				// Calling BDDInsert that contains our sql insert instruction with our new values
				BDDInsert bddInsert = new BDDInsert(first, last, id);
			}


		} 

		if (e.getSource() == buttonDeleteAll) {
			// Calling BDDEraseAll that contains our sql delete all instruction
			BDDEraseAll bddDeleteAll = new BDDEraseAll();
		} 

		/*if (e.getSource() == boutonMAJ) {	

				miseAJour.add(labelFirstnameMAJ, BorderLayout.SOUTH);
				miseAJour.add(firstnameMAJ, BorderLayout.SOUTH);
				miseAJour.add(MAJ, BorderLayout.SOUTH);
				MAJ.addActionListener(this);

		} */

		//		if (e.getSource() == MAJ) {	
		//			String firstMAJ;	
		//			
		//			
		//			Object valeur = combo.getSelectedItem();
		//			String firstBeforeMAJ = (String) valeur;
		//			
		//			firstMAJ = firstnameMAJ.getText();
		//			BDDUpdate bddUpdate = new BDDUpdate(firstMAJ, firstBeforeMAJ);	
		//			textResult.setText(bddUpdate.toString());
		//			ajout.add(textResult, BorderLayout.SOUTH);
		//			this.getContentPane().add(textResult); 
		//			System.out.println(bddUpdate.toString());
		//		
		//	} 

		this.setVisible(true);
	}

}