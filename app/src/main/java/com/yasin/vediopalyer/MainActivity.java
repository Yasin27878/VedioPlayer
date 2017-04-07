package com.yasin.vediopalyer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
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
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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

    AudioManager am;
    SeekBar seekbarVoice;
    private boolean isMute;
    private int touchRang; //横评下的屏幕高度
    private int mVol; //当前音量
    private int maxVoice; //最大音量
    private float startX;
    private float startY;

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

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVoice = mVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        int screenWidth = dm.widthPixels;

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
 * 手势监测
 */
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override//手指点击屏幕的瞬间
            public boolean onDown(MotionEvent e) {
                LogUtils.d("onDown");
                startY = e.getY();
                startX = e.getX();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                return true;
            }

            @Override//点击屏幕 拖住不放
            public void onShowPress(MotionEvent e) {
                LogUtils.d("onShowPress");

            }

            @Override//单击行为
            public boolean onSingleTapUp(MotionEvent e) {
                LogUtils.d("onSingleTapUp");
                if (isShowNavigation) {
                    LogUtils.d(isShowNavigation);
                    navigation.setVisibility(View.GONE);
                    isShowNavigation = false;
                } else {
                    LogUtils.d(isShowNavigation);
                    navigation.setVisibility(View.VISIBLE);
                    isShowNavigation = true;
                }
                return false;
            }

            @Override//拖动屏幕
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                LogUtils.d("onScroll");
                //横屏是进行操作  竖屏不进行滑动监听
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LogUtils.d("横屏----------------");
                    //水平滑动距离大于竖直滑动距离 认定水平滑动
                    if (Math.abs(e2.getX() - e1.getX()) > Math.abs(e2.getY() - e1.getY())) {
                        LogUtils.d("调整视频播放的进度!");
                    } else {//上下滑动
                        //滑动右边屏--> 声音降低与调高
                        if (e1.getX() > screenHeight / 2) {
                            //右边屏幕-调节声音
                            //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                            float delta = (distanceY / touchRang) * maxVoice;
                            //最终声音 = 原来的 + 改变声音；
                            int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                            if (delta != 0) {
                                isMute = false;
                                updateVoice(voice, isMute);
                            }
                        } else {//滑动左边屏--> 屏幕亮度降低与调高
                            //左边屏幕-调节亮度
                            final double FLING_MIN_DISTANCE = 0.5;
                            final double FLING_MIN_VELOCITY = 0.5;
                            if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                                setBrightness(10);
                            }
                            if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                                setBrightness(-10);
                            }
                        }
                    }
                }else {
                    LogUtils.d("竖屏---------------");
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                LogUtils.d("onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                LogUtils.d("onFling");
                return false;
            }
        });
        gestureDetector.setIsLongpressEnabled(false);
        /**
         * 处理点击屏幕 显示navigation布局
         */
        video.setOnTouchListener((v, event) -> {
            LogUtils.d(event.getAction());

            boolean consume = gestureDetector.onTouchEvent(event);
            return consume;


    /*        switch (event.getAction()) {
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
            }*/
          //  return true;
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

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    private void updateVoice(int progress, boolean isMute) {
        if (isMute) {

            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            // seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            // seekbarVoice.setProgress(progress);
            // currentVoice = progress;
        }
    }

    /*
 *
 * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
 */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        getWindow().setAttributes(lp);
    }
}
