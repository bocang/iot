package com.juhao.home.room;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.bean.AccountDevDTO;
import com.bean.Device;
import com.bean.RoomBean;
import com.facebook.react.uimanager.events.TouchesHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.net.ApiClient;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.FontIconView;
import com.view.MyToast;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class RoomDeviceSettingActivity extends BaseActivity {

    private int id;
    private String name;
    private TextView tv_name;
    private RecyclerView rcv_has_add;
    private RecyclerView rcv_not_add;
    private BookShelfTouchHelper touchHelper;
    private int page;
    private int pageSize;
    private DragRecyclerAdapter myDragAdapter;
    private DragRecyclerAdapter2 myDragAdapter2;
    private int[] sorts;
    private String devices;
    private List<Device> devicesList;
    private BookShelfTouchHelper touchHelper2;

    @Override
    protected void initData() {
        id = getIntent().getIntExtra(Constance.id,0);
        name = getIntent().getStringExtra(Constance.name);
        devices = getIntent().getStringExtra(Constance.devices);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_room_dev_setting);
        findViewById(R.id.rl_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RoomDeviceSettingActivity.this,RoomAddActivity.class);
                intent.putExtra(Constance.is_edit,true);
                intent.putExtra(Constance.id,id);
                intent.putExtra(Constance.name,name);
                startActivity(intent);
            }
        });
        tv_name = findViewById(R.id.tv_name);
        rcv_has_add = findViewById(R.id.rcv_has_add);
        rcv_not_add = findViewById(R.id.rcv_not_add);
        // 定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        rcv_not_add.setLayoutManager(manager);
        rcv_has_add.setLayoutManager(manager2);

        rcv_not_add.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                return false;
            }
        });
        rcv_has_add.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                return false;
            }
        });
        touchHelper = new BookShelfTouchHelper(new TouchCallback(new OnItemTouchCallbackListener() {
            @Override
            public void onSwiped(int position) {
                //处理划动删除操作
                if(accountDevDTOS != null && accountDevDTOS.size() >= 0 && position < accountDevDTOS.size()) {
                    accountDevDTOS.remove(position);
                    myDragAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                //处理拖拽事件
                if(accountDevDTOS == null || accountDevDTOS.size() == 0) {
                    return false;
                }
                if(srcPosition >= 0 && srcPosition < accountDevDTOS.size() && targetPosition >= 0 && targetPosition < accountDevDTOS.size()) {

                    int temp=sorts[srcPosition];
                    sorts[srcPosition]=sorts[targetPosition];
                    sorts[targetPosition]=temp;

                    //交换数据源两个数据的位置
                    Collections.swap(accountDevDTOS,srcPosition,targetPosition);
                    //更新视图
                    myDragAdapter.notifyItemMoved(srcPosition,targetPosition);

                    //消费事件
                    return true;
                } else {
                    return false;
                }
            }
        }));
        touchHelper2 = new BookShelfTouchHelper(new TouchCallback(new OnItemTouchCallbackListener() {
            @Override
            public void onSwiped(int position) {
                //处理划动删除操作
                if(devicesList != null && devicesList.size() >= 0 && position < devicesList.size()) {
                    devicesList.remove(position);
                    myDragAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                //处理拖拽事件
                if(devicesList == null || devicesList.size() == 0) {
                    return false;
                }
                if(srcPosition >= 0 && srcPosition < devicesList.size() && targetPosition >= 0 && targetPosition < devicesList.size()) {

                    int temp=sorts[srcPosition];
                    sorts[srcPosition]=sorts[targetPosition];
                    sorts[targetPosition]=temp;

                    //交换数据源两个数据的位置
                    Collections.swap(devicesList,srcPosition,targetPosition);
                    //更新视图
                    myDragAdapter.notifyItemMoved(srcPosition,targetPosition);

                    //消费事件
                    return true;
                } else {
                    return false;
                }
            }
        }));
        touchHelper.setEnableDrag(true);
        touchHelper.setEnableSwipe(false);
        touchHelper.attachToRecyclerView(rcv_not_add);

        touchHelper2.setEnableDrag(true);
        touchHelper2.setEnableSwipe(false);
        touchHelper2.attachToRecyclerView(rcv_has_add);


    }

    @Override
    protected void initController() {

    }
    List<AccountDevDTO> accountDevDTOS=new ArrayList<>();
    @Override
    protected void InitDataView() {
        tv_name.setText(name);
        if(devices!=null&&devices.length()>0){
            devicesList = new Gson().fromJson(devices,new TypeToken<List<Device>>(){}.getType());
            myDragAdapter2=new DragRecyclerAdapter2(this,devicesList);
            rcv_has_add.setAdapter(myDragAdapter2);
        }
        page = 1;
        pageSize = 40;
        ApiClientForIot.getDevList(page, pageSize, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                Object data = response.getData();
                if (null != data) {
                    if (data instanceof JSONObject) {
                        JSONObject result = (JSONObject) data;
                        JSONArray listData = null;
                        try {
                            listData = result.getJSONArray(Constance.data);
//                            total = result.getInt(Constance.total);
//                            pageNo = result.getInt(Constance.pageNo);
//                            if (total <= pageNo * 20 + 1) {
//                                isBottom = true;
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        mDeviceList = parseDeviceListFromSever((JSONArray) data);
                        List<AccountDevDTO> temp = new Gson().fromJson(((JSONArray) listData).toString(), new TypeToken<List<AccountDevDTO>>() {
                        }.getType());
                        accountDevDTOS.addAll(temp);
                        if (accountDevDTOS == null || accountDevDTOS.size() == 0) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myDragAdapter = new DragRecyclerAdapter(RoomDeviceSettingActivity.this,accountDevDTOS);
                                rcv_not_add.setAdapter(myDragAdapter);
                                myDragAdapter.notifyDataSetChanged();
                                myDragAdapter.setListener(new OnRoomDragFlagClickListener() {
                                    @Override
                                    public void onRoomDragFlagClick(RecyclerView.ViewHolder holder) {
                                        touchHelper.startDrag(holder);
                                    }
                                });
                                int height=UIUtils.dip2PX(55)*accountDevDTOS.size();
                                LogUtils.logE("lvroom",height+"");
                                    rcv_not_add.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));

                            }
                        });

                        sorts = new int[accountDevDTOS.size()];
                        for(int i=0;i<accountDevDTOS.size();i++){
                            sorts[i]=(i);
                        }
                        

                    }
                }

            }
        });

    }

    public class BookShelfTouchHelper extends ItemTouchHelper {


        private TouchCallback callback;

        public BookShelfTouchHelper(TouchCallback callback) {
            super(callback);
            this.callback = callback;
        }

        public void setEnableDrag(boolean enableDrag) {
            callback.setEnableDrag(enableDrag);
        }

        public void setEnableSwipe(boolean enableSwipe) {
            callback.setEnableSwipe(enableSwipe);
        }
    }
    public interface OnRoomDragFlagClickListener {
        void onRoomDragFlagClick(RecyclerView.ViewHolder holder);
    }
    //回调接口
    public interface OnItemClickListener {
        void onItemClick(View v, ContactsContract.CommonDataKinds.Note note, int position);
    }
    public class TouchCallback extends ItemTouchHelper.Callback {

        private boolean isEnableSwipe;//允许滑动
        private boolean isEnableDrag;//允许拖动
        private OnItemTouchCallbackListener callbackListener;//回调接口

        public TouchCallback(OnItemTouchCallbackListener callbackListener) {
            this.callbackListener = callbackListener;
        }

        /**
         * 滑动或者拖拽的方向，上下左右
         * @param recyclerView 目标RecyclerView
         * @param viewHolder 目标ViewHolder
         * @return 方向
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {// GridLayoutManager
                // flag如果值是0，相当于这个功能被关闭
                int dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlag = 0;
                return makeMovementFlags(dragFlag, swipeFlag);
            } else if (layoutManager instanceof LinearLayoutManager) {// linearLayoutManager
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int orientation = linearLayoutManager.getOrientation();

                int dragFlag = 0;
                int swipeFlag = 0;

                if (orientation == LinearLayoutManager.HORIZONTAL) {//横向布局
                    swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else if (orientation == LinearLayoutManager.VERTICAL) {//纵向布局
                    dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                }
                return makeMovementFlags(dragFlag, swipeFlag);
            }
            return 0;
        }

        /**
         * 拖拽item移动时产生回调
         * @param recyclerView 目标RecyclerView
         * @param viewHolder 拖拽的item对应的viewHolder
         * @param target 拖拽目的地的ViewHOlder
         * @return 是否消费拖拽事件
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if(this.callbackListener != null) {
                this.callbackListener.onMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            }
            return false;
        }

        /**
         * 滑动删除时回调
         * @param viewHolder 当前操作的Item对应的viewHolder
         * @param direction 方向
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if(this.callbackListener != null) {
                this.callbackListener.onSwiped(viewHolder.getAdapterPosition());
            }
        }

        /**
         * 是否可以长按拖拽
         * @return
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        /**
         * 是否可以滑动删除
         */
        @Override
        public boolean isItemViewSwipeEnabled() {
            return isEnableSwipe;
        }

        public void setEnableDrag(boolean enableDrag) {
            this.isEnableDrag = enableDrag;
        }

        public void setEnableSwipe(boolean enableSwipe) {
            this.isEnableSwipe = enableSwipe;
        }
    }
    public interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除时回调
         */
        void onSwiped(int adapterPosition);

        /**
         * 当两个Item位置互换的时候被回调(拖拽)
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);
    }
    class DragRecyclerAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private List<AccountDevDTO> mEntityList;
        private OnItemClickListener mOnItemClickListener;

        public DragRecyclerAdapter (Context context, List<AccountDevDTO> entityList){
            this.mContext = context;
            this.mEntityList = entityList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_room_device_not_add, parent, false);
            return new DemoViewHolder(view);
        }
        public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
            this.mOnItemClickListener = onItemClickListener;
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            AccountDevDTO entity = mEntityList.get(position);
            DemoViewHolder mHolder= (DemoViewHolder) holder;
            mHolder.mText.setText(entity.getProductName());
            if(true){
                mHolder.fiv_delete.setVisibility(View.VISIBLE);
                mHolder.fiv_sort.setVisibility(View.VISIBLE);
                mHolder.iv_arrow.setVisibility(View.GONE);
            }else {
                mHolder.fiv_delete.setVisibility(View.GONE);
                mHolder.fiv_sort.setVisibility(View.GONE);
                mHolder.iv_arrow.setVisibility(View.VISIBLE);
            }
            mHolder.fiv_sort.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(true){
                        if (listener != null) {
                            listener.onRoomDragFlagClick(holder);
                        }
                    }
                    return false;
                }
            });
            mHolder.fiv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if(devicesList==null||devicesList.size()==0){
                    devicesList=new ArrayList<>();
                }
                    Device temp=new Device();
                    temp.setIot_id(entity.getIotId());
                    temp.setSort(devicesList.size());
                    devicesList.add(temp);
                    accountDevDTOS.remove(position);
                    myDragAdapter.notifyDataSetChanged();
                    updateRoom();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEntityList.size();
        }
        private class DemoViewHolder extends RecyclerView.ViewHolder{

            private TextView mText;
            private final FontIconView fiv_delete;
            private final ImageView iv_arrow;
            private final FontIconView fiv_sort;
            View bg;

            public DemoViewHolder(View itemView) {
                super(itemView);
                mText = (TextView) itemView.findViewById(R.id.tv_name);
                fiv_delete = itemView.findViewById(R.id.tv_delete);
                iv_arrow = itemView.findViewById(R.id.iv_arrow);
                fiv_sort = itemView.findViewById(R.id.iv_sort);
                bg=itemView.findViewById(R.id.bg);
            }
        }


        //私有属性
        private OnItemClickListener onItemClickListener = null;

        //setter方法
        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
        public OnRoomDragFlagClickListener listener;

        public void setListener(OnRoomDragFlagClickListener listener) {
            this.listener = listener;
        }
    }

    private void updateRoom() {
        JSONArray array=new JSONArray();
        for(int i=0;i<devicesList.size();i++){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("iot_id",devicesList.get(i).getIot_id());
                jsonObject.put("sort",devicesList.get(i).getSort());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(jsonObject);
        }
        ApiClient.saveDevice(array,id, new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                return response.body().string();
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public String onResponse(String response, int id) {
                refresh();
                return null;
            }
        });
    }

    private void refresh() {
            if(myDragAdapter2!=null){
                myDragAdapter2.notifyDataSetChanged();
            }
    }

    class DragRecyclerAdapter2 extends RecyclerView.Adapter {

        private Context mContext;
        private List<Device> mEntityList;
        private OnItemClickListener mOnItemClickListener;

        public DragRecyclerAdapter2 (Context context, List<Device> entityList){
            this.mContext = context;
            this.mEntityList = entityList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_room_device_has_add, parent, false);
            return new DemoViewHolder(view);
        }
        public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
            this.mOnItemClickListener = onItemClickListener;
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Device entity = mEntityList.get(position);
            DemoViewHolder mHolder= (DemoViewHolder) holder;
            mHolder.mText.setText(entity.getDevice_name());
            String name=entity.getDevice_name();
            if(TextUtils.isEmpty(name)){
                Map<String,Object> map=new HashMap<>();
                map.put("iotId",entity.getIot_id());
                ApiClientForIot.getIotClient("/thing/detailInfo/queryProductInfo", "1.1.3", map, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        try {
                            JSONObject jsonObject=new JSONObject(String.valueOf(ioTResponse.getData()));
                            if(jsonObject!=null){
                                LogUtils.logE("info.get",jsonObject.toString());
                                String name=jsonObject.getString(Constance.name);
                                mEntityList.get(position).setDevice_name(name);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    myDragAdapter2.notifyDataSetChanged();

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if(true){
                mHolder.fiv_delete.setVisibility(View.VISIBLE);
                mHolder.fiv_sort.setVisibility(View.VISIBLE);
                mHolder.iv_arrow.setVisibility(View.GONE);
            }else {
                mHolder.fiv_delete.setVisibility(View.GONE);
                mHolder.fiv_sort.setVisibility(View.GONE);
                mHolder.iv_arrow.setVisibility(View.VISIBLE);
            }
            mHolder.fiv_sort.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(true){
                        if (listener != null) {
                            listener.onRoomDragFlagClick(holder);
                        }
                    }
                    return false;
                }
            });
            mHolder.fiv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mEntityList.size();
        }
        private class DemoViewHolder extends RecyclerView.ViewHolder{

            private TextView mText;
            private final FontIconView fiv_delete;
            private final ImageView iv_arrow;
            private final FontIconView fiv_sort;
            View bg;

            public DemoViewHolder(View itemView) {
                super(itemView);
                mText = (TextView) itemView.findViewById(R.id.tv_name);
                fiv_delete = itemView.findViewById(R.id.tv_delete);
                iv_arrow = itemView.findViewById(R.id.iv_arrow);
                fiv_sort = itemView.findViewById(R.id.iv_sort);
                bg=itemView.findViewById(R.id.bg);
            }
        }


        //私有属性
        private OnItemClickListener onItemClickListener = null;

        //setter方法
        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
        public OnRoomDragFlagClickListener listener;

        public void setListener(OnRoomDragFlagClickListener listener) {
            this.listener = listener;
        }
    }

}
