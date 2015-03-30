import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
import java.security.SignatureException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 
public class Mailin {
 
    private final String USER_AGENT = "Mozilla/5.0";
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    private String base_url;
    private String access_key;
    private String secret_key;
    
    public Mailin(String base_url, String access_key, String secret_key) {
        this.base_url = base_url;
        this.access_key = access_key;
        this.secret_key = secret_key;
    }
    
    public String do_request(String resource, String method, String input) throws Exception {
        String url = base_url + "/" + resource;
        String key = access_key;
        String secret = secret_key;
        String md5_content = input != "" ? MD5(input) : "";
        String content_type = "application/json";  
        String sign_string = method+"\n"+md5_content+"\n"+content_type+"\n"+url;
        byte ptext[] = sign_string.getBytes("ISO-8859-1"); 
        String new_sign_string = new String(ptext, "UTF-8"); 
        String auth_header = ""+ key + ":" + hmacSha1(new_sign_string,secret);
        String content_header = "application/json";
        
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestProperty("Authorization", auth_header);
        con.setRequestProperty("Content-Type", content_header);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod(method);
		con.setUseCaches(false);
		
 		if (input != "" && method != "GET") {
            DataOutputStream outStream = new DataOutputStream(con.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
            writer.write(input);
            writer.flush();
            writer.close();
            outStream.close();
		}
		
        int responseCode = con.getResponseCode();
        String inputLine;
        StringBuffer response = new StringBuffer();
        BufferedReader in;

        if (responseCode > 200) {
            in = new BufferedReader(
            new InputStreamReader(con.getErrorStream()));
        } else {
            in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        }

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
 
public String get(String resource, String input) {
try {
return do_request(resource, "GET", input);
} catch (Exception e) {

}
return null;
}

public String put(String resource, String input) {
    try {
    return do_request(resource, "PUT", input);
    } catch (Exception e) {

    }
    return null;
}

public String post(String resource, String input) {
    try {
    return do_request(resource, "POST", input);
    } catch (Exception e) {

    }
    return null;
}

public String delete(String resource, String input) {
    try {
    return do_request(resource, "DELETE", input);
    } catch (Exception e) {

    }
    return null;
}


public String get_account() {
    return get("account", "");
}

public String get_smtp_details() {
    return get("account/smtpdetail", "");
}

public String create_child_account(String email, String password, String company_org, String first_name, String last_name, Object credits, String [] associate_ip) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("child_email", email);
    map.put("password", password);
    map.put("company_org", company_org);
    map.put("first_name", first_name);
    map.put("last_name", last_name);
    map.put("credits", credits);
    map.put("associate_ip", associate_ip);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("account", json);
}
public String update_child_account(String child_authkey, String company_org, String first_name, String last_name, String password, String [] associate_ip, String [] disassociate_ip) {
    Map < String, String > map = new HashMap < String, String > ();
    map.put("auth_key", child_authkey);
    map.put("company_org", company_org);
    map.put("first_name", first_name);
    map.put("last_name", last_name);
    map.put("password", password);
    map.put("associate_ip", associate_ip);
    map.put("disassociate_ip", disassociate_ip);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("account", json);
}
public String delete_child_account(String child_authkey) {
    return delete("account/" + child_authkey, "");
}

public String get_child_account(String child_authkey) {
    Map < String, String > map = new HashMap < String, String > ();
    map.put("auth_key", child_authkey);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("account/getchild", json);
}

public String add_remove_child_credits(String child_authkey, Object add_credits, Object remove_credits) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("auth_key", child_authkey);
    map.put("add_credit", add_credits);
    map.put("rmv_credit", remove_credits);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("account/addrmvcredit", json);
}

public String send_sms(String to, String from, String text, String web_url, String tag, String type) {
    Map < String, String > map = new HashMap < String, String > ();
    map.put("text", text);
    map.put("tag", tag);
    map.put("web_url", web_url);
    map.put("from", from);
    map.put("to", to);
    map.put("type", type);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("sms", json);
}

public String create_sms_campaign(String camp_name, String sender, String content, String bat_sent, int [] listids, int [] exclude_list, String scheduled_date) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("name", camp_name);
    map.put("sender", sender);
    map.put("content", content);
    map.put("bat", bat_sent);
    map.put("listid", listids);
    map.put("exclude_list", exclude_list);
    map.put("scheduled_date", scheduled_date);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("sms", json);
}

