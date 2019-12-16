package com.juhao.home.ui;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BaseActivity;
import com.jan.activity.ChangePasswordActivity;
import com.juhao.home.R;

/**
 * 账号与安全
 */
public class AccountActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvUserPhone, tvMobileLocation;
    private RelativeLayout rlChangePassword;

    private String userPhone, mobileLocation;

    @Override
    protected void initView() {
        userPhone = mBundle.getString("user_phone");
        mobileLocation = mBundle.getString("user_location_code");
        setContentView(R.layout.activity_account);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvMobileLocation = findViewById(R.id.tv_mobile_location);
        rlChangePassword = findViewById(R.id.rl_change_password);
        rlChangePassword.setOnClickListener(this);
        tvUserPhone.setText(userPhone);
        tvMobileLocation.setText(mobileLocation);
    }

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_change_password:// 修改密码
                intent2Activity(ChangePasswordActivity.class);
                break;
        }
    }
}
