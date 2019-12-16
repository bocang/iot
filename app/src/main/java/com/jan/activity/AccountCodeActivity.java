package com.jan.activity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.BaseActivity;
import com.jan.view.CodeView;
import com.juhao.home.R;

/**
 * 修改密码-->输入验证码
 */
public class AccountCodeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private CodeView code_view;
    private TextView tv_code_error;
    private TextView tv_count_down;

    private String password = "";

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {

        password = getBundle().getString("password");

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        code_view = findViewById(R.id.code_view);
        code_view.setInputListener(new CodeView.InputListener() {
            @Override
            public void onInputCompleted(String text) {
                // 输入完成后回调
                Toast.makeText(AccountCodeActivity.this, "验证码==" + text, Toast.LENGTH_SHORT).show();
            }
        });
        tv_code_error = findViewById(R.id.tv_code_error);
        tv_count_down = findViewById(R.id.tv_count_down);
        tv_count_down.setOnClickListener(this);

        countDown();

    }

    @Override
    protected void initData() {
        setContentView(R.layout.activity_account_code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_count_down:// 验证码倒计时
                countDown();
                break;
        }
    }

    private int count;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (count > 0) {
                    tv_count_down.setEnabled(false);
                    tv_count_down.setTextColor(getResources().getColor(R.color.tv_999999));
                    count--;
                    tv_count_down.setText("验证码已发送到您的手机：86-15112345678，重新发送（" + count + "s）");
                    handler.sendEmptyMessageDelayed(100, 1000);
                } else {
                    // 倒计时完成后需要处理的业务逻辑
                    tv_count_down.setEnabled(true);
                    tv_count_down.setText("重新发送验证码");
                    tv_count_down.setTextColor(getResources().getColor(R.color.cFA5500));
                }
            }
        }
    };

    /**
     * 开始倒计时
     */
    public void countDown() {
        count = 60;
        handler.sendEmptyMessageDelayed(100, 1000);
    }

}