public String update_sms_campaign(int id, String camp_name, String sender, String content, String bat_sent, int [] listids, int [] exclude_list, String scheduled_date) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("name", camp_name);
    map.put("sender", sender);
    map.put("content", content);
    map.put("bat", bat_sent);
    map.put("listid", listids);
    map.put("exclude_list", exclude_list);
    map.put("scheduled_date", scheduled_date);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("sms/" + id, json);
}

public String send_bat_sms(String campid, String mobilephone) {
    return get("sms/" + campid+"/"+mobilephone, "");
}

public String get_campaigns(String type, String status, String page, String page_limit) {
    String url = "";
    if (type == "" && status == "" && page == "" && page_limit == "") {
        url = "campaign/";
    } else {
        url = "campaign/type/" + type + "/status/" + status + "/page/" + page + "/page_limit/" + page_limit + "/";
    }
    return get(url,"");
}

public String get_campaign(int id) {
    return get("campaign/" + id, "");
}

public String create_campaign(String category, String from_name, String name, String bat_sent, String html_content, String html_url, int [] listid, String scheduled_date, String subject, String from_email, String reply_to, String to_field, int [] exclude_list, String attachmentUrl, int inline_image) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("category", category);
    map.put("from_name", from_name);
    map.put("name", name);
    map.put("bat", bat_sent);
    map.put("html_content", html_content);
    map.put("html_url", html_url);
    map.put("listid", listid);
    map.put("scheduled_date", scheduled_date);
    map.put("subject", subject);
    map.put("from_email", from_email);
    map.put("reply_to", reply_to);
    map.put("to_field", to_field);
    map.put("exclude_list", exclude_list);
    map.put("attachment_url", attachmentUrl);
    map.put("inline_image", inline_image);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("campaign", json);
}

public String delete_campaign(int id) {
    return delete("campaign/" + id, "");
}

public String get_processes() {
    return get("process", "");
}

public String update_campaign(int id, String category, String from_name, String name, String bat_sent, String html_content, String html_url, int [] listid, String scheduled_date, String subject, String from_email, String reply_to, String to_field, int [] exclude_list, String attachmentUrl, int inline_image) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("category", category);
    map.put("from_name", from_name);
    map.put("name", name);
    map.put("bat", bat_sent);
    map.put("html_content", html_content);
    map.put("html_url", html_url);
    map.put("listid", listid);
    map.put("scheduled_date", scheduled_date);
    map.put("subject", subject);
    map.put("from_email", from_email);
    map.put("reply_to", reply_to);
    map.put("to_field", to_field);
    map.put("exclude_list", exclude_list);
    map.put("attachment_url", attachmentUrl);
    map.put("inline_image", inline_image);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("campaign/" + id, json);
}

public String campaign_report_email(int id, String lang, String email_subject, String [] email_to, String email_content_type, String [] email_bcc, String [] email_cc, String email_body) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("lang", lang);
    map.put("email_subject", email_subject);
    map.put("email_to", email_to);
    map.put("email_content_type", email_content_type);
    map.put("email_bcc", email_bcc);
    map.put("email_cc", email_cc);
    map.put("email_body", email_body);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("campaign/" + id + "/report", json);
}

public String campaign_recipients_export(int id, String notify_url, String type) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("notify_url", notify_url);
    map.put("type", type);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("campaign/" + id + "/recipients", json);
}

public String send_bat_email(String campid, String [] email_to) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("emails", email_to);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("campaign/" + campid + "/test", json);
}

public String create_trigger_campaign(String category, String from_name, String name, String bat_sent, String html_content, String html_url, int [] listid, String scheduled_date, String subject, String from_email, String reply_to, String to_field, int [] exclude_list, int recurring, String attachmentUrl, int inline_image) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("category", category);
    map.put("from_name", from_name);
    map.put("trigger_name", name);
    map.put("bat", bat_sent);
    map.put("html_content", html_content);
    map.put("html_url", html_url);
    map.put("listid", listid);
    map.put("scheduled_date", scheduled_date);
    map.put("subject", subject);
    map.put("from_email", from_email);
    map.put("reply_to", reply_to);
    map.put("to_field", to_field);
    map.put("exclude_list", exclude_list);
    map.put("recurring", recurring);
    map.put("attachment_url", attachmentUrl);
    map.put("inline_image", inline_image);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("campaign", json);
}

