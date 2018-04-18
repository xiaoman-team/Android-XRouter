package cn.xiaoman.android.router.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import cn.xiaoman.android.router.annotation.RouterMap;

@RouterMap("activity://third")
public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("我是三号");
        setContentView(button);

    }
}
