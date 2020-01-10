package com.juhao.home.deviceBiz;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Observable;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.BaseActivity;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.R;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.BuglyHintException;
import com.tencent.bugly.crashreport.BuglyLog;
import com.util.LogUtils;
import com.view.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class WifiSelectActivity extends BaseActivity implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 452;
    private EditText et_pwd;
    private EditText et_wifi;
    private boolean isCanSee;
    private TextView tv_cansee;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_wifi_select);
        fullScreen(this);
        et_wifi = findViewById(R.id.et_wifi);
        et_pwd = findViewById(R.id.et_pwd);
        TextView tv_change_wifi=findViewById(R.id.tv_change_wifi);
        tv_cansee = findViewById(R.id.tv_cansee);
        TextView tv_ensure=findViewById(R.id.tv_ensure);

        tv_change_wifi.setOnClickListener(this);
        tv_cansee.setOnClickListener(this);
        tv_ensure.setOnClickListener(this);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
//            int result=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
//            int result2=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//            int result3=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
//            if(result==PackageManager.PERMISSION_GRANTED&&
//                    result2==PackageManager.PERMISSION_GRANTED&&
//                        result3==PackageManager.PERMISSION_GRANTED){
////                if(isLocServiceEnable(this)){
////                    openGpsSettings();
////                }else {
//                    et_wifi.setText(getSSID());
////                }
//            }else {
                rxPermissions();
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},300);
//            }
        }else {
            et_wifi.setText(""+getSSID());
        }
//        dialog.show();
    }

    @Override
    protected void initData() {

    }

    public List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        final List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }
        final Dialog dialog=new Dialog(this,R.style.customDialog);
        dialog.setContentView(R.layout.dialog_wifi_select);
        ListView lv_wifi=dialog.findViewById(R.id.lv_wifi);
        QuickAdapter<ScanResult> adapter=new QuickAdapter<ScanResult>(this,R.layout.item_wifi) {
            @Override
            protected void convert(BaseAdapterHelper helper, ScanResult item) {
                helper.setText(R.id.tv_name,item.SSID);
            }
        };
        lv_wifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_wifi.setText(wifiList.get(i).SSID);
                dialog.dismiss();
            }
        });
        lv_wifi.setAdapter(adapter);
        adapter.replaceAll(wifiList);
        dialog.show();
        return wifiList;
    }
    private void registerPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            getWifiList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            getWifiList();
        }else if(requestCode==300){
//            if(isLocServiceEnable(this)){
//            openGpsSettings();
//            }else {
            et_wifi.setText(getSSID());
//            }
        }
    }

    /**
     * 获取当前连接WIFI的SSID
     */
    public String getSSID() {
        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                WifiInfo winfo = wm.getConnectionInfo();
                if (winfo != null) {
                    String s = winfo.getSSID();
                    if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                        return s.substring(1, s.length() - 1);
                    }
                }
            }
            return "";
        }catch (Exception e){
            BuglyLog.e("getssid",e.toString());
        }
        return "";

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_change_wifi:
                registerPermission();
                break;
            case R.id.tv_cansee:
                isCanSee = !isCanSee;
                if(isCanSee){
                    //密码可见
                    tv_cansee.setText(getString(R.string.icon_can_see));
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    //密码不可见
                    tv_cansee.setText(getString(R.string.icon_can_not_see));
                    et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.tv_ensure:
                String ssid=et_wifi.getText().toString();
                String pwd=et_pwd.getText().toString();
                if(TextUtils.isEmpty(ssid)||TextUtils.isEmpty(pwd)){
                    MyToast.show(this,"请填写wifi账号和密码");
                    return;
                }
                DemoApplication.wifi=ssid;
                DemoApplication.pwd=pwd;
                startActivity(new Intent(this,AddDeviceTipsActivity.class));
                break;
        }
    }
    /**
     */
    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否获取到了定位权限,获取wifi信息需要
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void rxPermissions() {
        //获取必要权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                RxPermissions rxPermissions=new RxPermissions(this);
                rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);
                rxPermissions.ensure(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);
            }else {
                et_wifi.setText(""+getSSID());

            }
        } else {
            et_wifi.setText(""+getSSID());
        }


    }
    /**
     * 获取连接wifi的ssid
     *
     * @return
     */
    private String getConnectWifiSsid()  {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();

    }

    /**
     *  打开Gps设置界面
     */
    private void openGpsSettings() {
        Intent  intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
