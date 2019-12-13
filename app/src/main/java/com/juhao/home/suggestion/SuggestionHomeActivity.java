package com.juhao.home.suggestion;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.juhao.home.R;
import com.util.Constance;

/**
 * 意见反馈
 */
public class SuggestionHomeActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_suggest_home);
        TextView tv_record = findViewById(R.id.tv_record);
        TextView tv_device_feedback = findViewById(R.id.tv_device_feedback);
        TextView tv_feedback_failure = findViewById(R.id.tv_feeback_failure);
        TextView tv_function_suggest = findViewById(R.id.tv_function_suggest);
        TextView tv_other_problem = findViewById(R.id.tv_other_problem);
        tv_record.setOnClickListener(this);
        tv_device_feedback.setOnClickListener(this);
        tv_feedback_failure.setOnClickListener(this);
        tv_function_suggest.setOnClickListener(this);
        tv_other_problem.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_record:// 反馈记录
                intent2Activity(SuggestRecordActivity.class);
                break;
            case R.id.tv_device_feedback:// 设备反馈
                mBundle.putInt(Constance.type, 1);
                intent2Activity(SuggestDeviceActivity.class, mBundle);
                break;
            case R.id.tv_feeback_failure:// 反馈故障
                mBundle.putInt(Constance.type, 2);
                intent2Activity(SuggestDeviceActivity.class, mBundle);
                break;
            case R.id.tv_function_suggest:// 功能建议
                mBundle.putInt(Constance.type, 3);
                intent2Activity(SuggestDeviceActivity.class, mBundle);
                break;
            case R.id.tv_other_problem:// 其他问题
                mBundle.putInt(Constance.type, 4);
                intent2Activity(SuggestDeviceActivity.class, mBundle);
                break;
        }
    }
}
