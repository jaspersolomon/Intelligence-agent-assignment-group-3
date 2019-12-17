
// agent where cookies send
package msg;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Vector;

public class searcher extends Agent{
	
	private Map<String, String> cookies;
	private String[] temp;
	private String url;
	private String course_name;
	private String getmessage;
	private Document wanted;
	private String matric;
	private String pass;
	private static Vector<String> ownMessages = new Vector<>();
	private String getAll = "";
	
	public static void tempclass(String matric, String pass, String naming,String url,File temp, String course_name) throws IOException{
		crawlerAgent takecook= new crawlerAgent();
		Map<String, String> cookies;
		
		if(temp.exists()) {
			
			FileReader fr=new FileReader(naming.substring(0, 7)+ ".txt");
			
			takecook.executeLogin(matric, pass);
			cookies=takecook.cookies;
			Document doc1=takecook.clickInCourse(cookies,url);
			Elements CourseContain=doc1.select("li.activity");
			
			Scanner sc=new Scanner(fr);
			
			for(Element coursePage:CourseContain) {
				 fr=new FileReader(naming.substring(0, 7)+ ".txt");
				 sc=new Scanner(fr);
				
				//url of the quiz, forum and files
				String urlforspecial=coursePage.getElementsByTag("a").attr("href");
				//course check of lab, quiz and forum
				String course_check=coursePage.getElementsByTag("li").attr("class");
			
				//check file 	
				String course_contain;
				
				if(course_check.equals("activity label modtype_label ")) { // pictures
				continue; //do nothing with pictures
				}
				
				else if(course_check.equals("activity folder modtype_folder ")){	//lecturers folder uploads
					course_contain=coursePage.getElementsByTag("span").next().text(); // get the name 
					int check=0;
					while(sc.hasNextLine()) { //scans file lines
						
						//has line in file
						if(course_contain.equals(sc.nextLine())) {
							
							check=1;
							break;
						} 
					}
						//does not have line in file
						if(!sc.hasNextLine()&&check==0) {
						//writes into file
						FileWriter fw = new FileWriter(naming.substring(0, 7)+ ".txt",true);
						fw.write(course_contain);
						fw.write("\n");
						fw.close();
						ownMessages.add(course_contain+" updated");
						fr=new FileReader(naming.substring(0, 7)+ ".txt");
						sc=new Scanner(fr);
						}
					}
				
				else {
					//other files and quizes
					course_contain=coursePage.getElementsByTag("span").first().text(); // gets name 
					int check=0;
					//check file again to see if anything new
					while(sc.hasNextLine()) {
						//has name in file
						if(course_contain.equals(sc.nextLine())) {
							check=1;
							break;
						} 
					}
						//does not have name in file
						if(!sc.hasNextLine()&&check==0) {
							//if file = quiz
							if(course_check.equals("activity quiz modtype_quiz ")) {
								ownMessages.add(course_contain+" updated");
					
								//login function
								crawlerAgent QuizBox = new crawlerAgent();
								QuizBox.executeLogin(matric, pass);
								cookies=QuizBox.cookies;
								Document quizBoxDoc = QuizBox.loginCourseContain(cookies,urlforspecial);
								Elements quizBoxElem = quizBoxDoc.select("div.box");
								//select course on current section
								for (Element quizBox:quizBoxElem) {
									String boxname=quizBox.getElementsByTag("div").attr("class");
									if(boxname.equals("box quizinfo")) {
									//get course name
									String quizBoxDue=quizBox.getElementsByTag("p").next().text();
									ownMessages.add(quizBoxDue);
									}
								}
							FileWriter fw = new FileWriter(naming.substring(0, 7)+ ".txt",true);
							fw.write(course_contain);
							fw.write("\n");
							fw.close();
							fr=new FileReader(naming.substring(0, 7)+ ".txt");
							sc=new Scanner(fr);
							}
							
							//file = lab
							else if(course_check.contentEquals("activity assign modtype_assign ")) {
								ownMessages.add(course_contain+" updated");
								
								//login function
								crawlerAgent LabTable = new crawlerAgent();
								LabTable.executeLogin(matric, pass);
								cookies=LabTable.cookies;
								Document labtableDoc = LabTable.loginCourseContain(cookies,urlforspecial);
								Elements labtableElem = labtableDoc.select("tr");
								
								//looping the tr tag to find duedate
								for (Element labTable:labtableElem) {
								
									String LabTableCell=labTable.getElementsByTag("td").first().text();
									if(LabTableCell.equals("Due date")) {
									//get due date
									String DueDate=labTable.getElementsByTag("td").next().text();
									ownMessages.add("Due on " + DueDate);
									break;
									}else if(!labtableElem.hasAttr("tr")) {
										ownMessages.add("NO DUE DATE");
										break;
									}
									}
								
							FileWriter fw = new FileWriter(naming.substring(0, 7)+ ".txt",true);
							fw.write(course_contain);
							fw.write("\n");
							fw.close();
							fr=new FileReader(naming.substring(0, 7)+ ".txt");
							sc=new Scanner(fr);
							break;
							}
							
							
							else {
							//normal files
							FileWriter fw = new FileWriter(naming.substring(0, 7)+ ".txt",true);
							fw.write(course_contain);
							fw.write("\n");
							fw.close();
							ownMessages.add(course_contain+" updated");
							fr=new FileReader(naming.substring(0, 7)+ ".txt");
							sc=new Scanner(fr);
							}
							}
				}
			}sc.close();
		}
		
		//if there is no file
		else if(!temp.exists()) {
			FileWriter fw = new FileWriter(naming.substring(0, 7)+ ".txt");
			takecook.executeLogin(matric, pass);
			cookies=takecook.cookies;
			crawlerAgent CoursePage=new crawlerAgent();
			Document doc1=CoursePage.clickInCourse(cookies,url);
			Elements CourseContain=doc1.select("div.activityinstance");
			for(Element coursePage:CourseContain) {
				String course_contain=coursePage.getElementsByTag("span").first().text();
				fw.write(course_contain);
				fw.write("\n");
			}
			ownMessages.add("Data missing, created new text file.");
			fw.close();
		}
	}
	
	public void setup() {
		addBehaviour(new getcourse() );
	}
	
	private class getcourse extends CyclicBehaviour {
		public void action() {
			
			ACLMessage message = receive();
			
			if(message != null ) {
				getmessage = message.getContent();
				String [] Name_check=getmessage.split(",");
				course_name=Name_check[0];
				url=Name_check[1];
				matric=Name_check[2];
				pass=Name_check[3];
				String [] NameCheck=course_name.split(" ");
				String naming=NameCheck[0];
				
				File temp=new File(naming.substring(0, 7)+ ".txt");
				 try {
					tempclass(matric,pass, naming,url,temp,course_name);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				ACLMessage toMed = message.createReply();
				
				
				for(int y = 0; y < ownMessages.size(); y++) {
						getAll+= ownMessages.get(y) +"\n ";
				}
					if(ownMessages.size()>=1) {
						toMed.setContent(course_name + "\n" +getAll);
						send(toMed);				
					}
					getAll = "";
					ownMessages.clear();
			}
		}		
	}
}



