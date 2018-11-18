import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XPathQueryService;

import java.util.regex.Pattern;

public class LoginBypass {
    protected static String DRIVER = "org.exist.xmldb.DatabaseImpl";
    protected static String URI = "xmldb:exist://13.70.19.195:8080/exist/xmlrpc";
    protected static String collectionPath = "/db/vulnapp/";
    protected static String resourceName = "user.xml";
    public static Collection col = null;

    public static void main(String[] args) throws Exception {
        Class cl = Class.forName(DRIVER);
        Database database = (Database) cl.newInstance();
        DatabaseManager.registerDatabase(database);

        // get the collection
        col = DatabaseManager.getCollection(URI + collectionPath);

        try {
            exploit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void  exploit() throws Exception{
        //Normal Username
        String username = "mjane";
        //Exploit Payload
        //username = "mjane\" or .=\"";
        String password = "randompassword>";

        if (login_validate(username, password))
            System.out.println("Login Success");
        else
            System.out.println("Login Failed");

    }
    public static boolean login_validate (String username, String password) throws Exception{

        Pattern username_whitelist_pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
        Pattern password_blacklist_pattern = Pattern.compile("['\"<>()&%]");

        if(!username_whitelist_pattern.matcher(username).matches()){
            throw new Exception("Username need to be in alphanumeric characters only");
        }
        if(password_blacklist_pattern.matcher(password).find()){
            throw new Exception("Password must not contain the blacklisted characters.");
        }

        // query a document
        String xQuery = "doc(\"user.xml\")/userlist/user[uname=\""+username+"\" and password=\""+password+"\"]";
        //System.out.println("Execute xQuery = " + xQuery);

        XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "3.1");

        xpqs.setProperty("indent", "yes");

        // Execute the query, print the result
        ResourceSet result = xpqs.query(xQuery);
        ResourceIterator i = result.getIterator();
//        while (i.hasMoreResources()) {
//            Resource r = i.nextResource();
//            System.out.println((String) r.getContent());
//        }
        return i.hasMoreResources();
    }

    public static boolean login (String username, String password) throws Exception{

        // query a document
        String xQuery = "doc(\"user.xml\")/userlist/user[uname=\""+username+"\" and password=\""+password+"\"]";
        //System.out.println("Execute xQuery = " + xQuery);

        XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "3.0");
        xpqs.setProperty("indent", "yes");

        // Execute the query, print the result
        ResourceSet result = xpqs.query(xQuery);
        ResourceIterator i = result.getIterator();
//        while (i.hasMoreResources()) {
//            Resource r = i.nextResource();
//            System.out.println((String) r.getContent());
//        }
        return i.hasMoreResources();
    }
}
