package cn.campusapp.router.route;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import cn.campusapp.router.DebugActivity;
import cn.campusapp.router.router.ActivityRouter;
import cn.campusapp.router.router.IRouter;

/**
 * Created by kris on 16/3/11.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityRouteTest {

    @Rule
    public ActivityTestRule<DebugActivity> rule = new ActivityTestRule<DebugActivity>(DebugActivity.class);

    @Test
    public void testRouteBuilder(){
        String url = "activity://www.baidu.com/happy/hello/好帅气?pp=23&ch=中国人";
        long longvalue = 12312312312313l;
        char charvalue = 'c';
        CharSequence charSequence = "charsequence";
        String string = "string";
        float floatValue = 0.34f;
        double doubleValue = 321313131242345234.3241321d;
        int intValue = 1223;
        DebugActivity debugActivity = rule.getActivity();
        IRouter router = new ActivityRouter();
        ActivityRoute route = new ActivityRoute.Builder(router)
                .setUrl(url)
                .withParams("longvalue", longvalue)
                .withParams("charvalue", charvalue)
                .withParams("charsequence", charSequence)
                .withParams("string", string)
                .withParams("float", floatValue)
                .withParams("double", doubleValue)
                .withParams("int", intValue)
                .withAnimation(debugActivity, 100, 200)
                .withOpenMethodStartForResult(debugActivity, 200)
                .build();

        Assert.assertEquals(route.getUrl(), url);
        Assert.assertEquals(route.getScheme(), "activity");
        Assert.assertEquals(route.getHost(), "www.baidu.com");
        List<String> keyValues = route.getPath();
        Assert.assertEquals(keyValues.size(), 3);
        Assert.assertEquals(keyValues.get(0), "happy");
        Assert.assertEquals(keyValues.get(1), "hello");
        Assert.assertEquals(keyValues.get(2), "好帅气");
        Map<String, String> parameters = route.getParameters();
        Assert.assertEquals(parameters.get("pp"), "23");
        Assert.assertEquals(parameters.get("ch"), "中国人");
        Assert.assertEquals(route.getExtras().getLong("longvalue"), longvalue);
        Assert.assertEquals(route.getExtras().getChar("charvalue"), charvalue);
        Assert.assertEquals(route.getExtras().getCharSequence("charsequence"), charSequence);
        Assert.assertEquals(route.getExtras().getString("string"), string);
        Assert.assertEquals(route.getExtras().getFloat("float"), floatValue);
        Assert.assertEquals(route.getExtras().getDouble("double"), doubleValue);
        Assert.assertEquals(route.getExtras().getInt("int"), intValue);
        Assert.assertEquals(route.getActivity(), debugActivity);
        Assert.assertEquals(route.getInAnimation(), 100);
        Assert.assertEquals(route.getOutAnimation(), 200);
        Assert.assertEquals(route.getRequestCode(), 200);
        Assert.assertEquals(route.getOpenType(), 1);
        Assert.assertEquals(route.getActivity(), debugActivity);
    }




}
