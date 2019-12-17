//pass cookies and access webpage
package msg;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import jade.core.Agent;
import jade.lang.acl.*;
import jade.core.behaviours.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class crawlerAgent{
	
	public Map<String, String> cookies;
	
	public Document executeLogin(String user, String password) {
	    try {
	    	Connection.Response loginPage = Jsoup.connect("https://smart2.ums.edu.my/login/index.php")
	                .data("username", user)
	                .data("password", password)
	                .followRedirects(true)
	                .method(Connection.Method.POST)
	                .execute();
	        
	        this.cookies = loginPage.cookies();
	        
	        Document evaluationPage = Jsoup.connect("http://smart2.ums.edu.my/")
	                .cookies(cookies)
	                .execute().parse();


	        
	       return evaluationPage;
	    } catch (IOException ioe) {
	        return null;
	    }
	}
	
	public Document clickInCourse(Map<String,String> cookies,String url) {
		 try { 
			    Document evaluationPage = Jsoup.connect(url)
		        		.cookies(cookies)
		                .execute().parse();
		        
		       return evaluationPage;
		    } catch (IOException ioe) {
		        return null;
		    }
	}
	public Document loginCourseContain(Map<String,String> cookies,String urlforquiz) {
		 try {
			  Document evaluationPage = Jsoup.connect(urlforquiz).cookies(cookies).execute().parse();

		       return evaluationPage;
		    } catch (IOException ioe) {
		        return null;
		    }
	}
	

	

}