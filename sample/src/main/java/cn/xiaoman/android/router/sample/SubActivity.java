package cn.xiaoman.android.router.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cn.xiaoman.android.router.Router;
import cn.xiaoman.android.router.annotation.RouterMap;

/**
 * Created by jiechic on 2017/7/12.
 */

@RouterMap("activity://sub")
public class SubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        setContentView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.open("activity://main");
            }
        });
    }
}
