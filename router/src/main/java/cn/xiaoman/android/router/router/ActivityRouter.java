package cn.xiaoman.android.router.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import cn.xiaoman.android.router.BuildConfig;
import cn.xiaoman.android.router.exception.InvalidRoutePathException;
import cn.xiaoman.android.router.exception.InvalidValueTypeException;
import cn.xiaoman.android.router.exception.RouteNotFoundException;
import cn.xiaoman.android.router.route.ActivityRoute;
import cn.xiaoman.android.router.route.IRoute;
import cn.xiaoman.android.router.tools.ActivityRouteRuleBuilder;
import cn.xiaoman.android.router.utils.UrlUtils;
import dalvik.system.DexFile;
import timber.log.Timber;

import static cn.xiaoman.android.router.utils.UrlUtils.getHost;
import static cn.xiaoman.android.router.utils.UrlUtils.getPathSegments;
import static cn.xiaoman.android.router.utils.UrlUtils.getScheme;


/**
 * Created by kris on 16/3/10.
 */
public class ActivityRouter extends BaseRouter {
    private static final String TAG = "Router";
    private static List<String> MATCH_SCHEMES = new ArrayList<>();
    private static final String DEFAULT_SCHEME = "activity";
    private static final int HISTORY_CACHE_SIZE = 20;

    private static ActivityRouter mActivityRouter = new ActivityRouter();   //Activity

    private static final String KEY_URL = "key_and_activity_router_url";

    static {
        CAN_OPEN_ROUTE = ActivityRoute.class;
        MATCH_SCHEMES.add(DEFAULT_SCHEME);

    }

    private Map<String, Class<? extends Activity>> mRouteTable = new HashMap<>();

    private CircularFifoQueue<HistoryItem> mHistoryCaches = new CircularFifoQueue<>(HISTORY_CACHE_SIZE);


    public static ActivityRouter getInstance() {
        return mActivityRouter;
    }

    public void init(Context appContext, IActivityRouteTableInitializer initializer) {
        super.init(appContext);
        initActivityRouterTable(initializer);
    }

    @Override
    public void init(Context appContext) {
        init(appContext, null);
    }


    public void initActivityRouterTable(IActivityRouteTableInitializer initializer) {
        if (initializer != null) {
            initializer.initRouterTable(mRouteTable);
        }
        for (String pathRule : mRouteTable.keySet()) {
            boolean isValid = ActivityRouteRuleBuilder.isActivityRuleValid(pathRule);
            if (!isValid) {
                Timber.e(new InvalidRoutePathException(pathRule), "");
                mRouteTable.remove(pathRule);
            }
        }
    }


    @Override
    public ActivityRoute getRoute(String url) {
        return new ActivityRoute.Builder(this)
                .setUrl(url)
                .build();
    }

    @Override
    public boolean canOpenTheRoute(IRoute route) {
        return CAN_OPEN_ROUTE.equals(route.getClass());
    }

    @Override
    public boolean canOpenTheUrl(String url) {
        for (String scheme : MATCH_SCHEMES) {
            if (TextUtils.equals(scheme, getScheme(url))) {
                return true;
            }
        }
        return false;
    }

    /**
     * It support multi schemes now
     *
     * @return
     * @see #getMatchSchemes()
     */
    @Deprecated
    public String getMatchScheme() {
        return MATCH_SCHEMES.get(0);
    }

    public List<String> getMatchSchemes() {
        return MATCH_SCHEMES;
    }

    public void setMatchScheme(String scheme) {
        MATCH_SCHEMES.clear();
        MATCH_SCHEMES.add(scheme);
    }

    public void setMatchSchemes(String... schemes) {
        MATCH_SCHEMES.clear();
        List<String> list = Arrays.asList(schemes);
        list.remove("");
        list.remove(null);
        MATCH_SCHEMES.addAll(list);
    }

    public void addMatchSchemes(String scheme) {
        MATCH_SCHEMES.add(scheme);
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return CAN_OPEN_ROUTE;
    }

