package cn.xiaoman.android.router.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
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
