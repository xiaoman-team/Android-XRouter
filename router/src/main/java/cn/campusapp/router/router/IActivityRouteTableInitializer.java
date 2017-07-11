package cn.campusapp.router.router;

import android.app.Activity;

import java.util.Map;

/**
 * Created by kris on 16/3/10.
 */
public interface IActivityRouteTableInitializer {
    /**
     * init the router table
     * @param router the router map to
     */
    void initRouterTable(Map<String, Class<? extends Activity>> router);



}