    @Override
    public boolean open(IRoute route) {
        boolean ret = false;
        if (route instanceof ActivityRoute) {
            ActivityRoute aRoute = (ActivityRoute) route;
            try {
                switch (aRoute.getOpenType()) {
                    case ActivityRoute.START:
                        if (doOnInterceptor(aRoute.getActivity(), route.getUrl())) {
                            return true;
                        }
                        open(aRoute, aRoute.getActivity());
                        ret = true;
                        break;
                    case ActivityRoute.FOR_RESULT_ACTIVITY:
                        if (doOnInterceptor(aRoute.getActivity(), route.getUrl())) {
                            return true;
                        }
                        openForResult(aRoute, aRoute.getActivity(), aRoute.getRequestCode());
                        ret = true;
                        break;
                    case ActivityRoute.FOR_RESULT_SUPPORT_FRAGMENT:
                        if (doOnInterceptor(aRoute.getSupportFragment().getActivity(), route.getUrl())) {
                            return true;
                        }
                        openForResult(aRoute, aRoute.getSupportFragment(), aRoute.getRequestCode());
                        ret = true;
                        break;
                    case ActivityRoute.FOR_RESULT_FRAGMENT:
                        if (doOnInterceptor(aRoute.getFragment().getActivity(), route.getUrl())) {
                            return true;
                        }
                        openForResult(aRoute, aRoute.getFragment(), aRoute.getRequestCode());
                        ret = true;
                        break;
                    default:
                        Timber.e("Error Open Type");
                        ret = false;
                        break;

                }
            } catch (Exception e) {
                Timber.e(e, "Url route not specified: %s", route.getUrl());
                ret = false;
            }
        }
        return ret;

    }

    @Override
    public boolean open(String url) {
        return open(null, url);
    }

    @Override
    public boolean open(Context context, String url) {
        if (doOnInterceptor(context, url)) {
            return true;
        }
        IRoute route = getRoute(url);
        if (route instanceof ActivityRoute) {
            ActivityRoute aRoute = (ActivityRoute) route;
            try {
                open(aRoute, context);
                return true;
            } catch (Exception e) {
                Timber.e(e, "Url route not specified: %s", route.getUrl());
            }
        }
        return false;
    }

    private boolean doOnInterceptor(Context context, String url) {
        if (interceptor != null) {
            return interceptor.intercept(context != null ? context : mBaseContext, url);
        }
        return false;
    }


