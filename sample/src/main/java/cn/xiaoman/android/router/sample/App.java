package cn.xiaoman.android.router.sample;

import android.app.Application;
import android.net.Uri;

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

    public static void main(String[] args) {
        Uri uri = Uri.parse("home");
    }
}
