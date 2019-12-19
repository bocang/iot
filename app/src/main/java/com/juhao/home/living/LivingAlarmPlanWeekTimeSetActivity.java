package com.juhao.home.living;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.bean.DayOfWeekBean;
import com.bigkoo.pickerview.TimePickerView;
import com.juhao.home.R;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.util.Constance;
import com.util.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivingAlarmPlanWeekTimeSetActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_begin_time;
    private TextView tv_end_time;
    private TimePickerView timePickerView;
    private String iotId;
    private QuickAdapter<DayOfWeekBean> adapter;
    private ListView lv_day;
    private List<DayOfWeekBean> dayOfWeekBeans;
    private String[] week;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_living_setting_alarm_week_timeset);
        lv_day = findViewById(R.id.lv_day);
        adapter = new QuickAdapter<DayOfWeekBean>(this,R.layout.item_day_of_week) {
            @Override
            protected void convert(BaseAdapterHelper helper, DayOfWeekBean item) {
                helper.setText(R.id.tv_day_of_week,item.getDayOfweek());
                int beginTime=item.getBeginTime();
                int endTime=item.getEndTime();
                helper.setText(R.id.tv_begin_time,beginTime/3600+":"+(beginTime-(beginTime/3600)*3600)/60);
                helper.setText(R.id.tv_end_time,endTime/3600+":"+(endTime-(endTime/3600)*3600)/60);
                if(item.isOpen()){
                    helper.setBackgroundRes(R.id.iv_isopen,R.mipmap.set_btn_on);
                }else {
                    helper.setBackgroundRes(R.id.iv_isopen,R.mipmap.set_btn_off);
                }
                helper.setOnClickListener(R.id.iv_isopen, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setOpen(!item.isOpen());
                        adapter.notifyDataSetChanged();
                    }
                });
                helper.setOnClickListener(R.id.rl_begin_time, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeSelectDialog(helper.getPosition(),true);
                    }
                });
                helper.setOnClickListener(R.id.rl_end_time, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeSelectDialog(helper.getPosition(),false);
                    }
                });

            }
        };
        lv_day.setAdapter(adapter);
        dayOfWeekBeans = new ArrayList<>();
        week = getResources().getStringArray(R.array.DayOfWeek);
        for(int i=0;i<7;i++){
            DayOfWeekBean temp=new DayOfWeekBean();
            temp.setBeginTime(0);
            temp.setEndTime(86399);
            temp.setOpen(false);
            temp.setDayOfweek(week[i]);
            dayOfWeekBeans.add(temp);
        }
        IPCManager.getInstance()
                .getDevice(iotId)
                .getProperties(new IPanelCallback() {
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
                                                                                                if(valueArray!=null&&valueArray.size()>0){
                                                                                                    for(int i=0;i<valueArray.size();i++){
                                                                                                        int beginTime=valueArray.getJSONObject(i).getInteger(Constance.BeginTime);
                                                                                                        int endTime=valueArray.getJSONObject(i).getInteger(Constance.EndTime);
                                                                                                        int day=valueArray.getJSONObject(i).getInteger(Constance.DayOfWeek);
                                                                                                        DayOfWeekBean temp=new DayOfWeekBean() ;
                                                                                                        temp.setEndTime(endTime);
                                                                                                        temp.setDayOfweek(week[day]);
                                                                                                        temp.setBeginTime(beginTime);
                                                                                                        temp.setOpen(true);
                                                                                                        if(day<dayOfWeekBeans.size()){
                                                                                                            dayOfWeekBeans.set(day,temp);
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                runOnUiThread(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        adapter.replaceAll(dayOfWeekBeans);
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
            case R.id.rl_begin_time:
//                showTimeSelectDialog(tv_begin_time);
                break;
            case R.id.rl_end_time:
//                showTimeSelectDialog(tv_end_time);
                break;

        }
    }
    private void showTimeSelectDialog(int position,boolean isStart){
        timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                    String endTime= date.getHours()+":"+date.getMinutes();
                    if(isStart){
                        dayOfWeekBeans.get(position).setBeginTime(date.getHours()*3600+date.getMinutes()*60);
                    }else {
                        dayOfWeekBeans.get(position).setEndTime(date.getHours()*3600+date.getMinutes()*60);
                    }
                    adapter.replaceAll(dayOfWeekBeans);
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

        String key="AlarmNotifyPlan";
        Map<String,Object>param=new HashMap<>();
        com.alibaba.fastjson.JSONArray array=new com.alibaba.fastjson.JSONArray();
        for(int i=0;i<7;i++){
            if(dayOfWeekBeans.get(i).isOpen()){
                com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
//                String beginTime=dayOfWeekBeans.get(i).getBeginTime()
//                String endTime=tv_end_time.getText().toString();
//                int beginSecond=Integer.parseInt(beginTime.split(":")[0])*60*60+Integer.parseInt(beginTime.split(":")[1])*60;
//                int endSecond=Integer.parseInt(endTime.split(":")[0])*60*60+Integer.parseInt(endTime.split(":")[1])*60;
//                LogUtils.logE("begin_end",beginSecond+","+endSecond);
                jsonObject.put("DayOfWeek",i);
                jsonObject.put("BeginTime",dayOfWeekBeans.get(i).getBeginTime());
                jsonObject.put("EndTime",dayOfWeekBeans.get(i).getEndTime());
                array.add(jsonObject);
            }
        }
        param.put(key,array);
        SettingsCtrl.getInstance().updateSettings(iotId, param);
        finish();
    }
}
