package com.juhao.home.ui;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.bean.WarmIconBean;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetWarmDeviceControlActivity extends BaseActivity {

    private String iotId;
    private List<WarmIconBean> iconBeans;
    private Object valueSet;
    private TextView tv_temp;

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_get_warm_control);
        fullScreen(this);
        tv_temp = findViewById(R.id.tv_temp);
        GridView gv_get_warm=findViewById(R.id.gv_get_warm);
        QuickAdapter<WarmIconBean>adapter=new QuickAdapter<WarmIconBean>(this,R.layout.item_get_warm_bean) {
            @Override
            protected void convert(BaseAdapterHelper helper, WarmIconBean item) {
            helper.setText(R.id.tv_name,item.name);
            helper.setText(R.id.tv_icon, Html.fromHtml(item.icon));
            View bg=helper.getView(R.id.ll_bg);
            bg.setLayoutParams(new ViewGroup.LayoutParams(UIUtils.getScreenWidth(GetWarmDeviceControlActivity.this)/3, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        };
        gv_get_warm.setAdapter(adapter);
        iconBeans = new ArrayList<>();
        iconBeans.add(new WarmIconBean("&#xe62c;",getString(R.string.str_kaiguan),"PowerSwitch"));
        iconBeans.add(new WarmIconBean("&#xe62d;",getString(R.string.str_zhaoming),"SwitchLight"));
        iconBeans.add(new WarmIconBean("&#xe630;",getString(R.string.str_muyu),"Bathe"));
        iconBeans.add(new WarmIconBean("&#xe62e;",getString(R.string.str_qunuan)+"1","Warmup1"));
        iconBeans.add(new WarmIconBean("&#xe635;",getString(R.string.str_baifeng),"Swing"));
        iconBeans.add(new WarmIconBean("&#xe631;",getString(R.string.str_qunuan)+"2","Warmup2"));
        iconBeans.add(new WarmIconBean("&#xe633;",getString(R.string.str_chuifeng),"Blow"));
        iconBeans.add(new WarmIconBean("&#xe632;",getString(R.string.str_huanqi),"Ventilation"));
        iconBeans.add(new WarmIconBean("&#xe634;",getString(R.string.str_ganzao),"Dry"));
        adapter.replaceAll(iconBeans);
        gv_get_warm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String identifiy=iconBeans.get(position).identify;
                valueSet = 1;
                ApiClientForIot.getDevProperties(iotId, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        JSONObject data= (JSONObject) ioTResponse.getData();
                        if(data!=null) {
                            try {
                                    JSONObject valueObject = data.getJSONObject(identifiy);
                                    if (valueObject != null) {
                                        final int value = valueObject.getInt(Constance.value);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (value == 1) {
                                                    valueSet =0;
                                                } else {
                                                    valueSet =1;
                                                }
                                                ApiClientForIot.setDevProperties(iotId,identifiy, valueSet, new IoTCallback() {
                                                    @Override
                                                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                                                    }

                                                    @Override
                                                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                MyToast.show(GetWarmDeviceControlActivity.this,ioTResponse.getLocalizedMsg());

                                                            }
                                                        });
                                                        getProperty();
                                                    }
                                                });
                                            }
                                        });
                                    }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

            }
        });
        MobileChannel.getInstance().registerDownstreamListener(true, new IMobileDownstreamListener() {
            @Override
            public void onCommand(String s, String s1) {
                LogUtils.logE("command",s+","+s1);
                try {
                    JSONObject jsonObject=new JSONObject(s1);
                    if(jsonObject!=null){
                        String ciotId=jsonObject.optString(Constance.iotId);
                        if(ciotId!=null&&ciotId.equals(iotId)){
                            JSONObject items=jsonObject.optJSONObject(Constance.items);
                            if(items!=null){
                                JSONObject valueObject=items.optJSONObject(Constance.TargetTemperature);
                                if (valueObject != null) {
                                    final Double value = valueObject.getDouble(Constance.value);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_temp.setText(""+value);
                                        }
                                    });
                                }

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean shouldHandle(String s) {
                LogUtils.logE("shouldHandle",s+"");
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProperty();
    }

    private void getProperty() {
        ApiClientForIot.getDevProperties(iotId, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {

                JSONObject data= (JSONObject) ioTResponse.getData();
                if(data!=null) {
                    try {
                        JSONObject valueObject = data.getJSONObject("TargetTemperature");
                        if (valueObject != null) {
                            final Double value = valueObject.getDouble(Constance.value);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_temp.setText(""+value);
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void InitDataView() {

    }
}
