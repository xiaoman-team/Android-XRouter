package cn.campusapp.router;

import org.junit.Before;

import timber.log.Timber;

/**
 * Created by kris on 16/3/11.
 */
public class BaseUnitTest {

    @Before
    public void setUp(){
        Timber.plant(new Timber.Tree(){

            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                if(t != null){
                    System.err.println(t);
                } else {
                    System.out.print("TAG: " + message);
                }
            }
        });
    }
}
