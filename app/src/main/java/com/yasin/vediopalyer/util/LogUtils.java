package com.yasin.vediopalyer.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Yasin on 2017/2/20.
 * Email : yili270@163.com
 * Description :Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 */

public class LogUtils {
    private static String mTAG = "";

    private static boolean isShow = true;

    public void setShowAble(boolean isShow) {
        this.isShow = isShow;
    }

    /**
     * 获取Class的TAG
     *
     * @param ste
     * @return
     */
    private static String getTAG(StackTraceElement ste) {
        String tag = "%s.%s(L:%d)";
        String name = ste.getClassName();
        name = name.substring(name.lastIndexOf(".") + 1);
        tag = String.format(tag, name, ste.getMethodName(), ste.getLineNumber());
        tag = TextUtils.isEmpty(mTAG) ? tag : mTAG + ":" + tag;
        return tag;
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void v(Object o) {
        if (!isShow) return;
        StackTraceElement ste = getCallerStackTraceElement();
        String tag = getTAG(ste);
        Log.v(tag, o.toString());
    }

    public static void d(Object o) {
        if (!isShow) return;
        StackTraceElement ste = getCallerStackTraceElement();
        String tag = getTAG(ste);
        Log.d(tag, o.toString());
    }

    public static void i(Object o) {
        if (!isShow) return;
        StackTraceElement ste = getCallerStackTraceElement();
        String tag = getTAG(ste);
        Log.i(tag, o.toString());
    }

    public static void w(Object o) {
        if (!isShow) return;
        StackTraceElement ste = getCallerStackTraceElement();
        String tag = getTAG(ste);
        Log.w(tag, o.toString());
    }

    public static void e(Object o) {
        if (!isShow) return;
        StackTraceElement ste = getCallerStackTraceElement();
        String tag = getTAG(ste);
        Log.e(tag, o.toString());
    }

    public static void wtf(Object o) {
        if (!isShow) return;
        StackTraceElement ste = getCallerStackTraceElement();
        String tag = getTAG(ste);
        Log.wtf(tag, o.toString());
    }
}
