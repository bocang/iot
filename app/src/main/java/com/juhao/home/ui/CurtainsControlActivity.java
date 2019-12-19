package com.juhao.home.ui;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.BaseActivity;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelEventCallback;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CurtainsControlActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_curtain_position;
    private SeekBar seekBar;
    private TextView tv_open;
    private TextView tv_pause;
    private TextView tv_close;
    private int currentP;
    private TextView font_open;
    private TextView font_pause;
    private TextView font_close;
    private String iotId;
    private int value;
    private ImageView iv_left;
    private ImageView iv_right;
    private float oldScaleX=1;
    private float oldWidth;
    private Animation scaleAnimation;
    private Animation scaleAnimation2;
    private int i;
    private int position;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_curtain_control);
        fullScreen(this);
        RelativeLayout rl_setting=findViewById(R.id.rl_setting);
        tv_curtain_position = findViewById(R.id.tv_curtain_position);
        seekBar = findViewById(R.id.seekb_curtain);
        font_open = findViewById(R.id.font_open);
        tv_open = findViewById(R.id.tv_open);
        font_pause = findViewById(R.id.font_pause);
        tv_pause = findViewById(R.id.tv_pause);
        font_close = findViewById(R.id.font_close);
        tv_close = findViewById(R.id.tv_close);
        iv_left = findViewById(R.id.iv_left);
        iv_right = findViewById(R.id.iv_right);
        rl_setting.setOnClickListener(this);
        font_open.setOnClickListener(this::onClick);
        font_pause.setOnClickListener(this::onClick);
        font_close.setOnClickListener(this::onClick);
        oldWidth=UIUtils.dip2PX(128);
        tv_curtain_position.setText(getString(R.string.str_curtain_position)+"  "+"0%");
