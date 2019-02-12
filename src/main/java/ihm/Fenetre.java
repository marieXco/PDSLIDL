package ihm;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Fenetre extends JFrame implements ActionListener{
	int id = 0;
	JFrame fenetre = new JFrame();

	JPanel container = new JPanel();

	JPanel affiche = new JPanel();
	Button boutonAffiche = new Button("Affichage des patients");
	JTextArea textResult = new JTextArea();


	JPanel ajout = new JPanel();
	Button boutonAjout = new Button("Ajouter un patient");
	Button boutonSuppressionTout = new Button("Supprimer tous les patients");

	JLabel labelFirstname = new JLabel("Prénom");
	JTextField firstname = new JTextField(10);
	JLabel labelLastname = new JLabel("Nom");
	JTextField lastname = new JTextField(10);
	
	

	JPanel miseAJour = new JPanel();
//	Button boutonMAJ = new Button("Effectuer une modification");
//	Button MAJ = new Button("Modifier");

	JLabel labelFirstnameMAJ = new JLabel("Prénom");
	JTextField firstnameMAJ = new JTextField(10);
	JLabel labelLastnameMAJ = new JLabel("Nom");
	JTextField lastnameMAJ = new JTextField(10);

	Vector vec = new Vector();

	public Fenetre() {
		this.setLayout(new BorderLayout());
		this.setTitle("Test BDD");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		textResult.setLineWrap(true);

		labelFirstname.setSize(200, 150);

		boutonAffiche.addActionListener(this);
		affiche.add(boutonAffiche, BorderLayout.SOUTH);

		boutonAjout.addActionListener(this);
		boutonSuppressionTout.addActionListener(this);
		ajout.add(boutonAjout, BorderLayout.SOUTH);
		ajout.add(boutonSuppressionTout, BorderLayout.SOUTH);
		ajout.add(labelFirstname, BorderLayout.SOUTH);
		ajout.add(firstname, BorderLayout.SOUTH);
		ajout.add(labelLastname, BorderLayout.SOUTH);
		ajout.add(lastname, BorderLayout.SOUTH);

		//miseAJour.add(boutonMAJ, BorderLayout.SOUTH);
		//boutonMAJ.addActionListener(this);
		
		
		Font police = new Font("Tahoma", Font.BOLD, 11);  
		textResult.setFont(police);  
		textResult.setForeground(Color.black);  

		container.setBackground(Color.white);
		container.add(ajout, BorderLayout.NORTH);
		container.add(affiche, BorderLayout.SOUTH);
		container.add(miseAJour, BorderLayout.SOUTH);
		this.add(container, BorderLayout.CENTER);
		this.setContentPane(container); 
		this.setVisible(true);   
	}

	@Override

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == boutonAffiche) {
			BDDSelect bddSelect = new BDDSelect();
			System.out.println(bddSelect.toString());
			textResult.setText(bddSelect.toString());
			ajout.add(textResult, BorderLayout.SOUTH);
			this.getContentPane().add(textResult); 
			System.out.println(bddSelect.toString());
		}

		if (e.getSource() == boutonAjout) {
			if(firstname.getText().isEmpty() || lastname.getText().isEmpty()){
				textResult.setText("veuillez reéssayer l'ajout!");
			}
			else {
				String first;
				String last;
				id++;

				first = firstname.getText();
				last = lastname.getText();

				BDDInsert bddInsert = new BDDInsert(first, last, id);
			}


		} 

		if (e.getSource() == boutonSuppressionTout) {
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