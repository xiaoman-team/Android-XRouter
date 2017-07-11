package cn.campusapp.router.tools;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by kris on 16/3/11.
 */
@RunWith(AndroidJUnit4.class)
public class ActivityRouteUrlBuilderTest {

    @Test
    public void testBuildPath(){
        String rule = "activity://main/:i{id}/b/:s{des}/h/:l{age}/k";
            String path = new ActivityRouteUrlBuilder(rule)
                    .withKeyValue("id", 1)
                    .withKeyValue("des", "sss")
                    .withKeyValue("age", 11111111l)
                    .build();
            Assert.assertEquals("activity://main/1/b/sss/h/11111111/k", path);

            String path2 = new ActivityRouteUrlBuilder(rule)
                    .withKeyValue("id", 2)
                    .build();



    }

}