public String update_trigger_campaign(int id, String category, String from_name, String name, String bat_sent, String html_content, String html_url, int [] listid, String scheduled_date, String subject, String from_email, String reply_to, String to_field, int [] exclude_list, int recurring, String attachmentUrl, int inline_image) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("category", category);
    map.put("from_name", from_name);
    map.put("trigger_name", name);
    map.put("bat", bat_sent);
    map.put("html_content", html_content);
    map.put("html_url", html_url);
    map.put("listid", listid);
    map.put("scheduled_date", scheduled_date);
    map.put("subject", subject);
    map.put("from_email", from_email);
    map.put("reply_to", reply_to);
    map.put("to_field", to_field);
    map.put("exclude_list", exclude_list);
    map.put("recurring", recurring);
    map.put("attachment_url", attachmentUrl);
    map.put("inline_image", inline_image);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("campaign/" + id, json);
}

public String campaign_share_link(int [] campaign_ids) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("camp_ids", campaign_ids);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("campaign/sharelink", json);
}

public String update_campaign_status(int id, String status) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("status", status);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("campaign/" + id + "/updatecampstatus", json);
}

public String get_process(int id) {
    return get("process/" + id, "");
}

public String get_lists() {
    return get("list", "");
}

public String get_list(int id) {
    return get("list/" + id, "");
}

public String create_list(String list_name, int list_parent) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("list_name", list_name);
    map.put("list_parent", list_parent);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("list", json);
}

public String delete_list(int id) {
    return delete("list/" + id, "");
}

public String update_list(int id, String list_name, int list_parent) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("list_name", list_name);
    map.put("list_parent", list_parent);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("list/" + id, json);
}

public String display_list_users(int [] listids, int page, int page_limit) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("listids", listids);
    map.put("page", page);
    map.put("page_limit", page_limit);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("list/display", json);
}

public String add_users_list(int id, String [] users) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("users", users);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("list/" + id + "/users", json);
}

public String delete_users_list(int id, String [] users) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("users", users);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return delete("list/" + id + "/delusers", json);
}

public String send_email(Object to, String subject, String [] from, String html, String text, Object cc, Object bcc, String [] replyto, Object attachment, Object headers) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("cc", cc);
    map.put("text", text);
    map.put("bcc", bcc);
    map.put("replyto", replyto);
    map.put("html", html);
    map.put("to", to);
    map.put("attachment", attachment);
    map.put("from", from);
    map.put("subject", subject);
    map.put("headers", headers);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("email", json);
}

public String get_webhooks() {
    return get("webhook", "");
}
public String get_webhook(int id) {
    return get("webhook/" + id, "");
}

public String create_webhook(String url, String description, String [] events) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("url", url);
    map.put("description", description);
    map.put("events", events);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("webhook", json);
}

public String delete_webhook(int id) {
    return delete("webhook/" + id, "");
}

public String update_webhook(int id, String url, String description, String [] events) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("url", url);
    map.put("description", description);
    map.put("events", events);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("webhook/" + id, json);
}
public String get_statistics(int aggregate, String tag, int days, String end_date, String start_date) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("aggregate", aggregate);
    map.put("tag", tag);
    map.put("days", days);
    map.put("end_date", end_date);
    map.put("start_date", start_date);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("statistics", json);
}

public String get_user(String email) {
    return get("user/" + email, "");
}

public String create_user(Object attributes, int blacklisted, String email, int [] listid) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("attributes", attributes);
    map.put("blacklisted", blacklisted);
    map.put("email", email);
    map.put("listid", listid);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("user", json);
}

public String delete_user(String email) {
    return delete("user/" + email, "");
}

public String update_user(String email, Object attributes, int blacklisted, int [] listid, int [] listid_unlink) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("attributes", attributes);
    map.put("blacklisted", blacklisted);
    map.put("listid", listid);
    map.put("listid_unlink", listid_unlink);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("user/" + email, json);
}

public String import_users(String url, int [] listids, String notify_url, String name, int folder_id) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("url", url);
    map.put("listids", listids);
    map.put("notify_url", notify_url);
    map.put("name", name);
    map.put("list_parent", folder_id);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("user/import", json);
}

public String export_users(String export_attrib, String filter, String notify_url) {
    Map < String, String > map = new HashMap < String, String > ();
    map.put("export_attrib", export_attrib);
    map.put("filter", filter);
    map.put("notify_url", notify_url);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("user/export", json);
}