    protected void open(ActivityRoute route, Context context) throws RouteNotFoundException {
        Class<?> fromClazz = context != null ? context.getClass() : mBaseContext.getClass();
        Intent intent = match(fromClazz, route);
        if (intent == null) {
            throw new RouteNotFoundException(route.getUrl());
        }
        if (context == null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | route.getFlags());
            mBaseContext.startActivity(intent);
        } else {
            intent.setFlags(route.getFlags());
            context.startActivity(intent);
        }

        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }

    }

    protected void openForResult(ActivityRoute route, Activity activity, int requestCode) throws RouteNotFoundException {


        Intent intent = match(activity.getClass(), route);
        if (intent == null) {
            throw new RouteNotFoundException(route.getUrl());
        }
        intent.setFlags(route.getFlags());
        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }
        activity.startActivityForResult(intent, requestCode);

    }

    protected void openForResult(ActivityRoute route, Fragment fragment, int requestCode) throws RouteNotFoundException {

        Intent intent = match(fragment.getClass(), route);
        if (intent == null) {
            throw new RouteNotFoundException(route.getUrl());
        }
        intent.setFlags(route.getFlags());
        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }
        fragment.startActivityForResult(intent, requestCode);

    }

    protected void openForResult(ActivityRoute route, android.app.Fragment fragment, int requestCode) throws RouteNotFoundException {

        Intent intent = match(fragment.getClass(), route);
        if (intent == null) {
            throw new RouteNotFoundException(route.getUrl());
        }
        intent.setFlags(route.getFlags());
        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }
        fragment.startActivityForResult(intent, requestCode);

    }


    /**
     * host 和path匹配称之为路由匹匹配
     *
     * @param route
     * @return String the match routePath
     */
    @Nullable
    private String findMatchedRoute(ActivityRoute route) {
        List<String> givenPathSegs = route.getPath();
        OutLoop:
        for (String routeUrl : mRouteTable.keySet()) {
            List<String> routePathSegs = getPathSegments(routeUrl);
            if (!TextUtils.equals(getHost(routeUrl), route.getHost())) {
                continue;
            }
            if (givenPathSegs.size() != routePathSegs.size()) {
                continue;
            }
            for (int i = 0; i < routePathSegs.size(); i++) {
                if (!routePathSegs.get(i).startsWith(":")
                        && !TextUtils.equals(routePathSegs.get(i), givenPathSegs.get(i))) {
                    continue OutLoop;
                }
            }
            //find the match route
            return routeUrl;
        }

        return null;
    }

    /**
     * find the key value in the path and set them in the intent
     *
     * @param routeUrl the matched route path
     * @param givenUrl the given path
     * @param intent   the intent
     * @return the intent
     */
    private Intent setKeyValueInThePath(String routeUrl, String givenUrl, Intent intent) {
        List<String> routePathSegs = getPathSegments(routeUrl);
        List<String> givenPathSegs = getPathSegments(givenUrl);
        for (int i = 0; i < routePathSegs.size(); i++) {
            String seg = routePathSegs.get(i);
            if (seg.startsWith(":")) {
                int indexOfLeft = seg.indexOf("{");
                int indexOfRight = seg.indexOf("}");
                String key = seg.substring(indexOfLeft + 1, indexOfRight);
                char typeChar = seg.charAt(1);
                switch (typeChar) {
                    //interger type
                    case 'i':
                        try {
                            int value = Integer.parseInt(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析整形类型失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                //如果是在release情况下则给一个默认值
                                intent.putExtra(key, 0);
                            }
                        }
                        break;
                    case 'f':
                        //float type
                        try {
                            float value = Float.parseFloat(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析浮点类型失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, 0f);
                            }
                        }
                        break;
                    case 'l':
                        //long type
                        try {
                            long value = Long.parseLong(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析长整形失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, 0l);
                            }
                        }
                        break;
                    case 'd':
                        try {
                            double value = Double.parseDouble(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析double类型失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, 0d);
                            }
                        }
                        break;
                    case 'c':
                        try {
                            char value = givenPathSegs.get(i).charAt(0);
                        } catch (Exception e) {
                            Log.e(TAG, "解析Character类型失败" + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, ' ');
                            }
                        }
                        break;
                    case 's':
                    default:
                        intent.putExtra(key, givenPathSegs.get(i));
                }
            }

        }
        return intent;
    }

    private Intent setOptionParams(String url, Intent intent) {
        Map<String, String> queryParams = UrlUtils.getParameters(url);
        for (String key : queryParams.keySet()) {
            intent.putExtra(key, queryParams.get(key));
        }

        return intent;
    }

    private Intent setExtras(Bundle bundle, Intent intent) {
        intent.putExtras(bundle);
        return intent;
    }

    @Nullable
    private Intent match(Class<?> from, ActivityRoute route) {
        String matchedRoute = findMatchedRoute(route);
        if (matchedRoute == null) {
            return null;
        }
        Class<? extends Activity> matchedActivity = mRouteTable.get(matchedRoute);
        Intent intent = new Intent(mBaseContext, matchedActivity);
        mHistoryCaches.add(new HistoryItem(from, matchedActivity));
        //find the key value in the path
        intent = setKeyValueInThePath(matchedRoute, route.getUrl(), intent);
        intent = setOptionParams(route.getUrl(), intent);
        intent = setExtras(route.getExtras(), intent);
        intent.putExtra(KEY_URL, route.getUrl());
        return intent;
    }


    public static String getKeyUrl() {
        return KEY_URL;
    }

    public Queue<HistoryItem> getRouteHistories() {
        return mHistoryCaches;
    }

    private List<String> getClasses(Context mContext, String packageName) {
        ArrayList<String> classes = new ArrayList<>();
        try {
            String packageCodePath = mContext.getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            String regExp = "^" + packageName + ".\\w+$";
            for (Enumeration iter = df.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement().toString();
                if (className.matches(regExp)) {
                    classes.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
}
