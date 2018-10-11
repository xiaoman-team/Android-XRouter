package cn.xiaoman.android.router.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
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
        button.setText("我是二号");
        setContentView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.open("activity://main");
            }
        });

    }
}
