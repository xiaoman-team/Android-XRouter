package cn.xiaoman.android.router.interceptor;

import android.content.Context;

import cn.xiaoman.android.router.route.BaseRoute;

/**
 * Created by kris on 17/3/1.
 */

public interface Interceptor {

    /**
     *
     * @param baseRoute
     * @return if intercept the request
     */
    boolean intercept(Context context, BaseRoute baseRoute);

}