//        iv_left.setLayoutParams(new RelativeLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtils.logE("onProgressChanged",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtils.logE("onStopTrackingTouch",seekBar.getProgress()+"");
            setProperties("CurtainPosition",seekBar.getProgress());
            tv_curtain_position.setText(getString(R.string.str_curtain_position)+"  "+seekBar.getProgress()+"%");
            if(seekBar.getProgress()>80){
                startAnimation(1000,20);
            }else {
            startAnimation(1000,100-seekBar.getProgress());
            }

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
                        if(ciotId!=null&&ciotId.equals(CurtainsControlActivity.this.iotId)){
                            JSONObject items=jsonObject.optJSONObject(Constance.items);
                            if(items!=null){
                            JSONObject CurtainPosition=items.optJSONObject(Constance.CurtainPosition);
                            if(CurtainPosition!=null){
                                position = CurtainPosition.optInt(Constance.value,seekBar.getProgress());
                            }
                            JSONObject CurtainOperation=items.optJSONObject(Constance.CurtainOperation);
                            int valueOperate=CurtainOperation.optInt(Constance.valid,0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (valueOperate == 2) {
                                        font_pause.performClick();
                                        handler.post(setProgressPostion);
                                    } else if (valueOperate == 0) {
                                        if(seekBar.getProgress()<100){
//                                            handler.postDelayed(setProgressPostion,20*(100));
                                        }else {
//                                            handler.post(setProgressPostion);
                                        }
                                    } else if (valueOperate == 1) {
                                        if(seekBar.getProgress()>1){
//                                            handler.postDelayed(setProgressPostion,20*(100));
                                        }else {
//                                            handler.post(setProgressPostion);
                                        }
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
        getProperties();

    }

    private void getProperties() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/get")
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
                LogUtils.logE("getproperty",ioTResponse.getData()+"");
                try {
                    JSONObject jsonObject=new JSONObject(String.valueOf(ioTResponse.getData()));
                    if(jsonObject!=null){
                        JSONObject CurtainPos=jsonObject.getJSONObject(Constance.CurtainPosition);
                        int position=CurtainPos.optInt(Constance.value,0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seekBar.setProgress(value);
                                tv_curtain_position.setText(getString(R.string.str_curtain_position)+"  "+seekBar.getProgress()+"%");
                                if(seekBar.getProgress()>80){
                                    startAnimation(10,20);
                                }else {
                                startAnimation(10,100-seekBar.getProgress());
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }});

    }

    private void startAnimation(int duration,int i) {
        if(i==0)i=1;
        float newWidth= (float) (UIUtils.dip2PX(128)*i*0.01);
        LogUtils.logE("newwidth",newWidth+"");

        LogUtils.logE("oldWidth", oldWidth +"");
        float scalex=newWidth/ oldWidth;
        oldWidth = newWidth;
//        newWidth= oldWidth *scalex;

        LogUtils.logE("scalex",scalex+"");
        LogUtils.logE("oldScalex",oldScaleX+"");
        scalex=scalex*oldScaleX;
//        LogUtils.logE("bitmapWithd",iv_left.getDrawingCache().getWidth()+"");
//        if(seekBar.getProgress()>80){
//            return;
//        }
        scaleAnimation = new ScaleAnimation(oldScaleX,scalex,1,1,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation2 = new ScaleAnimation(oldScaleX,scalex,1,1,Animation.RELATIVE_TO_SELF,1,Animation.RELATIVE_TO_SELF,0.5f);
        oldScaleX=scalex;
        scaleAnimation.setDuration(duration);
        scaleAnimation.setFillAfter(true);


        scaleAnimation2.setDuration(duration);
        scaleAnimation2.setFillAfter(true);

//        iv_left2.startAnimation(scaleAnimation3);
//        iv_left.startAnimation(scaleAnimation);
//        scaleAnimation.setFillBefore();
//        scaleAnimation.setInterpolator();
        iv_left.startAnimation(scaleAnimation);
        iv_right.startAnimation(scaleAnimation2);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                iv_left.clearAnimation();
//                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams((int) (finalNewWidth1), ViewGroup.LayoutParams.MATCH_PARENT);
//                params.setMargins(UIUtils.dip2PX(8),UIUtils.dip2PX(8),0,0);
//                iv_left.setLayoutParams(params);
//                params.width= (int) newWidth;
//                new Thread(){
//                    @Override
//                    public void run() {
//                        SystemClock.sleep(500);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                iv_left2.setVisibility(View.INVISIBLE);
////                                iv_left2.clearAnimation();
////                                iv_left2.setBackgroundResource(R.mipmap.img_zcl);
////                                iv_left2.setLayoutParams(params);
//                            }
//                        });
//                    }
//                }.start();
//

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        float finalNewWidth = newWidth;
        scaleAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                RelativeLayout.LayoutParams params2= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                params2.setMargins(0,UIUtils.dip2PX(8),UIUtils.dip2PX(8),0);
//                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                params2.width= (int) finalNewWidth;
//                iv_right.setLayoutParams(params2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }

    @Override
    public void onClick(View v) {
        String identify="CurtainOperation";
        switch (v.getId()){
            case R.id.rl_setting:
                break;
            case R.id.font_open:
                i=seekBar.getProgress();
                currentP = 0;
                value = 0;
                handler.removeCallbacksAndMessages(null);
                refreshUI();
                handler.post(runnableOpen);
                setProperties(identify,value);
                break;
            case R.id.font_pause:
                currentP = 1;
                value = 2;
                refreshUI();
                handler.removeCallbacksAndMessages(null);
                setProperties(identify,value);
                break;
            case R.id.font_close:
                currentP = 2;
                value = 1;
                i=seekBar.getProgress();
                handler.removeCallbacksAndMessages(null);
                refreshUI();
                handler.post(runnableClose);
                setProperties(identify,value);
                break;
        }
    }
    Runnable runnableOpen=new Runnable() {
        @Override
        public void run() {
            if(i==100){
                return;
            }
                i+=1;
                seekBar.setProgress(i);
                tv_curtain_position.setText(getString(R.string.str_curtain_position)+"  "+seekBar.getProgress()+"%");
                if(seekBar.getProgress()>80){
                    startAnimation(1000,20);
                }else {
                startAnimation(1000,100-seekBar.getProgress());
                }
                handler.postDelayed(runnableOpen,20);
        }
    };
    Runnable runnableClose=new Runnable() {
        @Override
        public void run() {
            if(i==0){
                return;
            }
            i-=1;
            seekBar.setProgress(i);
            tv_curtain_position.setText(getString(R.string.str_curtain_position)+"  "+seekBar.getProgress()+"%");
            if(seekBar.getProgress()>80){
                startAnimation(1000,20);
            }else {
                startAnimation(1000,100-seekBar.getProgress());
            }
            handler.postDelayed(runnableClose,20);
        }
    };
    Runnable setProgressPostion=new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(position);
            tv_curtain_position.setText(getString(R.string.str_curtain_position) + "  " + seekBar.getProgress() + "%");
            if (seekBar.getProgress() > 80) {
                startAnimation(10, 20);
            } else {
                startAnimation(10, 100 - seekBar.getProgress());
            }
        }
    };

    private void refreshUI() {

        font_close.setTextColor(Color.WHITE);
        font_open.setTextColor(Color.WHITE);
        font_pause.setTextColor(Color.WHITE);
        tv_open.setTextColor(Color.WHITE);
        tv_pause.setTextColor(Color.WHITE);
        tv_close.setTextColor(Color.WHITE);
        switch (currentP){
            case 0:

                font_open.setTextColor(getResources().getColor(R.color.blue_theme));
                tv_open.setTextColor(getResources().getColor(R.color.blue_theme));
                break;
            case 1:
                font_pause.setTextColor(getResources().getColor(R.color.blue_theme));
                tv_pause.setTextColor(getResources().getColor(R.color.blue_theme));
                break;
            case 2:
                font_close.setTextColor(getResources().getColor(R.color.blue_theme));
                tv_close.setTextColor(getResources().getColor(R.color.blue_theme));
                break;
        }

    }
    private void setProperties(String identify, Object value) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
        jsonObject.put(identify,value);
        maps.put("items",jsonObject);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
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

}
