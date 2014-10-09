import java.util.*;
public class JavaAPI {
	public static void main(String[] args) {
	/*
	 * This will initiate the API with the endpoint and your access and secret key.
	 *
	 */
	Mailin http = new Mailin("https://api.sendinblue.com/v1.0","Your access key","Your secret key");

	/** Prepare variables for easy use **/

	Map < String, String > to = new HashMap < String, String > ();
		to.put("to@example.net", "to whom!");
	Map < String, String > cc = new HashMap < String, String > ();
		cc.put("cc@example.net", "Sachin Kumar");
	Map < String, String > bcc = new HashMap < String, String > ();
		bcc.put("bcc@example.net", "bcc whom!");
	Map < String, String > headers = new HashMap < String, String > ();
		headers.put("Content-Type", "text/html; charset=iso-8859-1");
		headers.put("X-Ewiufkdsjfhn", "hello");
		headers.put("X-Mailin-custom", "Custom");

	String str = http.send_email(to,"My subject",new String [] {"from@email.com","from email!"},"This is the <h1>HTML</h1>","This is the text",cc,bcc,new String [] {"replyto@email.com","reply to!"},new String [] {},headers);

	System.out.println(str);
	}
}