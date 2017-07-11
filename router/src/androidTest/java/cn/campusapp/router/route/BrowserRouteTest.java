package cn.campusapp.router.route;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import cn.campusapp.router.router.BrowserRouter;

/**
 * Created by kris on 16/3/18.
 */
@RunWith(AndroidJUnit4.class)
public class BrowserRouteTest {


    @Test
    public void testBuild(){

        BrowserRoute route = new BrowserRoute.Builder(new BrowserRouter())
                .setUrl("http://www.baidu.com/浙江人").build();
        Assert.assertEquals(route.getUrl(), "http://www.baidu.com/浙江人");
    }
}
