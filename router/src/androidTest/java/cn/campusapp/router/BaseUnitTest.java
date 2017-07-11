package cn.campusapp.router;


import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.runner.RunWith;

import timber.log.Timber;

/**
 * Created by kris on 16/3/11.
 */
@RunWith(AndroidJUnit4.class)
public class BaseUnitTest {


    @Before
    public void setUp(){
        Timber.plant(new Timber.DebugTree());
    }
}
