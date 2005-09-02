package wsmo4j;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

public class Wsmo4jFactoryCreationTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Wsmo4jFactoryCreationTest.class);
    }
    
    public void testCreateWsmo4JFactoryTwice() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
        WsmoFactory factory = Factory.createWsmoFactory(properties);
        assertNotNull(factory);
        factory = Factory.createWsmoFactory(properties);
        assertNotNull(factory);
    }

}
