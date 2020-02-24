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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManage;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.Device;
import com.bean.RoomBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.net.ApiClient;
import com.util.Constance;
import com.util.LogUtils;
import com.util.json.JSONArray;
import com.util.json.JSONObject;
import com.view.FontIconView;
import com.view.MyToast;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RoomManageActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView lv_room;
    private TextView tv_edit;
    private TextView tv_add_room;
    private QuickAdapter<RoomBean> adapter;
    private List<RoomBean> roomBeans;
    private ScrollView scrollview;
    private BookShelfTouchHelper touchHelper;
    private DragRecyclerAdapter myDragAdapter;
    private int[] sorts;
    private boolean isEdit;
    private String identity;

    @Override
    protected void InitDataView() {
//        getRoomList();
    }

    private void getRoomList() {
        IoTCredentialManage ioTCredentialManage= IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
        ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
            @Override
            public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                identity = ioTCredentialData.identity;
                if(!TextUtils.isEmpty(identity)){
                    ApiClient.getRoomList(identity, new Callback<String>() {
                        @Override
                        public String parseNetworkResponse(Response response, int id) throws Exception {
                            return response.body().string();
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public String onResponse(String response, int id) {
                            LogUtils.logE("room",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray array=jsonObject.getJSONArray(Constance.data);
                            if(array.length()>0){
                                roomBeans = new Gson().fromJson(array.toString(),new TypeToken<List<RoomBean>>(){}.getType());
                                if(roomBeans.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            myDragAdapter = new DragRecyclerAdapter(RoomManageActivity.this,roomBeans);
                                            lv_room.setAdapter(myDragAdapter);
                                            myDragAdapter.setListener(new OnRoomDragFlagClickListener() {
                                                @Override
                                                public void onRoomDragFlagClick(RecyclerView.ViewHolder holder) {
                                                    touchHelper.startDrag(holder);
                                                }
                                            });
                                            sorts = new int[roomBeans.size()];
                                            for(int i=0;i<roomBeans.size();i++){
                                                sorts[i]=(i);
                                            }
//                                            adapter.replaceAll(roomBeans);
                                            int height=UIUtils.dip2PX(55)*roomBeans.size();
                                            LogUtils.logE("lvroom",height+"");
                                            int max=UIUtils.getScreenHeight(RoomManageActivity.this)-UIUtils.dip2PX(135);
                                            if(height>max){
                                                lv_room.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,max));
                                            }
//                                            if(UIUtils.dip2PX(UIUtils.initListViewHeight(lv_room))>max){
//                                                ViewGroup.LayoutParams layoutParams=lv_room.getLayoutParams();
//                                                layoutParams.height=max;
//                                                lv_room.setLayoutParams(layoutParams);
//                                                lv_room.requestLayout();
////                                                scrollview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,max));
//                                            }else {
//                                                int height=UIUtils.dip2PX(UIUtils.initListViewHeight(lv_room));
//                                                ViewGroup.LayoutParams layoutParams=lv_room.getLayoutParams();
//                                                layoutParams.height=height;
//                                                lv_room.setLayoutParams(layoutParams);
//                                                lv_room.requestLayout();
////                                                scrollview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
//                                            }
                                        }
                                    });
                                }
                            }
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

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_home_manage);
        lv_room = findViewById(R.id.lv_room);
        tv_edit = findViewById(R.id.tv_edit);
        tv_add_room = findViewById(R.id.tv_add_room);
        scrollview = findViewById(R.id.scrollview);
        tv_edit.setOnClickListener(this);
        tv_add_room.setOnClickListener(this);
        adapter = new QuickAdapter<RoomBean>(this,R.layout.item_room_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, RoomBean item) {
            helper.setText(R.id.tv_name,item.getName());
            }
        };
        // 定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        // 设置布局管理器
        lv_room.setLayoutManager(manager);
        lv_room.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                return false;
            }
        });
        touchHelper = new BookShelfTouchHelper(new TouchCallback(new OnItemTouchCallbackListener() {
            @Override
            public void onSwiped(int position) {
                //处理划动删除操作
                if(roomBeans != null && roomBeans.size() >= 0 && position < roomBeans.size()) {
                    roomBeans.remove(position);
                    myDragAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                //处理拖拽事件
                if(roomBeans == null || roomBeans.size() == 0) {
                    return false;
                }
                if(srcPosition >= 0 && srcPosition < roomBeans.size() && targetPosition >= 0 && targetPosition < roomBeans.size()) {

                    int temp=sorts[srcPosition];
                    sorts[srcPosition]=sorts[targetPosition];
                    sorts[targetPosition]=temp;

                    //交换数据源两个数据的位置
                    Collections.swap(roomBeans,srcPosition,targetPosition);
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
        touchHelper.attachToRecyclerView(lv_room);

//        lv_room.setAdapter(adapter);

    }

    private void updateRoom(String identity,JSONArray array) {
        ApiClient.editRoom(identity,array, new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                return response.body().string();
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public String onResponse(String response, int id) {
                LogUtils.logE("msg",response+"");
                onResume();
                return null;
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        roomBeans=new ArrayList<>();
        getRoomList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_edit:
                isEdit=!isEdit;
                if(isEdit){
                    tv_edit.setText(R.string.str_save);
                }else {
                    tv_edit.setText(R.string.str_edit);
                    JSONArray array=new JSONArray() ;

                    for (int i=0;i<sorts.length;i++){
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.add("id",roomBeans.get(i).getId());
                        jsonObject.add("sort",i);
                        array.add(jsonObject);
                        }
                            updateRoom(identity,array);
                }
                myDragAdapter.notifyDataSetChanged();
//                LogUtils.logE("update",roomBeans.get(srcPosition).getName()+","+targetPosition);


                break;
            case R.id.tv_add_room:
                startActivity(new Intent(this,RoomAddActivity.class));
                break;

        }
    }
     class DragRecyclerAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private List<RoomBean> mEntityList;
         private OnItemClickListener mOnItemClickListener;

         public DragRecyclerAdapter (Context context, List<RoomBean> entityList){
            this.mContext = context;
            this.mEntityList = entityList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_sort_room, parent, false);
            return new DemoViewHolder(view);
        }
         public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
             this.mOnItemClickListener = onItemClickListener;
         }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RoomBean entity = mEntityList.get(position);
            DemoViewHolder mHolder= (DemoViewHolder) holder;
            mHolder.mText.setText(entity.getName());
            mHolder.bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(RoomManageActivity.this,RoomDeviceSettingActivity.class);
                    intent.putExtra(Constance.is_edit,true);
                    intent.putExtra(Constance.id,entity.getId());
                    intent.putExtra(Constance.name,entity.getName());
                    if(entity.getDevice()!=null&&entity.getDevice().size()>0){
                        intent.putExtra(Constance.devices,new Gson().toJson(entity.getDevice(),new TypeToken<List<Device>>(){}.getType()));
                    }
                    startActivity(intent);
                }
            });
            if(isEdit){
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
                    if(isEdit){
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
                    Dialog dialog=UIUtils.showSingleWordDialogWithLayout(RoomManageActivity.this, R.layout.dialog_delete_room);
                    TextView tv_num= (TextView) dialog.findViewById(R.id.tv_title);
                    tv_num.setText(getResources().getString(R.string.str_ensure_delete_room)+"\""+entity.getName()+"\"?");
                    TextView btn = (TextView) dialog.findViewById(R.id.tv_ensure);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            ApiClient.deleteRoom(identity,entity.getId(), new Callback<String>() {
                                @Override
                                public String parseNetworkResponse(Response response, int id) throws Exception {
                                    return response.body().string();
                                }

                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public String onResponse(String response, int id) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyToast.show(RoomManageActivity.this,getString(R.string.str_delete_success));
                                            tv_edit.performClick();
                                        }
                                    });
                                    roomBeans=new ArrayList<>();
                                    getRoomList();
                                    return null;
                                }
                            });
                        }
                    });
                    TextView cancel= (TextView) dialog.findViewById(R.id.tv_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
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
}
