package com.juhao.home.ui;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.jan.activity.ChangePasswordActivity;
import com.juhao.home.R;

/**
 * 账号与安全
 */
public class AccountActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvUserPhone, tvMobileLocation;
    private RelativeLayout rlChangePassword;

    private UserInfo userInfo;

    @Override
    protected void initData() {
        setContentView(R.layout.activity_account);
        userInfo = LoginBusiness.getUserInfo();
    }

    @Override
    protected void initView() {
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvMobileLocation = findViewById(R.id.tv_mobile_location);
        rlChangePassword = findViewById(R.id.rl_change_password);
        rlChangePassword.setOnClickListener(this);
        tvUserPhone.setText(userInfo.userPhone);
        switch (userInfo.mobileLocationCode) {
            case "86":
                tvMobileLocation.setText("中国大陆");
                break;
            case "81":
                tvMobileLocation.setText("日本");
                break;
            case "82":
                tvMobileLocation.setText("韩国");
                break;
            default:
                tvMobileLocation.setText("");
                break;
        }

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void InitDataView() {

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
