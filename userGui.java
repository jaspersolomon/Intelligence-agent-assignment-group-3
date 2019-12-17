package msg;
//get pass and matric
import jade.core.AID;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class userGui extends JFrame {	
	
	private usreq myAgent;
	
	private JTextField MatricF, PassF;
	
	userGui(usreq a) {
		super(a.getLocalName());
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		p.add(new JLabel("Matric Number:"));
		MatricF = new JTextField(15);
		p.add(MatricF);
		p.add(new JLabel("Password:"));
		PassF = new JPasswordField(15);
		p.add(PassF);
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton subButton = new JButton("Ok");
		subButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String MatNo = MatricF.getText().trim();
					String Pass = PassF.getText().trim();
					a.setInfo(MatNo, Pass);
					setVisible(false);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(userGui.this, "Invalid values. "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
				}
			}
		} );
		
		p = new JPanel();
		p.add(subButton);
		getContentPane().add(p, BorderLayout.SOUTH);
		
		// Make the agent terminate when the user closes 
		// the GUI using the button on the upper right corner	
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(false);
	}
	
	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}