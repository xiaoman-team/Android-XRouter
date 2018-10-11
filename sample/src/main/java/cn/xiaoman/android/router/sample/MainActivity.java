package cn.xiaoman.android.router.sample;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import cn.xiaoman.android.router.Router;
import cn.xiaoman.android.router.annotation.RouterMap;
import cn.xiaoman.android.router.interceptor.Interceptor;
import cn.xiaoman.android.router.route.BaseRoute;

/**
 * Created by jiechic on 2017/7/12.
 */
@RouterMap("activity://main")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        setContentView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.open("activity://sub");
            }
        });

        Router.setInterceptor(new Interceptor() {

            @Override
            public boolean intercept(Context context, BaseRoute baseRoute) {
                if ("activity://sub".contains(baseRoute.getUrl())) {
                    Router.open("activity://third");
                    return false;
                }
                return false;
            }
        });

    }
}
