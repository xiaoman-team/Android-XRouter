package cn.campusapp.router.interceptor;

import android.content.Context;

/**
 * Created by kris on 17/3/1.
 */

public interface Interceptor {

    /**
     *
     * @param url
     * @return if intercept the request
     */
    boolean intercept(Context context, String url);

}
