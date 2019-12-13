package com.juhao.home.ui;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.bean.GetConfigReq;
import com.juhao.home.R;
import com.util.Constance;
import com.view.CommonPopWindow;
import com.view.PickerScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivingSettingAlarmSetActivity extends BaseActivity implements View.OnClickListener, CommonPopWindow.ViewClickListener {

    private String iotId;
    private TextView tv_sensitivity;
    private TextView tv_report_frequency;
    private ImageView iv_day_switch;
    private boolean isOpen;
    private List<GetConfigReq.DatasBean> datasBeanList;
    private int current;
    private String[] array;
    private String[] arrayValue;
    private String categoryName;
    private String value;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_living_setting_alarm);
        iv_day_switch = findViewById(R.id.iv_day_switch);
        View rl_sensitivity=findViewById(R.id.rl_sensitivity);
        tv_sensitivity = findViewById(R.id.tv_sensitivity);
        View rl_frequency=findViewById(R.id.rl_frequency);
        tv_report_frequency = findViewById(R.id.tv_report_frequency);
        View rl_alarm_period=findViewById(R.id.rl_alarm_period);
        iv_day_switch.setOnClickListener(this);
        rl_sensitivity.setOnClickListener(this);
        rl_frequency.setOnClickListener(this);
        rl_alarm_period.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IPCManager.getInstance().getDevice(iotId).getProperties(new IPanelCallback() {
            @Override
            public void onComplete(boolean b, Object o) {
                if (b) {
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
                                if (data.containsKey(Constants.DAY_NIGHT_MODE_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.DAY_NIGHT_MODE_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        isOpen = intValue==0?true:false;
                                        SharePreferenceManager.getInstance().setDayNightMode(intValue);
                                        int finalIntValue = intValue;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(finalIntValue==0){
                                                    iv_day_switch.setBackgroundResource(R.mipmap.kg_on);
                                                }else {
                                                    iv_day_switch.setBackgroundResource(R.mipmap.kg_off);
                                                }
                                            }
                                        });
                                    }
                                }
                                if (data.containsKey(Constants.MOTION_DETECT_SENSITIVITY_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.MOTION_DETECT_SENSITIVITY_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getMotionDetectSensitivity()) {
                                            SharePreferenceManager.getInstance().setMotionDetectSensitivity(intValue);
                                        }
                                        int finalIntValue1 = intValue;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                switch (finalIntValue1){
                                                    case 0:
                                                        tv_sensitivity.setText(getString(R.string.str_close));
                                                        break;
                                                    case 1:
                                                        tv_sensitivity.setText(getString(R.string.str_the_lowest));
                                                        break;
                                                    case 2:
                                                        tv_sensitivity.setText(getString(R.string.str_low));
                                                        break;
                                                    case 3:
                                                        tv_sensitivity.setText(getString(R.string.str_mid));
                                                        break;
                                                    case 4:
                                                        tv_sensitivity.setText(getString(R.string.str_high));
                                                        break;
                                                    case 5:
                                                        tv_sensitivity.setText(getString(R.string.str_the_highest));
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                }
                                if (data.containsKey(Constants.ALARM_FREQUENCY_LEVEL_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.ALARM_FREQUENCY_LEVEL_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getAlarmFrequencyLevel()) {
                                            SharePreferenceManager.getInstance().setAlarmFrequencyLevel(intValue);
                                        }
                                        int finalIntValue1 = intValue;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                switch (finalIntValue1){
                                                    case 0:
                                                        tv_report_frequency.setText(getString(R.string.str_low_frequency));
                                                        break;
                                                    case 1:
                                                        tv_report_frequency.setText(getString(R.string.str_mid_frequency));
                                                        break;
                                                    case 2:
                                                        tv_report_frequency.setText(getString(R.string.str_high_frequency));
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                }


                            } catch (Exception e) {

                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_day_switch:
                    isOpen=!isOpen;
                    if (isOpen){
                       iv_day_switch.setBackgroundResource(R.mipmap.set_btn_on);
                    }else {
                        iv_day_switch.setBackgroundResource(R.mipmap.kg_off);
                    }
                    Map<String, Object> param = new HashMap<>();
                    String key=Constants.DAY_NIGHT_MODE_MODEL_NAME;
                    param.put(key,isOpen?0:2);
                    SettingsCtrl.getInstance().updateSettings(iotId, param);
                    break;
                case R.id.rl_sensitivity:
                    current = 0;
                    datasBeanList = new ArrayList<>();
                    array = getResources().getStringArray(R.array.MotionDetectSensitivity);
                    arrayValue = getResources().getStringArray(R.array.MotionDetectSensitivityValue);
                    for(int i = 0; i< array.length; i++){
                        datasBeanList.add(new GetConfigReq.DatasBean(""+i, array[i], arrayValue[i]));
                    }
                    setAddressSelectorPopup(v);
                    break;
                case R.id.rl_frequency:
                    current = 1;
                    datasBeanList = new ArrayList<>();
                    array= getResources().getStringArray(R.array.AlarmFrequencyLevel);
                    arrayValue= getResources().getStringArray(R.array.AlarmFrequencyLevelValue);
                    for(int i = 0; i< array.length; i++){
                        datasBeanList.add(new GetConfigReq.DatasBean(""+i, array[i], arrayValue[i]));
                    }
                    setAddressSelectorPopup(v);
                    break;
                case R.id.rl_alarm_period:

                    break;
            }
    }


    /**
     * 将选择器放在底部弹出框
     * @param v
     */
    private void setAddressSelectorPopup(View v) {
        int screenHeigh = getResources().getDisplayMetrics().heightPixels;
        CommonPopWindow.newBuilder()
                .setView(R.layout.pop_picker_selector_bottom)
//                .setAnimationStyle(R.style.AnimUp)
                .setBackgroundDrawable(new BitmapDrawable())
                .setSize(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(screenHeigh * 0.3f))
                .setViewOnClickListener(this)
                .setBackgroundDarkEnable(true)
                .setBackgroundAlpha(0.7f)
                .setBackgroundDrawable(new ColorDrawable(999999))
                .build(this)
                .showAsBottom(v);
    }


    @Override
    public void getChildView(PopupWindow mPopupWindow, View view, int mLayoutResId) {
        switch (mLayoutResId) {
            case R.layout.pop_picker_selector_bottom:
                TextView imageBtn = view.findViewById(R.id.img_guanbi);
                PickerScrollView addressSelector = view.findViewById(R.id.address);

                // 设置数据，默认选择第一条
                addressSelector.setData(datasBeanList);
                addressSelector.setSelected(0);
                categoryName=datasBeanList.get(0).getCategoryName();
                value=datasBeanList.get(0).getState();
                //滚动监听
                addressSelector.setOnSelectListener(new PickerScrollView.onSelectListener() {
                    @Override
                    public void onSelect(GetConfigReq.DatasBean pickers) {
                        categoryName = pickers.getCategoryName();
                        value = pickers.getState();
                    }
                });

                //完成按钮
                imageBtn.setOnClickListener(new View.OnClickListener() {

                    private String key;

                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                        Map<String, Object> param = new HashMap<>();
                        if(current==0){
                            tv_sensitivity.setText(categoryName);
                            key=Constants.MOTION_DETECT_SENSITIVITY_MODEL_NAME;
                        }else {
                            tv_report_frequency.setText(categoryName);
                            key = Constants.ALARM_FREQUENCY_LEVEL_MODEL_NAME;
                        }
                        param.put(key,Integer.parseInt(value));
                        SettingsCtrl.getInstance().updateSettings(iotId, param);
                    }
                });
                break;
        }
    }

}
