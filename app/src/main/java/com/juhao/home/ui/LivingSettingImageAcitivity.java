package com.juhao.home.ui;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.aliyun.iotx.linkvisual.mqtt.ChannelManager;
import com.bean.GetConfigReq;
import com.juhao.home.R;
import com.util.Constance;
import com.util.LogUtils;
import com.view.CommonPopWindow;
import com.view.PickerScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivingSettingImageAcitivity  extends BaseActivity implements View.OnClickListener, CommonPopWindow.ViewClickListener {

    private TextView tv_day_night_mode;
    private TextView tv_flip_status;
    private List<GetConfigReq.DatasBean> datasBeanList;
    private String categoryName;
    private String value;
    private String iotId;
    private Handler uiHandler;
    private String[] array;
    private String[] arrayValue;
    private int current;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_living_setting_image);
        uiHandler = new Handler(Looper.getMainLooper());
        View rl_day_night_mode=findViewById(R.id.rl_day_night_mode);
        tv_day_night_mode = findViewById(R.id.tv_day_night_mode);
        View rl_flip=findViewById(R.id.rl_flip);
        tv_flip_status = findViewById(R.id.tv_flip_status);
        rl_day_night_mode.setOnClickListener(this);
        rl_flip.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }
    @Override
    public void onResume() {
        super.onResume();
        //注册物模型属性修改的监听
        SharePreferenceManager.getInstance().registerOnCallSetListener(mOnCallListener);
        //获取物模型属性
        SettingsCtrl.getInstance().getProperties(iotId);
        //监听物模型属性的变化以防止其他途径对物模型属性的修改
        ChannelManager.getInstance().registerListener(iMobileMsgListener);
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
                                        if (intValue != SharePreferenceManager.getInstance().getDayNightMode()) {
                                            SharePreferenceManager.getInstance().setDayNightMode(intValue);
                                        }
                                        int finalIntValue = intValue;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                switch (finalIntValue){
                                                    case 0:
                                                        tv_day_night_mode.setText(getString(R.string.str_day_mode));
                                                        break;
                                                    case 1:
                                                        tv_day_night_mode.setText(getString(R.string.str_night_mode));
                                                        break;
                                                    case 2:
                                                        tv_day_night_mode.setText(getString(R.string.str_auto_mode));
                                                        break;
                                                }
                                            }
                                        });

                                    }
                                }
                                if (data.containsKey(Constants.IMAGE_FLIP_STATE_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.IMAGE_FLIP_STATE_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getImageFlip()) {
                                            SharePreferenceManager.getInstance().setImageFlip(intValue);
                                        }
                                        int finalIntValue1 = intValue;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(finalIntValue1 ==0){
                                                    tv_flip_status.setText(getString(R.string.str_normal));
                                                }else {
                                                    tv_flip_status.setText(getString(R.string.str_flip_mode));
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
    public void onPause() {
        super.onPause();
        SharePreferenceManager.getInstance().unRegisterOnCallSetListener(mOnCallListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        ChannelManager.getInstance().unRegisterListener(iMobileMsgListener);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_day_night_mode:
                current = 0;
                datasBeanList = new ArrayList<>();
                array= getResources().getStringArray(R.array.DayNightMode);
                arrayValue= getResources().getStringArray(R.array.DayNightModeValue);
                for(int i = 0; i< array.length; i++){
                datasBeanList.add(new GetConfigReq.DatasBean(""+i, array[i], arrayValue[i]));
                }
                setAddressSelectorPopup(v);

                break;
            case R.id.rl_flip:
                current = 1;
                datasBeanList = new ArrayList<>();
                array = getResources().getStringArray(R.array.ImageFlipState);
                arrayValue = getResources().getStringArray(R.array.ImageFlipStateValue);
                for(int i = 0; i< array.length; i++){
                    datasBeanList.add(new GetConfigReq.DatasBean(""+i, array[i], arrayValue[i]));
                }
                setAddressSelectorPopup(v);
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
                        if(current==1){
                            tv_flip_status.setText(categoryName);
                            key=Constants.IMAGE_FLIP_STATE_MODEL_NAME;
                        }else {
                            tv_day_night_mode.setText(categoryName);
                            key = Constants.DAY_NIGHT_MODE_MODEL_NAME;
                        }
                        param.put(key,Integer.parseInt(value));
                        SettingsCtrl.getInstance().updateSettings(iotId, param);
                    }
                });
                break;
        }
    }

    private SharePreferenceManager.OnCallSetListener mOnCallListener = new SharePreferenceManager.OnCallSetListener() {
        @Override
        public void onCallSet(String key) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtils.logE("refreshui",key+"");
                    refreshUI(key);
                }
            });
        }
    };

    private void refreshUI(String key) {
        if (key != null && !key.trim().equals("")) {
            if (key.equals(getString(com.aliyun.iot.demo.ipcview.R.string.day_night_mode_key))) {
                int mode=SharePreferenceManager.getInstance().getDayNightMode();
                String[] array=getResources().getStringArray(com.aliyun.iot.demo.ipcview.R.array.DayNightMode);
                if(mode==0){
                    tv_day_night_mode.setText(array[0]);
                }else if(mode==1){
                    tv_day_night_mode.setText(array[1]);
                }else {
                    tv_day_night_mode.setText(array[2]);
                }
            }else if(key.equals(getString(R.string.image_flip_status_key))){
                int mode=SharePreferenceManager.getInstance().getImageFlip();
                String[] array=getResources().getStringArray(com.aliyun.iot.demo.ipcview.R.array.ImageFlipState);
                if(mode==0){
                    tv_flip_status.setText(array[0]);
                }else if(mode==1){
                    tv_flip_status.setText(array[1]);
                }else {
                }

            }
        }
    }

    private ChannelManager.IMobileMsgListener iMobileMsgListener = new ChannelManager.IMobileMsgListener() {
        @Override
        public void onCommand(String topic, String msg) {
//            Log.e(TAG, "ChannelManager.IMobileMsgListener    topic:" + topic + "     msg:" + msg);
            if (topic.equals("/thing/properties")) {
                SettingsCtrl.getInstance().getProperties(iotId);
            }
        }
    };
}
