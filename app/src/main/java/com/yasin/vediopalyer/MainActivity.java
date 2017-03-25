package com.yasin.vediopalyer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.yasin.vediopalyer.util.LogUtils;
import com.yasin.vediopalyer.util.SnackbarUtil;
import com.yasin.vediopalyer.util.Utils;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout rlMain;
    private FrameLayout flVedio;
    //private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private VideoView video;
    private LinearLayout navigation;
    private ImageView ivBullet;
    private ImageView ivExpand;
    private View mDecorView;

    //默认显示视频操作导航
    private boolean isShowNavigation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mDecorView = getWindow().getDecorView();
        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        flVedio = (FrameLayout) findViewById(R.id.fl_vidio);
        // appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        video = (VideoView) findViewById(R.id.video);
        navigation = (LinearLayout) findViewById(R.id.ll_navigation);
        ivBullet = (ImageView) findViewById(R.id.iv_bullet_screen);
        ivExpand = (ImageView) findViewById(R.id.iv_expand);

        navigation.setVisibility(View.VISIBLE);
        isShowNavigation = true;

        video.setVideoPath("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high");
        //video.setZOrderOnTop(true);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video.start();
            }
        });
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });

        /**
         * 处理点击屏幕 显示navigation布局
         */
        video.setOnTouchListener((v, event) -> {
            LogUtils.d(event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://点击
                    if (isShowNavigation) {
                        LogUtils.d(isShowNavigation);
                        navigation.setVisibility(View.GONE);
                        isShowNavigation = false;
                    } else {
                        LogUtils.d(isShowNavigation);
                        navigation.setVisibility(View.VISIBLE);
                        isShowNavigation = true;
                    }
                    break;
            }
            return true;
        });


        //屏幕状态切换
        ivExpand.setOnClickListener(v -> {
            changeOrientation();
        });
        ivBullet.setOnClickListener(v -> {
            SnackbarUtil.showLong(this, "弹幕...66666");
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        showSystemUI();
    }

    /**
     * 屏幕旋转时调用此方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(MainActivity.this, "现在是竖屏", Toast.LENGTH_SHORT).show();
            rlMain.removeAllViews();

            //clMain.addView(appBarLayout);
            rlMain.addView(flVedio);
            rlMain.addView(fab);

            int widthPixels = FrameLayout.LayoutParams.MATCH_PARENT;
            int heightPixels = Utils.dpToPx(200);
            ViewGroup.LayoutParams layoutParams = flVedio.getLayoutParams();
            layoutParams.height = heightPixels;
            layoutParams.width = widthPixels;
            flVedio.setLayoutParams(layoutParams);

            rlMain.requestLayout();
            showSystemUI();

        } else {
            Toast.makeText(MainActivity.this, "现在是横屏", Toast.LENGTH_SHORT).show();
            rlMain.removeAllViews();
            rlMain.addView(flVedio);

            int widthPixels = FrameLayout.LayoutParams.MATCH_PARENT;
            int heightPixels = FrameLayout.LayoutParams.MATCH_PARENT;
            ViewGroup.LayoutParams layoutParams = flVedio.getLayoutParams();
            layoutParams.height = heightPixels;
            layoutParams.width = widthPixels;
            flVedio.setLayoutParams(layoutParams);

            rlMain.requestLayout();

            hideSystemUI();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 切换显示模式
     */
    private void changeOrientation() {
        //如果是竖屏切换到全屏
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            // LogUtils.d(TAG, "expandPlayer", "设置横屏");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            //LogUtils.d(TAG, "expandPlayer", "设置竖屏");
        }
    }

    /**
     * 隐藏系统UI
     */
    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    /**
     * 显示系统控件
     */
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }
}
