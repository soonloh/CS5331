import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.XMLResource;
public class App {
    protected static String DRIVER = "org.exist.xmldb.DatabaseImpl";
    protected static String URI = "xmldb:exist://13.70.19.195:8080/exist/xmlrpc";
    protected static String collectionPath = "/db/vulnapp/";
    protected static String resourceName = "Heat.xml";

    public static void main(String[] args) throws Exception {

        // initialize database driver
        Class cl = Class.forName(DRIVER);
        Database database = (Database) cl.newInstance();
        DatabaseManager.registerDatabase(database);

        // get the collection
        Collection col = DatabaseManager.getCollection(URI + collectionPath);

        // get the content of a document
        System.out.println("Get the content of " + resourceName);
        XMLResource res = (XMLResource) col.getResource(resourceName);
        if (res == null) {
            System.out.println("document not found!");
        } else {
            System.out.println(res.getContent());
        }
    }
}
