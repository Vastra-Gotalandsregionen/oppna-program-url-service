package se.vgregion.urlservice.types;

import org.junit.Assert;
import org.junit.Test;

public class RedirectRuleTest {

    private static final String URL = "http://example.com";
    
    @Test
    public void matchesSimple() {
        RedirectRule rule = new RedirectRule("foo", URL);
        
        Assert.assertTrue(rule.matches("foo"));
        Assert.assertFalse(rule.matches("foox"));
        Assert.assertFalse(rule.matches("foo/x"));
        Assert.assertFalse(rule.matches("xfoo"));
        Assert.assertFalse(rule.matches("bar"));
    }

    @Test
    public void matchesWildcard() {
        RedirectRule rule = new RedirectRule("foo.*", URL);
        
        Assert.assertTrue(rule.matches("foo"));
        Assert.assertTrue(rule.matches("foox"));
        Assert.assertTrue(rule.matches("foo/x"));
        Assert.assertFalse(rule.matches("xfoo"));
        Assert.assertFalse(rule.matches("bar"));
    }


}
