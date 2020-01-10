package com.juhao.home.ui;

import android.view.View;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.RoomBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.net.ApiClient;
import com.util.Constance;
import com.util.LogUtils;
import com.view.TextViewPlus;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class HomeSettingActivity extends BaseActivity implements View.OnClickListener {

    private View rl_room_manage;
    private TextViewPlus tv_room_count;

    @Override
    protected void InitDataView() {
        final IoTCredentialManageImpl ioTCredentialManage = IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
        ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
            @Override
            public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                String identityId = ioTCredentialData.identity;
                ApiClient.getRoomList(identityId, new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response, int id) throws Exception {
                        return response.body().string();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public String onResponse(String response, int id) {
                        LogUtils.logE("roomlist",response);
                        try {
                            JSONObject result=new JSONObject(response);
                            JSONArray data=result.getJSONArray(Constance.data);
                            if(data.length()>0){
                                List<RoomBean> roomBeans=new Gson().fromJson(data.toString(),new TypeToken<List<RoomBean>>(){}.getType());


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

            }

            @Override
            public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {

            }
        });

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_home_setting);
        rl_room_manage = findViewById(R.id.rl_room_manage);
        tv_room_count = findViewById(R.id.tv_room_count);
        rl_room_manage.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_room_manage:

                break;

        }
    }
}
