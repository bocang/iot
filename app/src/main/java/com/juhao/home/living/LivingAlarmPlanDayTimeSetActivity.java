package com.juhao.home.living;

import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.bigkoo.pickerview.TimePickerView;
import com.juhao.home.R;
import com.util.Constance;
import com.util.LogUtils;
import com.util.json.JSONArray;
import com.util.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LivingAlarmPlanDayTimeSetActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_begin_time;
    private TextView tv_end_time;
    private TimePickerView timePickerView;
    private String iotId;
    private int beginTime;
    private int endTime;

    @Override
    protected void InitDataView() {
        tv_begin_time.setText(beginTime/3600+":"+(beginTime-(beginTime/3600*3600))/60);
        tv_end_time.setText( endTime /3600+":"+(endTime -(endTime /3600*3600))/60);
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
    setContentView(R.layout.activity_living_setting_alarm_day_timeset);
    findViewById(R.id.rl_begin_time).setOnClickListener(this);
    findViewById(R.id.rl_end_time).setOnClickListener(this);
        tv_begin_time = findViewById(R.id.tv_begin_time);
        tv_end_time = findViewById(R.id.tv_end_time);

    }

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
        beginTime = getIntent().getIntExtra(Constance.BeginTime,0);
        endTime = getIntent().getIntExtra(Constance.EndTime,0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_begin_time:
                showTimeSelectDialog(tv_begin_time);
                break;
            case R.id.rl_end_time:
                showTimeSelectDialog(tv_end_time);
                break;

        }
    }
    private void showTimeSelectDialog(TextView textView){
        timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                    String endTime= date.getHours()+":"+date.getMinutes();
                    textView.setText(endTime);
            }
        })
                .setType(new boolean[]{false,false,false,true,true,false})
                .build();
        timePickerView.setDate(Calendar.getInstance());
        timePickerView.show();
    }

    @Override
    public void save(View v) {
        super.save(v);
        String beginTime=tv_begin_time.getText().toString();
        String endTime=tv_end_time.getText().toString();
        int beginSecond=Integer.parseInt(beginTime.split(":")[0])*60*60+Integer.parseInt(beginTime.split(":")[1])*60;
        int endSecond=Integer.parseInt(endTime.split(":")[0])*60*60+Integer.parseInt(endTime.split(":")[1])*60;
        LogUtils.logE("begin_end",beginSecond+","+endSecond);
        String key="AlarmNotifyPlan";
        Map<String,Object>param=new HashMap<>();
        com.alibaba.fastjson.JSONArray array=new com.alibaba.fastjson.JSONArray();
        for(int i=0;i<7;i++){
            com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
            jsonObject.put("DayOfWeek",i);
            jsonObject.put("BeginTime",beginSecond);
            jsonObject.put("EndTime",endSecond);
            array.add(jsonObject);
        }
        param.put(key,array);
        SettingsCtrl.getInstance().updateSettings(iotId, param);
        finish();
    }
}
