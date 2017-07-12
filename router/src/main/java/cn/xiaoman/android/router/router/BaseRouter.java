package cn.xiaoman.android.router.router;

import android.content.Context;

import cn.xiaoman.android.router.interceptor.Interceptor;
import cn.xiaoman.android.router.route.IRoute;


/**
 * Created by kris on 16/3/17.
 */
public abstract class BaseRouter implements IRouter{



    protected static Class<? extends IRoute> CAN_OPEN_ROUTE;

    protected Interceptor interceptor = null;


    protected Context mBaseContext;


    public void init(Context context){
        mBaseContext = context;
    }

    @Override
    public void setInterceptor(Interceptor interceptor){
        this.interceptor = interceptor;
    }




}
