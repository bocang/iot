package com.jan.util;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * created by Administrator
 * on 2019/11/8
 * <p>
 * 倒计时
 */
public class CountDownUtil {

    /**
     * 方法一
     *
     * @param total
     * @param gap
     * @param textView
     * @param onFinishListener
     */
    public static void countDown(long total, long gap, TextView textView, OnFinishListener onFinishListener) {
        CountDownTimer timer = new CountDownTimer(total, gap) {
            @Override
            public void onTick(long millisUntilFinished) {
                onFinishListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // 倒计时完成后的业务逻辑
                onFinishListener.onFinish();
            }
        };
        timer.start();
    }

    public interface OnFinishListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }

    /**
     * 方法二
     */
    private static int count = 60;
    private static TextView textView;
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (count > 0) {
                    count--;
                    textView.setText(count + "秒");
                    handler.sendEmptyMessageDelayed(100, 1000);
                } else {
                    // 倒计时完成后需要处理的业务逻辑
                }
            }
        }
    };

    public static void countDown() {
        handler.sendEmptyMessageDelayed(100, 1000);
    }

}
