/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.urlservice.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class BitlyApiControllerExpandTest {

    private static final URI SHORT_LINK_PREFIX = URI.create("http://s.vgregion.se");
    private BitlyApiController controller = new BitlyApiController(new MockUrlServiceService(), SHORT_LINK_PREFIX);
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private static final String GLOBAL_HASH = "abcdef";

    @Test
    public void jsonResponse() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/foo")), null, "json", response);

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(
                        "{\"status_code\":200," +
                        "\"status_txt\":\"OK\"," +
                        "\"data\":{" +
                        "\"expand\":[{" +
                        "\"hash\":\"foo\"," +
                        "\"short_url\":\"http://s.vgregion.se/foo\"," +
                        "\"global_hash\":\"" + GLOBAL_HASH + "\"," +
                        "\"long_url\":\"http://example.com\"," +
                        "\"user_hash\":\"foo\"" +
                        "}]}}", response.getContentAsString());
    }

    @Test
    public void xmlResponse() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/foo")), null, "xml", response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(
                "<response><status_code>200</status_code>" +
                "<status_txt>OK</status_txt>" +
                "<data><entry>" +
                "<hash>foo</hash>" +
                "<short_url>http://s.vgregion.se/foo</short_url>" +
                "<global_hash>" + GLOBAL_HASH + "</global_hash>" +
                "<long_url>http://example.com</long_url>" +
                "<user_hash>foo</user_hash>" +
                "</entry></data></response>", response.getContentAsString());
    }

    
    @Test
    public void txtResponse() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/foo")), null, "txt", response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://example.com", response.getContentAsString());
    }

    @Test
    public void unknownFormatMustNotBeAllowed() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://example.com")), null, "unknown", response);
        
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void noShortLinkNorHashMustBeRefused() throws IOException {
        controller.expand(null, null, "txt", response);
        
        Assert.assertEquals(500, response.getStatus());
    }
}
