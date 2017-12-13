package com.huduan.contactstest.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.huduan.contactstest.R;
import com.huduan.contactstest.activity.MainActivity;

public class FloatService extends Service {
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    ImageView mFloatView;

    private static final String TAG = "FloatService";


    public FloatService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
        Log.d(TAG, "onCreate: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


        mFloatView = new ImageView(this);
        mFloatView.setImageResource(R.mipmap.ic_launcher_round);
        //添加mFloatLayout
        mWindowManager.addView(mFloatView, wmParams);

        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            private float lastX; //上一次位置的X.Y坐标
            private float lastY;
            private float nowX;  //当前移动位置的X.Y坐标
            private float nowY;
            private float tranX; //悬浮窗移动位置的相对值
            private float tranY;
            private float x0; //初始位置
            private float y0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取按下时的X，Y坐标
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        x0 = event.getRawX();
                        y0 = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取移动时的X，Y坐标
                        nowX = event.getRawX();
                        nowY = event.getRawY();
                        // 计算XY坐标偏移量
                        tranX = nowX - lastX;
                        tranY = nowY - lastY;
                        // 移动悬浮窗
                        wmParams.x += tranX;
                        wmParams.y += tranY;
                        //更新悬浮窗位置
                        mWindowManager.updateViewLayout(mFloatView, wmParams);
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX;
                        lastY = nowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        float x1 = event.getRawX();
                        float y1 = event.getRawY();
                        //使悬浮球停靠在两边
                        if (x1 > getScreenWidth() / 2) {
                            wmParams.x = getScreenWidth() - mFloatView.getWidth();
                        } else {
                            wmParams.x = 0;
                        }

                        mWindowManager.updateViewLayout(mFloatView, wmParams);
                        //如果是移动事件,则消费掉
                        if (Math.abs(x1 - x0) > 6||Math.abs(y1-y0)>6) {
                            return true;
                        } else {
                            return false;
                        }

                }
                return false;
            }

        });


        mFloatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(FloatService.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatView != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//获取屏幕宽度
    public int getScreenWidth (){
        return mWindowManager.getDefaultDisplay().getWidth();
    }
}
