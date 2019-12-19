package com.juhao.home.ui;

import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SdCardActivity extends BaseActivity {

    private TextView tv_state;
    private TextView tv_total;
    private TextView tv_remain;
    private TextView tv_format;
    private String iotId;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }
    DecimalFormat decimalFormat=new DecimalFormat("0.00");
    @Override
    protected void initView() {
        setContentView(R.layout.activity_sd_card);
        tv_state = findViewById(R.id.tv_state);
        tv_total = findViewById(R.id.tv_total_store);
        tv_remain = findViewById(R.id.tv_remain);
        tv_format = findViewById(R.id.tv_format_sd_card);
        tv_format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.showSingleWordDialog(SdCardActivity.this, getString(R.string.str_format_sd_card), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> param=new HashMap<>();
                        param.put(Constance.iotId,iotId);
                        param.put(Constance.identifier,"FormatStorageMedium");
                        invokeService(param);
//                        SettingsCtrl.getInstance().updateSettings(iotId, param);
                    }
                });
            }
        });
        IPCManager.getInstance().getDevice(iotId).getProperties( new IPanelCallback() {
            @Override
            public void onComplete(boolean b, Object o) {
            if(b){
                if (o != null && !"".equals(String.valueOf(o))) {
                    JSONObject jsonObject = JSONObject.parseObject(String.valueOf(o));
                    if (jsonObject.containsKey("code")) {
                        int code = jsonObject.getInteger("code");
                        if (code != 200) {
                            return;
                        }
                    }
                    if (jsonObject.containsKey("data")) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONObject tmp;
                            boolean booleanValue;
                            int intValue;
                            float floatValue;
                            JSONObject StorageTotalCapacity=data.getJSONObject("StorageTotalCapacity");
                            JSONObject StorageRemainCapacity=data.getJSONObject("StorageRemainCapacity");
                            double total=StorageTotalCapacity.getDoubleValue("value");
                            double remain=StorageRemainCapacity.getDoubleValue("value");
                            intValue=data.getJSONObject("StorageStatus").getIntValue("value");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String[] sd_state=getResources().getStringArray(R.array.sd_state);
                                    tv_state.setText(sd_state[intValue]);
                                    tv_total.setText(decimalFormat.format(total/1024)+" GB");
                                    tv_remain.setText(decimalFormat.format(remain/1024)+" GB");
                                }
                            });
                        } catch (Exception e) {

                        }
                    }
                }
            }
            }
        });

    }
    private void invokeService(Map<String,Object> maps) {


        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/service/invoke")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
                }

            }
        });
    }

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }
}