public String create_update_user(String email, Object attributes, int blacklisted, int [] listid, int [] listid_unlink, int blacklisted_sms) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("email", email);
    map.put("attributes", attributes);
    map.put("blacklisted", blacklisted);
    map.put("listid", listid);
    map.put("listid_unlink", listid_unlink);
    map.put("blacklisted_sms", blacklisted_sms);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("user/createdituser", json);
}

public String get_attributes() {
    return get("attribute", "");
}

public String get_attribute(String type) {
    return get("attribute/" + type, "");
}

public String create_attribute(String type, Object data) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("type", type);
    map.put("data", data);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("attribute", json);
}

public String delete_attribute(String type, String [] data) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("data", data);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("attribute/" + type, json);
}

public String get_report(int limit, String start_date, String end_date, int offset, String date, String days, String email) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("limit", limit);
    map.put("start_date", start_date);
    map.put("end_date", end_date);
    map.put("offset", offset);
    map.put("date", date);
    map.put("days", days);
    map.put("email", email);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("report", json);
}

public String get_folders() {
    return get("folder", "");
}

public String get_folder(int id) {
    return get("folder/" + id, "");
}

public String create_folder(String name) {
    Map < String, String > map = new HashMap < String, String > ();
    map.put("name", name);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("folder", json);
}

public String delete_folder(int id) {
    return delete("folder/" + id, "");
}

public String update_folder(int id, String name) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("name", name);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("folder/" + id, json);
}

public String delete_bounces(String start_date, String end_date, String email) {
    Map < String, String > map = new HashMap < String, String > ();
    map.put("start_date", start_date);
    map.put("end_date", end_date);
    map.put("email", email);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("bounces", json);
}

public String send_transactional_template(int id, String to, String cc, String bcc, Object attr, String attachmentUrl, Object attachment) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("cc", cc);
    map.put("to", to);
    map.put("attr", attr);
    map.put("bcc", bcc);
    map.put("attachment_url", attachmentUrl);
    map.put("attachment", attachment);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("template/" + id, json);
}

public String create_template(String from_name, String name, String bat_sent, String html_content, String html_url, String subject, String from_email, String reply_to, String to_field, int status, int attach) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("from_name", from_name);
    map.put("template_name", name);
    map.put("bat", bat_sent);
    map.put("html_content", html_content);
    map.put("html_url", html_url);
    map.put("subject", subject);
    map.put("from_email", from_email);
    map.put("reply_to", reply_to);
    map.put("to_field", to_field);
    map.put("status", status);
    map.put("attachment", attach);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("template", json);
}

public String update_template(int id, String from_name, String name, String bat_sent, String html_content, String html_url, String subject, String from_email, String reply_to, String to_field, int status, int attach) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("from_name", from_name);
    map.put("template_name", name);
    map.put("bat", bat_sent);
    map.put("html_content", html_content);
    map.put("html_url", html_url);
    map.put("subject", subject);
    map.put("from_email", from_email);
    map.put("reply_to", reply_to);
    map.put("to_field", to_field);
    map.put("status", status);
    map.put("attachment", attach);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("template/" + id, json);
}

public String get_senders(String option) {
	String url = "";
    if (option == "") {
    	url = "advanced/";
    } else {
    	url = "advanced/option/"+option+"/";
    }
    return get(url, "");
}

public String create_sender(String sender_name, String sender_email, String [] ip_domain) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("name", sender_name);
    map.put("email", sender_email);
    map.put("ip_domain", ip_domain);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return post("advanced", json);
}

public String update_sender(int id, String sender_name, String sender_email, String [] ip_domain) {
    Map < String, Object > map = new HashMap < String, Object > ();
    map.put("name", sender_name);
    map.put("email", sender_email);
    map.put("ip_domain", ip_domain);
    Gson gson = new Gson();
    String json = gson.toJson(map);
    return put("advanced/" + id, json);
}

public String delete_sender(int id) {
    return delete("advanced/" + id, "");
}



 public String MD5(String md5) {
   try {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] array = md.digest(md5.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
       }
        return sb.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
    }
    return null;
}
public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}
public static String hmacSha1(String value, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(value.getBytes());   
            String str = bytesToHex(rawHmac);
            String result = DatatypeConverter.printBase64Binary(str.toLowerCase().getBytes("UTF-8"));
            return result;
} catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}