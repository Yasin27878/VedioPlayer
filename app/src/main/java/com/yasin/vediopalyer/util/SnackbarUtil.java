package com.yasin.vediopalyer.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Yasin on 2017/3/1.
 * Email : yasin27878@163.com
 * Description :Snackbar 工具类
 */

public class SnackbarUtil {
    /**
     * 短Snackbar提示
     * @param context
     * @param msg
     */
    public static void showShort(Context context, String msg) {
        Snackbar.make(findFirstView((AppCompatActivity) context), msg, Snackbar.LENGTH_SHORT).show();
    }
    /**
     * 短Snackbar提示
     * @param context
     * @param msg
     */
    public static void showShort(Context context, @StringRes int msg) {
        Snackbar.make(findFirstView((AppCompatActivity) context), context.getText(msg), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 长Snackbar提示
     * @param context
     * @param msg
     */
    public static void showLong(Context context, String msg) {
        Snackbar.make(findFirstView((AppCompatActivity) context), msg, Snackbar.LENGTH_LONG).show();
    }
    /**
     * 长Snackbar提示
     * @param context
     * @param msg
     */
    public static void showLong(Context context, @StringRes int msg) {
        Snackbar.make(findFirstView((AppCompatActivity) context), msg, Snackbar.LENGTH_LONG).show();
    }

    /**
     * 可交互的Snackbar
     *
     * @param context
     * @param msg
     * @param action
     * @param callBack
     */
    public static void showAction(Context context, String msg, String action, ActionCallBack callBack) {
        Snackbar snackbar = Snackbar.make(findFirstView((AppCompatActivity) context), msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                callBack.click();
            }
        });
        snackbar.show();
    }

    public interface ActionCallBack {
        void click();
    }

    public static View findFirstView(AppCompatActivity activity) {
        return ((ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
    }

}
