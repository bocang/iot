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
    private int valueSet;
    private TextView tv_temp;
    private QuickAdapter<WarmIconBean> adapter;

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
        adapter = new QuickAdapter<WarmIconBean>(this,R.layout.item_get_warm_bean) {
            @Override
            protected void convert(BaseAdapterHelper helper, WarmIconBean item) {
            helper.setText(R.id.tv_name,item.name);
            helper.setText(R.id.tv_icon, Html.fromHtml(item.icon));
            View bg=helper.getView(R.id.ll_bg);
            if(item.isopen){
                helper.setTextColor(R.id.tv_name,getResources().getColor(R.color.blue_theme));
                helper.setTextColor(R.id.tv_icon,getResources().getColor(R.color.blue_theme));
            }else {
                helper.setTextColor(R.id.tv_name,getResources().getColor(R.color.white));
                helper.setTextColor(R.id.tv_icon,getResources().getColor(R.color.white));
            }
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
//                                                                iconBeans.get(position).isopen=(valueSet==0?false:true);
//                                                                adapter.replaceAll(iconBeans);
                                                            }
                                                        });
                                                        if(ioTResponse.getCode()==200){
                                                        getProperty();
                                                        }
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

            private int dryValue;
            private int ventilationValue;
            private int blowValue;
            private int warmup2Value;
            private int swingValue;
            private int warmup1Value;
            private int batheValue;
            private int switchLightValue;
            private int pws;
            private Double value;

            @Override
            public void onCommand(String s, String s1) {
                LogUtils.logE("command",s+","+s1);
                getProperty();
                if(true){
                    return;
                }
                try {
                    JSONObject jsonObject=new JSONObject(s1);
                    if(jsonObject!=null){
                        String ciotId=jsonObject.optString(Constance.iotId);
                        if(ciotId!=null&&ciotId.equals(iotId)){
                            JSONObject items=jsonObject.optJSONObject(Constance.items);
                            if(items!=null){
                                JSONObject valueObject=items.optJSONObject(Constance.TargetTemperature);
                                JSONObject PowerSwitch=items.optJSONObject("PowerSwitch");
                                JSONObject SwitchLight=items.optJSONObject("SwitchLight");
                                JSONObject Bathe=items.optJSONObject("Bathe");
                                JSONObject Warmup1=items.optJSONObject("Warmup1");
                                JSONObject Swing=items.optJSONObject("Swing");
                                JSONObject Warmup2=items.optJSONObject("Warmup2");
                                JSONObject Blow=items.optJSONObject("Blow");
                                JSONObject Ventilation=items.optJSONObject("Ventilation");
                                JSONObject Dry=items.optJSONObject("Dry");

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (valueObject != null) {
                                                    value = valueObject.getDouble(Constance.value);
                                                    tv_temp.setText(""+value);
                                                }
                                                if(PowerSwitch!=null){
                                                    pws = PowerSwitch.getInt(Constance.value);
                                                    iconBeans.get(0).isopen=(pws==0?false:true);
                                                }
                                                if(SwitchLight!=null){
                                                    switchLightValue = SwitchLight.getInt(Constance.value);
                                                    iconBeans.get(1).isopen=(switchLightValue ==0?false:true);
                                                }
                                                if(Bathe!=null){
                                                    batheValue = Bathe.getInt(Constance.value);
                                                    iconBeans.get(2).isopen=(batheValue ==0?false:true);}
                                                if(Warmup1!=null){
                                                    warmup1Value = Warmup1.getInt(Constance.value);
                                                    iconBeans.get(3).isopen=(warmup1Value ==0?false:true);
                                                }
                                                if(Swing!=null){
                                                    swingValue = Swing.getInt(Constance.value);
                                                    iconBeans.get(4).isopen=(swingValue ==0?false:true);
                                                }
                                                if(Warmup2!=null){
                                                    warmup2Value = Warmup2.getInt(Constance.value);
                                                    iconBeans.get(5).isopen=(warmup2Value ==0?false:true);
                                                }
                                                if(Blow!=null){
                                                    blowValue = Blow.getInt(Constance.value);
                                                    iconBeans.get(6).isopen=(blowValue ==0?false:true);
                                                }
                                                if(Ventilation!=null){
                                                    ventilationValue = Ventilation.getInt(Constance.value);
                                                    iconBeans.get(7).isopen=(ventilationValue ==0?false:true);
                                                }
                                                if(Dry!=null){
                                                    dryValue = Dry.getInt(Constance.value);
                                                    iconBeans.get(8).isopen=(dryValue ==0?false:true);
                                                }
                                                adapter.replaceAll(iconBeans);
                                            } catch (Exception e) {

                                            }
                                        }
                                    });



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

            private int dryValue;
            private int ventilationValue;
            private int blowValue;
            private int warmup2Value;
            private int swingValue;
            private int warmup1Value;
            private int batheValue;
            private int switchLightValue;
            private int pws;
            private Double value;

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {

                JSONObject data= (JSONObject) ioTResponse.getData();
                LogUtils.logE("data",data.toString());
                if(data!=null) {
                    try {
                        JSONObject valueObject = data.optJSONObject("TargetTemperature");
                        JSONObject PowerSwitch=data.optJSONObject("PowerSwitch");
                        JSONObject SwitchLight=data.optJSONObject("SwitchLight");
                        JSONObject Bathe=data.optJSONObject("Bathe");
                        JSONObject Warmup1=data.optJSONObject("Warmup1");
                        JSONObject Swing=data.optJSONObject("Swing");
                        JSONObject Warmup2=data.optJSONObject("Warmup2");
                        JSONObject Blow=data.optJSONObject("Blow");
                        JSONObject Ventilation=data.optJSONObject("Ventilation");
                        JSONObject Dry=data.optJSONObject("Dry");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                    if (valueObject != null) {
                                        value = valueObject.getDouble(Constance.value);
                                        tv_temp.setText(""+value);
                                    }
                                        if(PowerSwitch!=null){
                                            pws = PowerSwitch.getInt(Constance.value);
                                            iconBeans.get(0).isopen=(pws==0?false:true);
                                        }
                                        if(SwitchLight!=null){
                                            switchLightValue = SwitchLight.getInt(Constance.value);
                                            iconBeans.get(1).isopen=(switchLightValue ==0?false:true);
                                        }
                                        if(Bathe!=null){
                                            batheValue = Bathe.getInt(Constance.value);
                                            iconBeans.get(2).isopen=(batheValue ==0?false:true);}
                                        if(Warmup1!=null){
                                            warmup1Value = Warmup1.getInt(Constance.value);
                                            iconBeans.get(3).isopen=(warmup1Value ==0?false:true);
                                        }
                                        if(Swing!=null){
                                            swingValue = Swing.getInt(Constance.value);
                                            iconBeans.get(4).isopen=(swingValue ==0?false:true);
                                        }
                                        if(Warmup2!=null){
                                            warmup2Value = Warmup2.getInt(Constance.value);
                                            iconBeans.get(5).isopen=(warmup2Value ==0?false:true);
                                        }
                                        if(Blow!=null){
                                            blowValue = Blow.getInt(Constance.value);
                                            iconBeans.get(6).isopen=(blowValue ==0?false:true);
                                        }
                                        if(Ventilation!=null){
                                            ventilationValue = Ventilation.getInt(Constance.value);
                                            iconBeans.get(7).isopen=(ventilationValue ==0?false:true);
                                        }
                                        if(Dry!=null){
                                            dryValue = Dry.getInt(Constance.value);
                                            iconBeans.get(8).isopen=(dryValue ==0?false:true);
                                        }
                                    adapter.replaceAll(iconBeans);
                                    } catch (Exception e) {

                                    }
                                }
                            });

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
