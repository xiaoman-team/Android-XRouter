package cn.campusapp.router.utils;

import junit.framework.Assert;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.campusapp.router.BaseUnitTest;
import timber.log.Timber;

/**
 * Created by kris on 16/3/11.
 * TODO 目前不支持在值内有：
 *
 *
 */
public class UrlUtilTest extends BaseUnitTest {




    @Test
    public void testGetPathSegments(){
        List<String> segs = null;
        segs = UrlUtils.getPathSegments("activity://popo.com/happy/picasso/:c{id}/:{key}/好美丽");
        Assert.assertEquals(segs.size(), 5);
        Assert.assertEquals(segs.get(0), "happy");
        Assert.assertEquals(segs.get(1), "picasso");
        Assert.assertEquals(segs.get(2), ":c{id}");
        Assert.assertEquals(segs.get(3), ":{key}");
        Assert.assertEquals(segs.get(4), "好美丽");


        segs = UrlUtils.getPathSegments("http://www.baidu.com/happy/picasso/12312332232312/f");
        Assert.assertEquals(segs.size(), 4);
    }



    @Test
    public void testGetScheme(){
        String scheme = UrlUtils.getScheme("cn.campusapp://www.baidu.com/picasso/ffff");
        Assert.assertEquals(scheme, "cn.campusapp");
    }

    @Test
    public void testGetPost(){
        int port = UrlUtils.getPort("cn://www.baidu.com/ffff");
        Timber.i("port %d", port);
    }

    @Test
    public void testGetHost(){
        String host = UrlUtils.getHost("cn.campus://main/ffffsfwr/werew");
        Assert.assertEquals(host, "main");
    }

    @Test
    public void testGetParameters(){
        HashMap<String, String> parameters = UrlUtils.getParameters("http://www.baidu.com/:i{ff}?qq=3");
        Assert.assertEquals(parameters.get("qq"), "3");
    }

    @Test
    public void testAddQueryParameters(){
        String url = "http://www.baiud.com/wwwfffdfs";
        url = UrlUtils.addQueryParameters(url, "haha", "fff");
        Assert.assertEquals(url, "http://www.baiud.com/wwwfffdfs?haha=fff");
    }


    @Test
    public void testGetOptionParams(){
        Map<String, String> ret = null;
        ret = UrlUtils.getParameters("http://www.baidu.com/happy?cc=ffff&w=123&cp=1");
        Assert.assertEquals(ret.get("cc"), "ffff");
        Assert.assertEquals(ret.get("w"), "123");
        Assert.assertEquals(ret.get("cp"), "1");
    }


    @Test
    public void testFail(){
        Assert.assertEquals(UrlUtils.getHost("wwfdsfsdfvvc"), null);
        Assert.assertEquals(UrlUtils.getParameters("ffdsfw").size(), 0);
        Assert.assertEquals(UrlUtils.getScheme("23"), null);
        Assert.assertEquals(UrlUtils.getPathSegments("34234").get(0), "34234");
        Assert.assertEquals(UrlUtils.getPort("fdfs"), -1);
    }
}
