package com.juhao.home.living;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.demo.ipcview.adapter.MyListAdapter;
import com.aliyun.iot.demo.ipcview.beans.TimeSectionForPlan;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.juhao.home.R;
import com.util.Constance;

import java.util.LinkedList;
import java.util.List;

public class LivingAlarmPlanActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private String iotId;

    private MyListAdapter myListAdapter;
    private List<TimeSectionForPlan> timeLst = new LinkedList<>();
    private ListView listView;
    private Button unbindBtn, bindBtn;
    private CheckBox isAllDayCb;
    boolean isAllDay = false;
    private boolean hasPlan = false;
    private String planId = "";
    private String planName = "";
    private List<Integer> eventTypeList = new LinkedList<>();
    Handler uiHandler;

    private final int DEFAULT_PRE_RECORD_DURATION = 5;
    private final int DEFAULT_RECORD_DURATION = 30;
    private final int DEFAULT_STREAM_TYPE = 0;
    private TextView tv_day_time;
    private ImageView iv_day_switch;
    private ImageView iv_week_switch;
    private TextView tv_clock_1;
    private TextView tv_clock_2;
    private TextView tv_everyday;
    private TextView tv_everyweek;
    private boolean isDayLoopOpen;
    private Intent intent;
    private int beginTime;
    private int endTime;

    /**
     * 重置数据
     */
    private void restoreData() {
        isAllDay = false;
        hasPlan = false;
        planId = "";
    }

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        uiHandler = new Handler(getMainLooper());
        setContentView(R.layout.activity_living_alarm_plan);
        View rl_loop_day=findViewById(R.id.rl_loop_day);
        tv_day_time = findViewById(R.id.tv_day_time);
        iv_day_switch = findViewById(R.id.iv_day_switch);
        View rl_loop_week=findViewById(R.id.rl_loop_week);
        iv_week_switch = findViewById(R.id.iv_week_switch);
        tv_clock_1 = findViewById(R.id.tv_clock_1);
        tv_clock_2 = findViewById(R.id.tv_clock_2);
        tv_everyday = findViewById(R.id.tv_everyday);
        tv_everyweek = findViewById(R.id.tv_everyweek);


        rl_loop_day.setOnClickListener(this);
        iv_day_switch.setOnClickListener(this);
        rl_loop_week.setOnClickListener(this);
        iv_week_switch.setOnClickListener(this);

        eventTypeList.add(1);

    }

    @Override
    protected void initData() {
    iotId= getIntent().getStringExtra("iotId");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        iotId = getIntent().getStringExtra("iotId");
//        initView();
//        refreshUI();
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
                                if (data.containsKey(Constants.ALARM_NOTIFY_PLAN_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.ALARM_NOTIFY_PLAN_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        JSONArray valueArray = tmp.getJSONArray("value");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                               boolean everyday=true;
                                               if(valueArray.size()!=7){
                                                   everyday=false;
                                                   tv_clock_1.setTextColor(getResources().getColor(R.color.tv_cccccc));
                                                   tv_clock_2.setTextColor(getResources().getColor(R.color.blue_theme));
                                                   tv_everyweek.setTextColor(Color.BLACK);
                                                   tv_everyday.setTextColor(getResources().getColor(R.color.tv_666666));
                                                   iv_week_switch.setBackgroundResource(R.mipmap.set_btn_on);
                                                   iv_day_switch.setBackgroundResource(R.mipmap.set_btn_off);
                                               }else {
                                                   beginTime = valueArray.getJSONObject(0).getInteger(Constance.BeginTime);
                                                   endTime = valueArray.getJSONObject(0).getInteger(Constance.EndTime);
                                                   iv_week_switch.setBackgroundResource(R.mipmap.set_btn_off);
                                                   iv_day_switch.setBackgroundResource(R.mipmap.set_btn_on);
                                                   tv_clock_1.setTextColor(getResources().getColor(R.color.blue_theme));
                                                   tv_clock_2.setTextColor(getResources().getColor(R.color.tv_cccccc));
                                                   tv_everyweek.setTextColor(getResources().getColor(R.color.tv_666666));
                                                   tv_everyday.setTextColor(Color.BLACK);
                                                   int startTime=valueArray.getJSONObject(0).getInteger(Constance.BeginTime);
                                                   int endTime=valueArray.getJSONObject(0).getInteger(Constance.EndTime);
                                                   tv_day_time.setText(startTime/3600+":"+(startTime-(startTime/3600*3600))/60+"-"+ endTime /3600+":"+(endTime -(endTime /3600*3600))/60);
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
    private void refreshUI() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                myListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_loop_day:
                intent=new Intent(this,LivingAlarmPlanDayTimeSetActivity.class);
                intent.putExtra("iotId",iotId);
                intent.putExtra(Constance.BeginTime,beginTime);
                intent.putExtra(Constance.EndTime,endTime);
                startActivity(intent);
                break;
            case R.id.rl_loop_week:
                intent = new Intent(this,LivingAlarmPlanWeekTimeSetActivity.class);
                intent.putExtra("iotId",iotId);
                startActivity(intent);
                break;
            case R.id.iv_day_switch:
                changeLoop();
                break;
            case R.id.iv_week_switch:
                changeLoop();
                break;

        }
    }

    private void changeLoop() {
        isDayLoopOpen = !isDayLoopOpen;

        if(!isDayLoopOpen){
            tv_clock_1.setTextColor(getResources().getColor(R.color.tv_cccccc));
            tv_clock_2.setTextColor(getResources().getColor(R.color.blue_theme));
            tv_everyweek.setTextColor(Color.BLACK);
            tv_everyday.setTextColor(getResources().getColor(R.color.tv_666666));
            iv_week_switch.setBackgroundResource(R.mipmap.set_btn_on);
            iv_day_switch.setBackgroundResource(R.mipmap.set_btn_off);
        }else {
            iv_week_switch.setBackgroundResource(R.mipmap.set_btn_off);
            iv_day_switch.setBackgroundResource(R.mipmap.set_btn_on);
            tv_clock_1.setTextColor(getResources().getColor(R.color.blue_theme));
            tv_clock_2.setTextColor(getResources().getColor(R.color.tv_cccccc));
            tv_everyweek.setTextColor(getResources().getColor(R.color.tv_666666));
            tv_everyday.setTextColor(Color.BLACK);
//                    int startTime=valueArray.getJSONObject(0).getInteger(Constance.BeginTime);
//                    int endTime=valueArray.getJSONObject(0).getInteger(Constance.EndTime);
//                    tv_day_time.setText(startTime/3600+":"+startTime/60+"-"+endTime/3600+":"+endTime/60);
        }
    }
}
