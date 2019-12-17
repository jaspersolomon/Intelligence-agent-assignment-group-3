package msg;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Vector;
import  java.lang.Math ;
import  java.util.Random;
public class med extends Agent{
	
	public String matric;
	public String pass;
	public Map<String, String> cookie;
	public String[] multiReceive = {"","","","","",""};
	private String url;
	public Document doc;
	
	//assigning courses to agents
	private void distCourse(String needed_session, String course_session, int check, String url, String course_name, String matric, String pass){
		if(course_session.equals(needed_session)&&check==0) {
			
			String combine= course_name+","+url+","+matric+","+pass;
			ACLMessage combination=new ACLMessage(ACLMessage.INFORM);
			combination.setContent(combine);
			combination.addReceiver(new AID("searcher1",AID.ISLOCALNAME));
			send(combination);
			
		}
		else if(course_session.equals(needed_session)&&check==1) {
			
		
			String combine= course_name+","+url+","+matric+","+pass;
			ACLMessage combination=new ACLMessage(ACLMessage.INFORM);
			combination.setContent(combine);
			combination.addReceiver(new AID("searcher2",AID.ISLOCALNAME));
			send(combination);
			
			
		}
		else if(course_session.equals(needed_session)&&check==2) {
			
			String combine= course_name+","+url+","+matric+","+pass;
			ACLMessage combination=new ACLMessage(ACLMessage.INFORM);
			combination.setContent(combine);
			combination.addReceiver(new AID("searcher3",AID.ISLOCALNAME));
			send(combination);
			
			
		}
		else if(course_session.equals(needed_session)&&check==3) {
		
			String combine= course_name+","+url+","+matric+","+pass;
			ACLMessage combination=new ACLMessage(ACLMessage.INFORM);
			combination.setContent(combine);
			combination.addReceiver(new AID("searcher4",AID.ISLOCALNAME));
			send(combination);
			
		}
		else if(course_session.equals(needed_session)&&check==4) {
			
			String combine= course_name+","+url+","+matric+","+pass;
			ACLMessage combination=new ACLMessage(ACLMessage.INFORM);
			combination.setContent(combine);
			combination.addReceiver(new AID("searcher5",AID.ISLOCALNAME));
			send(combination);
			
		}
		else if(course_session.equals(needed_session)&&check==5) {
			
			String combine= course_name+","+url+","+matric+","+pass;
			ACLMessage combination=new ACLMessage(ACLMessage.INFORM);
			combination.setContent(combine);
			combination.addReceiver(new AID("searcher6",AID.ISLOCALNAME));
			send(combination);
		}
	}
	
	
	protected void setup() {
		
		addBehaviour(new infoRequest()); //get MatricsNumber and PassWord
		
		addBehaviour(new getCookie()); // 
		
		addBehaviour(new TickerBehaviour(this,1800000) {
			public void onStart() {}
			public void onTick() {
				addBehaviour(new giveTask());
			}
			
		});
		
		addBehaviour(new fromSearchers());
		
	}
		
	
	
private class infoRequest extends CyclicBehaviour{
		public void action() {
			ACLMessage msg = receive();
			ACLMessage psw = receive();
			if(msg != null && psw!= null) {
				matric = msg.getContent();
				pass =psw.getContent();
				
				this.myAgent.removeBehaviour(this);
			
			}
		}
	}

	private class getCookie extends CyclicBehaviour{
		public void action() {
			if(matric!= null && pass!= null) {
			crawlerAgent web = new crawlerAgent();
			doc = web.executeLogin(matric, pass);
			this.myAgent.removeBehaviour(this);
			}
		}
	}
	

	private class giveTask extends CyclicBehaviour{
		public void action() {
			
			if (doc!=null) {
				int check=0;
			
				Elements temp = doc.select("div.info");
					for (Element topic:temp){
						//get course name
						String course_name=topic.getElementsByTag("a").first().text();
						String url=topic.getElementsByTag("a").attr("href");
						//split course name
						String [] Name_check=course_name.split(" ");
						//get the session of the course
						String course_session=Name_check[Name_check.length-1];
						// get month and year
						Calendar date=Calendar.getInstance();
						int current_month=date.get(Calendar.MONTH)+1;
						int current_year=date.get(Calendar.YEAR);
						int current_next_year=date.get(Calendar.YEAR)+1;
						int current_previous_year=date.get(Calendar.YEAR)-1;
						String currentYear=Integer.toString(current_year);
						String currentNextYear=Integer.toString(current_next_year);
						String currentPreviousYear=Integer.toString(current_previous_year);
						//checking current session
						
						if(current_month==9||current_month==10||current_month==11||current_month==12) {
							String needed_session="[1-"+currentYear+"/"+currentNextYear+"]";
							 distCourse(needed_session,course_session, check, url, course_name, matric, pass);
								check++;
						}
						else if(current_month==1) {
							String needed_session="[1-"+currentPreviousYear+"/"+currentYear+"]";
							 distCourse(needed_session,course_session, check, url, course_name, matric, pass);
								check++;
							
						}
						else if(current_month==2||current_month==3||current_month==4||current_month==5||current_month==6||current_month==7) {
							String needed_session="[2-"+currentPreviousYear+"/"+currentYear+"]";
							 distCourse(needed_session,course_session, check, url, course_name,matric, pass);
								check++;
							
						}					
				}
					this.myAgent.removeBehaviour(this);
			}
		}
	}
	
	private class fromSearchers extends CyclicBehaviour{
		public void action() {
			if(doc!=null) {
				ACLMessage searcherReplies = receive();
				if (searcherReplies!= null) {
					ACLMessage toAcc = new ACLMessage(ACLMessage.INFORM);
					toAcc.addReceiver(new AID("usreq", AID.ISLOCALNAME));
							toAcc.setContent(searcherReplies.getContent());
							send(toAcc);
				}
			}
		}
	}
}