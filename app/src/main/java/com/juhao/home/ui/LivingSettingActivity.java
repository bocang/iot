package com.juhao.home.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.util.Constance;
import com.view.MyToast;

import java.util.List;

public class LivingSettingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "livingsetting";
    private Intent intent;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }
    private String iotId = "";
    @Override
    protected void initView() {
        setContentView(R.layout.activity_living_setting);
        iotId = getIntent().getStringExtra("iotId");
//        SettingsPreferenceFragment fragment = new SettingsPreferenceFragment();
//        fragment.setIotId(iotId);
//        getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        View rl_image=findViewById(R.id.rl_image);
        View rl_alarm_setting=findViewById(R.id.rl_alarm_setting);
        View rl_sd_card=findViewById(R.id.rl_sd_card);
        TextView tv_sd_card_status=findViewById(R.id.tv_sd_card_status);
        TextView tv_unbind=findViewById(R.id.tv_unbind);
        TextView tv_restart=findViewById(R.id.tv_restart);
        rl_image.setOnClickListener(this);
        rl_alarm_setting.setOnClickListener(this);
        rl_sd_card.setOnClickListener(this);
        tv_unbind.setOnClickListener(this);
        tv_restart.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_image:
                intent = new Intent(this,LivingSettingImageAcitivity.class);
                intent.putExtra(Constance.iotId,iotId);
                startActivity(intent);
                break;
            case R.id.rl_alarm_setting:
                intent = new Intent(this,LivingSettingAlarmSetActivity.class);
                intent.putExtra(Constance.iotId,iotId);
                startActivity(intent);
                break;
            case R.id.rl_sd_card:
                intent=new Intent(this,SdCardActivity.class);
                intent.putExtra(Constance.iotId,iotId);
                startActivity(intent);
                break;
            case R.id.tv_unbind:
//                showCheckDialog();
                UIUtils.showSingleWordDialog(this, getString(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_dialog_title), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestUnbind(iotId, new IoTCallback() {
                            @Override
                            public void onFailure(IoTRequest ioTRequest, Exception e) {
                                Log.e(TAG,"解绑失败：e:"+e.toString());
                                showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_err);
                            }

                            @Override
                            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                                if(ioTResponse != null){
                                    if(ioTResponse.getCode()==200){
                                        showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_success);
                                        //FIXME 根据用户自身跳转到对应逻辑，目前逻辑是直接跳转到飞燕DemoApp首页
                                        //Router.getInstance().toUrl(getActivity(), "page/ilopmain");
                                        Intent intent = new Intent(LivingSettingActivity.this, MainActivity.class);
//                                List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);
//                                boolean isValid = !activities.isEmpty();
//                                if(isValid){
//                                    startActivityForResult(intent, RESULT_OK);
//                                }
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_err);
                                    }
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.tv_restart:
                UIUtils.showSingleWordDialog(this, getString(R.string.str_restart_tips), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IPCManager.getInstance().getDevice(iotId).reboot(new IPanelCallback() {
                            @Override
                            public void onComplete(boolean b, Object o) {
                                Log.d(TAG, "reboot:" + b + "       o:" + (o != null ? String.valueOf(o) : "null"));
                                if(b){
                                    showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_reboot_success);
                                }
                            }
                        });
                    }
                });

                break;
        }
    }
    private void showCheckDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_dialog_title);
        builder.setNegativeButton(com.aliyun.iot.demo.ipcview.R.string.ipc_cancle, null);
        builder.setPositiveButton(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestUnbind(iotId, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {
                        Log.e(TAG,"解绑失败：e:"+e.toString());
                        showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_err);
                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        if(ioTResponse != null){
                            if(ioTResponse.getCode()==200){
                                showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_success);
                                //FIXME 根据用户自身跳转到对应逻辑，目前逻辑是直接跳转到飞燕DemoApp首页
                                //Router.getInstance().toUrl(getActivity(), "page/ilopmain");
                                Intent intent = new Intent(LivingSettingActivity.this, MainActivity.class);
//                                List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);
//                                boolean isValid = !activities.isEmpty();
//                                if(isValid){
//                                    startActivityForResult(intent, RESULT_OK);
//                                }
                                startActivity(intent);
                                finish();

                            }else{
                                showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_setting_unbind_err);
                            }
                        }
                    }
                });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static final String UN_BIND = "/uc/unbindAccountAndDev";
    public static void requestUnbind(String iotId,IoTCallback ioTCallback) {
        Log.d("EqSettingHelp", "_______________" + iotId);
        String apiVersion = "1.0.2";
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setScheme(Scheme.HTTPS)
                .setPath(UN_BIND)
                .setApiVersion(apiVersion)
                .addParam("iotId", iotId);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request,ioTCallback );
    }
}
