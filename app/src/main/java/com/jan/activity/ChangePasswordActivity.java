package com.jan.activity;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.BaseActivity;
import com.jan.activity.AccountCodeActivity;
import com.juhao.home.R;

/**
 * 修改密码
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private EditText edt_password;
    private ImageView iv_eye_password;
    private ImageView iv_clear_password;
    private TextView tv_submit;

    private boolean isEyePassword = false;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        edt_password = findViewById(R.id.edt_password);
        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    tv_submit.setEnabled(false);
                } else {
                    tv_submit.setEnabled(true);
                }
            }
        });
        iv_eye_password = findViewById(R.id.iv_eye_password);
        iv_eye_password.setOnClickListener(this);
        iv_clear_password = findViewById(R.id.iv_clear_password);
        iv_clear_password.setOnClickListener(this);
        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setContentView(R.layout.activity_change_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:// finish
                finish();
                break;
            case R.id.iv_eye_password:// 明示/隐藏密码
                isPassword();
                break;
            case R.id.iv_clear_password:// 清除密码
                edt_password.setText("");
                break;
            case R.id.tv_submit:// 完成
                submitPassword();
                break;
        }
    }

    /**
     * 是否显示密码
     */
    private void isPassword() {
        isEyePassword = !isEyePassword;
        if (isEyePassword) {
            HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance();
            edt_password.setTransformationMethod(method);
            iv_eye_password.setImageResource(R.mipmap.icon_bkj_on);
        } else {
            PasswordTransformationMethod method = PasswordTransformationMethod.getInstance();
            edt_password.setTransformationMethod(method);
            iv_eye_password.setImageResource(R.mipmap.icon_bkj);
        }
        // 切换后将EditText光标置于末尾
        CharSequence charSequence = edt_password.getText();
        if (charSequence instanceof Spannable) {
            Spannable spannable = (Spannable) charSequence;
            Selection.setSelection(spannable, charSequence.length());
        }
    }

    private void submitPassword() {
        mBundle.putString("password", edt_password.getText().toString().trim());
        intent2Activity(AccountCodeActivity.class,mBundle);
        finish();
    }

}
