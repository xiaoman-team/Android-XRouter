package cn.campusapp.router.tools;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import cn.campusapp.router.BaseUnitTest;
import cn.campusapp.router.utils.UrlUtils;

/**
 * Created by kris on 16/3/11.
 */
@RunWith(AndroidJUnit4.class)
public class ActivityRouteRuleBuilderTest extends BaseUnitTest{

    @Test
    public void testRuleBuilder(){
        String rule = new ActivityRouteRuleBuilder()
                .setScheme("activity")
                .setHost("main")
                .addKeyValueDefine("id", Integer.class)
                .addKeyValueDefine("l", Long.class)
                .addKeyValueDefine("f", Float.class)
                .addKeyValueDefine("d", Double.class)
                .addKeyValueDefine("s", String.class)
                .addPathSegment("end").build();
        List<String> paths = UrlUtils.getPathSegments(rule);
        Assert.assertEquals(paths.get(0), ":i{id}");
        Assert.assertEquals(paths.get(1), ":l{l}");
        Assert.assertEquals(paths.get(2), ":f{f}");
        Assert.assertEquals(paths.get(3), ":d{d}");
        Assert.assertEquals(paths.get(4), ":s{s}");
        Assert.assertEquals(paths.get(5), "end");
    }

    @Test
    public void testIsRouteRuleValid(){
        Assert.assertTrue(ActivityRouteRuleBuilder.isActivityRuleValid("http://main/hello/:{f}/:i{id}/l"));
        Assert.assertFalse(ActivityRouteRuleBuilder.isActivityRuleValid("http://main/hello/:fffw}"));
    }
}
