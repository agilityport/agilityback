package org.smorgrav.agilityback.httphandlers;

import junit.framework.TestCase;
import org.junit.Assert;

import java.net.URI;
import java.net.URISyntaxException;

public class PathMatcherTest extends TestCase {

    public void testMatch() throws URISyntaxException {
        PathMatcher path = new PathMatcher(new URI(null, null, null, 0, "/my/path/is/good", null, null));
        Assert.assertTrue(path.match("/my/{silly}/is/good"));
        Assert.assertTrue(path.match("my/{silly}/is/good/"));
        Assert.assertEquals("path", path.get("silly"));
        Assert.assertFalse(path.match("/my/{silly}/is/good/but/more"));
        Assert.assertFalse(path.match("/my"));
        Assert.assertTrue(path.match("/my/path/is/*"));
    }
}