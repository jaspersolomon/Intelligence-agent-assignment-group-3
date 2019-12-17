// agent send user name and pass
package msg;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;
import java.io.IOException;
import javax.swing.JOptionPane;

public class usreq extends Agent{
	
	private String user;
	private String password;
	public usreq b;
	private String[] fromMed = {"","","","","",""};
	
	void setInfo(String user, String password) {
		this.user = user;
		this.password = password;
	}	
	
protected void setup() {
		
		userGui gui = new userGui(this);
		gui.showGui();
		
		
		addBehaviour(new request());
		addBehaviour(new medRE());
	}

		private class request extends CyclicBehaviour {
			public void action() {
				if (user!= null && password != null) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				ACLMessage psw = new ACLMessage(ACLMessage.INFORM);
				msg.setContent(user); 
				psw.setContent(password);
				msg.addReceiver(new AID("med", AID.ISLOCALNAME));
				psw.addReceiver(new AID("med", AID.ISLOCALNAME));
				send(msg);
				send(psw);
				this.myAgent.removeBehaviour(this);
				}
			}
		}
		
		
		private class medRE extends CyclicBehaviour{
			public void action() {
				ACLMessage medMessage = receive();
				if (medMessage != null) {
					JOptionPane.showMessageDialog(null, medMessage.getContent());
				}
			}
		}
}