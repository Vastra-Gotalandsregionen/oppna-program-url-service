package se.vgregion.urlservice.inttest;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Ignore;
import org.junit.Test;

public class IntegrationTest extends IntegrationTestTemplate {

    @Test
    @Ignore // TODO works in Eclipse but not in Maven
    public void test() throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(hubUrl.toString() + "/shorten");
        
        HttpResponse response = httpClient.execute(get);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        
    }
}
