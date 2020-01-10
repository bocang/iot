package com.juhao.home.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.R;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.net.ApiClient;
import com.util.json.JSONObject;
import com.view.MyToast;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RoomAddActivity extends BaseActivity {

    private EditText et_name;
    private GridView gv_tuijian;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_room_add);
        et_name = findViewById(R.id.et_room_name);
        gv_tuijian = findViewById(R.id.gv_tuijian);
        List<String> tuijianList=new ArrayList<>();
        tuijianList.add("客厅");
        tuijianList.add("主卧");
        tuijianList.add("次卧");
        tuijianList.add("餐厅");
        tuijianList.add("厨房");
        tuijianList.add("书房");
        tuijianList.add("玄关");
        tuijianList.add("阳台");
        tuijianList.add("儿童房");
        tuijianList.add("衣帽间");

        QuickAdapter adapter=new QuickAdapter<String>(this,R.layout.item_tuijian) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
            helper.setText(R.id.tv_name,item);
            }
        };
        gv_tuijian.setAdapter(adapter);
        adapter.replaceAll(tuijianList);
        gv_tuijian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_name.setText(tuijianList.get(position));
            }
        });
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void InitDataView() {

    }

    @Override
    public void save(View v) {
        super.save(v);
        String name=et_name.getText().toString();
        if(TextUtils.isEmpty(name)){
            MyToast.show(this,getString(R.string.str_input_room_name));
            return;
        }
        IoTCredentialManageImpl ioTCredentialManage = IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
        ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
            @Override
            public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                String identity=ioTCredentialData.identity;
                if(identity!=null){
                    ApiClient.AddRoom(identity, name, new Callback<String>() {
                        @Override
                        public String parseNetworkResponse(Response response, int id) throws Exception {
                            return response.body().string();
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public String onResponse(String response, int id) {
                            JSONObject jsonObject=new JSONObject(response);
                            MyToast.show(RoomAddActivity.this,getString(R.string.str_excute_success));
                            finish();
                            return null;
                        }
                    });
                }
            }

            @Override
            public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {

            }
        });

    }
}
