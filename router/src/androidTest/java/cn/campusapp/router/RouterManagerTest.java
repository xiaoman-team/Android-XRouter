package cn.campusapp.router;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import cn.campusapp.router.route.ActivityRoute;
import cn.campusapp.router.route.BaseRoute;
import cn.campusapp.router.route.IRoute;
import cn.campusapp.router.router.BaseRouter;
import cn.campusapp.router.router.IActivityRouteTableInitializer;
import cn.campusapp.router.router.IRouter;
import cn.campusapp.router.utils.UrlUtils;
import timber.log.Timber;

/**
 * Created by kris on 16/3/11.
 */
@RunWith(AndroidJUnit4.class)
public class RouterManagerTest extends BaseUnitTest{

    @Rule
    public ActivityTestRule<DebugActivity> rule = new ActivityTestRule<DebugActivity>(DebugActivity.class);



    @Before
    public void setUp(){
        RouterManager.getSingleton().initActivityRouter(rule.getActivity().getApplicationContext(), new IActivityRouteTableInitializer() {
            @Override
            public void initRouterTable(Map<String, Class<? extends Activity>> router) {
                router.put("activity://main/debug", DebugActivity.class);
            }
        });
        RouterManager.getSingleton().initBrowserRouter(rule.getActivity().getApplicationContext());

    }


    @Test
    public void testGetRoute(){
        IRoute route = RouterManager.getSingleton().getRoute("activity://main");
        Assert.assertEquals(ActivityRoute.class, route.getClass());
    }




    @Test
    public void testAddRouter(){
        RouterManager.getSingleton().addRouter(new TestRouter());
        IRoute route = RouterManager.getSingleton().getRoute("test://main");
        Assert.assertEquals(TestRoute.class, route.getClass());

    }


    private static class TestRouter extends BaseRouter{

        @Override
        public boolean open(IRoute route) {
            Timber.i(route.getUrl());
            return true;
        }

        @Override
        public boolean open(String url) {
            Timber.i(url);
            return true;
        }

        @Override
        public boolean open(Context context, String url) {
            return false;
        }

        @Override
        public IRoute getRoute(String url) {
            return new TestRoute(this, url);
        }

        @Override
        public boolean canOpenTheRoute(IRoute route) {
            return (route instanceof TestRoute);
        }

        @Override
        public boolean canOpenTheUrl(String url) {
            return TextUtils.equals(UrlUtils.getScheme(url), "test");
        }

        @Override
        public Class<? extends IRoute> getCanOpenRoute() {
            return TestRoute.class;
        }
    }

    private static class TestRoute extends BaseRoute {

        public TestRoute(IRouter router, String url) {
            super(router, url);
        }
    }


}
