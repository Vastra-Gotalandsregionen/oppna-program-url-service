package se.vgregion.urlservice.inttest;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;


public class IntegrationTestTemplate {

    private Server server;
    protected URI hubUrl;
    
    @Before
    public void setUpComponents() throws Exception {
        System.setProperty("testproperties", "classpath:integrationtest.properties");
        
        server = new Server(0);
        
        WebAppContext context = new WebAppContext();
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("src/main/webapp");
        context.setContextPath("/");
 
        server.setHandler(context);
        
        server.start();
        
        hubUrl = URI.create("http://localhost:" + server.getConnectors()[0].getLocalPort());
    }
    
    @After
    public void stopServer() throws Exception {
        server.stop();
    }
}
