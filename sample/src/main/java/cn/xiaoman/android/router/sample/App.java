package cn.xiaoman.android.router.sample;

import android.app.Application;

import cn.xiaoman.android.router.Router;

/**
 * Created by jiechic on 2017/7/12.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Router.initActivityRouter(this);
    }
}
