package com.juhao.home;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.BaseActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class TestActivity extends BaseActivity {
    @Override
    protected void initData() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
    setContentView(R.layout.activity_test);
    requestPermissions(new String[]{ACCESS_FINE_LOCATION},200);
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void InitDataView() {

    }
}
