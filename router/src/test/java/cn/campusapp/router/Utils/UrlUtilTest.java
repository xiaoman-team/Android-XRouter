package cn.campusapp.router.utils;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import cn.campusapp.router.BaseUnitTest;

/**
 * Created by kris on 16/3/11.
 */
public class UrlUtilTest extends BaseUnitTest {

    @Test
    public void testGetPathSegments(){
        List<String> segs = null;
        segs = UrlUtils.getPathSegments("/happy/picasso/:c{id}/:{key}");
        Assert.assertEquals(segs.size(), 4);


        segs = UrlUtils.getPathSegments("/happy/picasso/12312332\\\\/232312/f");
        Assert.assertEquals(segs.size(), 4);
    }




    @Test
    public void testGetOptionParams(){
        Map<String, String> ret = null;
        ret = UrlUtils.getOptionParams("/happy?cc=ffff&w=123&cp=1");
        Assert.assertEquals(ret.get("cc"), "ffff");
        Assert.assertEquals(ret.get("w"), "123");
        Assert.assertEquals(ret.get("cp"), "1");
    }

}
