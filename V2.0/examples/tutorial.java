import java.util.*;
public class JavaAPI {
	public static void main(String[] args) {
	/*
	 * This will initiate the API with the endpoint and your access key.
	 *
	 */
	Mailin http = new Mailin("https://api.sendinblue.com/v2.0","Your access key");

	/** Prepare variables for easy use **/

		Map < String, String > to = new HashMap < String, String > ();
			to.put("to@example.net", "to whom!");
		Map < String, String > cc = new HashMap < String, String > ();
			cc.put("cc@example.net", "cc whom!");
		Map < String, String > bcc = new HashMap < String, String > ();
			bcc.put("bcc@example.net", "bcc whom!");
		Map < String, String > headers = new HashMap < String, String > ();
			headers.put("Content-Type", "text/html; charset=iso-8859-1");
			headers.put("X-param1", "value1");
			headers.put("X-param2", "value2");
			headers.put("X-Mailin-custom", "my custom value");
			headers.put("X-Mailin-IP", "102.102.1.2");
			headers.put("X-Mailin-Tag", "My tag");

		Map < String, Object > data = new HashMap < String, Object > ();
			data.put("to", to);
			data.put("cc", cc);
			data.put("bcc", bcc);
			data.put("replyto", new String [] {"replyto@email.com","reply to!"});
			data.put("from", new String [] {"from@email.com","from email!"});
			data.put("subject", "My subject");
			data.put("html", "This is the <h1>HTML</h1>");
			data.put("text", "This is text");
			data.put("attachment", new String [] {});
			data.put("headers", headers);
			data.put("inline_image", new String [] {});

		String str = http.send_email(data);
		System.out.println(str);
	}
}