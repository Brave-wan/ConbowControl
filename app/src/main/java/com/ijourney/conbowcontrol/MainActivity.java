package com.ijourney.conbowcontrol;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ijourney.conbowcontrol.adapter.ExpandableListViewAdapter;
import com.ijourney.conbowcontrol.bean.FixFatherBean;
import com.ijourney.conbowcontrol.bean.FixedBean;
import com.ijourney.conbowcontrol.databinding.ActivityMainBinding;
import com.ijourney.conbowcontrol.old.OldChatActivity;
import com.ijourney.conbowcontrol.view.ChatPresent;
import com.ijourney.conbowcontrol.view.IChatView;
import com.ijourney.p2plib.connect.NearConnect;
import com.ijourney.p2plib.discovery.NearDiscovery;
import com.ijourney.p2plib.model.Host;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements View.OnClickListener, IChatView {

    private static final long DISCOVERABLE_TIMEOUT_MILLIS = 60000;
    private static final long DISCOVERY_TIMEOUT_MILLIS = 10000;
    private static final long DISCOVERABLE_PING_INTERVAL_MILLIS = 5000;
    public static final String MESSAGE_REQUEST_START_CHAT = "start_chat";
    public static final String MESSAGE_RESPONSE_DECLINE_REQUEST = "decline_request";
    public static final String MESSAGE_RESPONSE_ACCEPT_REQUEST = "accept_request";
    private NearDiscovery mNearDiscovery;
    private NearConnect mNearConnect;
    private ActivityMainBinding binding;
    private Snackbar mDiscoveryInProgressSnackbar;
    private ParticipantsAdapter mParticipantsAdapter;
    private boolean mDiscovering;
    List<FixedBean> featuresBeans = new ArrayList<>();
    BaseQuickAdapter<FixedBean, BaseViewHolder> adapter;
    ChatPresent chatPresent;

    private List<FixFatherBean> fixFatherBeanList = new ArrayList<>();

    private int selectGroupPosition=-1;
    private int selectChildPosition=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mNearDiscovery = new NearDiscovery.Builder()
                .setContext(this)
                .setDiscoverableTimeoutMillis(DISCOVERABLE_TIMEOUT_MILLIS)
                .setDiscoveryTimeoutMillis(DISCOVERY_TIMEOUT_MILLIS)
                .setDiscoverablePingIntervalMillis(DISCOVERABLE_PING_INTERVAL_MILLIS)
                .setDiscoveryListener(getNearDiscoveryListener(), Looper.getMainLooper())
                .build();
        binding.menuCheck.setOnClickListener(this);
        chatPresent = new ChatPresent(this, this);
        binding.startChattingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDiscovering) {
                    stopDiscovery();
                } else {
                    mNearDiscovery.makeDiscoverable(Build.BRAND);
                    startDiscovery();
                    if (!mNearConnect.isReceiving()) {
                        mNearConnect.startReceiving();
                    }
                }
            }
        });

        mNearConnect = new NearConnect.Builder()
                .fromDiscovery(mNearDiscovery)
                .setContext(this)
                .setListener(getNearConnectListener(), Looper.getMainLooper())
                .build();

        mParticipantsAdapter = new ParticipantsAdapter(new ParticipantsAdapter.Listener() {
            @Override
            public void sendChatRequest(Host host) {
                mNearConnect.send(MESSAGE_REQUEST_START_CHAT.getBytes(), host);
            }
        });

        binding.participantsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.participantsRv.setAdapter(mParticipantsAdapter);
        featuresBeans = chatPresent.getFixedTop();
        binding.btnReset.setOnClickListener(this);
        binding.btnConnect.setOnClickListener(this);

        initAdapter();
        binding.llBtnLayout.setVisibility( View.GONE);
        binding.llMenuLayout.setVisibility(View.GONE);
        binding.expandableListView.setVisibility(View.VISIBLE);

        initExpandAdapter();
//        chatPresent.initWebView(binding.mainWebView, this);
        chatPresent.setListener();
    }

    private void initExpandAdapter() {
        fixFatherBeanList = chatPresent.getFixFatherData();
        final ExpandableListViewAdapter exAdapter = new ExpandableListViewAdapter(this,fixFatherBeanList);

        binding.expandableListView.setAdapter(exAdapter);
        binding.expandableListView.expandGroup(0);
        binding.expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int count = binding.expandableListView.getExpandableListAdapter().getGroupCount();
                for(int j = 0; j < count; j++){
                    if(j != groupPosition){
                        binding.expandableListView.collapseGroup(j);
                    }
                }
            }
        });
//        binding.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                return false;
//            }
//        });
        binding.expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                if(selectChildPosition!=-1&&selectGroupPosition!=-1){

                    fixFatherBeanList.get(selectGroupPosition).getChildListBean().get(selectChildPosition).setCheck(false);
                }
                selectGroupPosition = groupPosition;
                selectChildPosition = childPosition;
                fixFatherBeanList.get(selectGroupPosition).getChildListBean().get(selectChildPosition).setCheck(true);
                FixedBean fixedBean = fixFatherBeanList.get(groupPosition).getChildListBean().get(childPosition);
                if (!StringUtils.isEmpty(fixedBean.getSocket_position()) && !StringUtils.isEmpty(fixedBean.getSocket_page())) {
                    chatPresent.sendMsgData(fixedBean.getSocket_position(), fixedBean.getSocket_page(), fixedBean.getType());

                }
                exAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void initAdapter() {
        binding.menuList.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new BaseQuickAdapter<FixedBean, BaseViewHolder>(R.layout.item_features, featuresBeans) {
            @Override
            protected void convert(BaseViewHolder helper, FixedBean item) {
                TextView btn_name = helper.itemView.findViewById(R.id.btn_name);
                if (item.isCheck()) {
                    btn_name.setBackgroundColor(getResources().getColor(R.color.spots_dialog_color));
                    btn_name.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btn_name.setBackgroundColor(getResources().getColor(R.color.black));
                    btn_name.setTextColor(getResources().getColor(R.color.white));
                }
                btn_name.setText(item.getName());
                ViewGroup.LayoutParams layoutParams = btn_name.getLayoutParams();
                layoutParams.height = item.getVisibility()==1? 80:40;
                btn_name.setLayoutParams(layoutParams);

                btn_name.setVisibility(item.getVisibility() == 1 ? View.VISIBLE : View.INVISIBLE);
            }
        };
        binding.menuList.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FixedBean fixedBean = (FixedBean) adapter.getItem(position);

                for (FixedBean features : featuresBeans) {
                    features.setCheck(false);
                }
                featuresBeans.get(position).setCheck(true);

                if (!StringUtils.isEmpty(fixedBean.getSocket_position()) && !StringUtils.isEmpty(fixedBean.getSocket_page())) {
                    chatPresent.sendMsgData(fixedBean.getSocket_position(), fixedBean.getSocket_page(), fixedBean.getType());

                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @NonNull
    private NearDiscovery.Listener getNearDiscoveryListener() {
        return new NearDiscovery.Listener() {
            @Override
            public void onPeersUpdate(Set<Host> hosts) {
                mParticipantsAdapter.setData(hosts);
            }

            @Override
            public void onDiscoveryTimeout() {
                Snackbar.make(binding.getRoot(), "No other conbow found", Snackbar.LENGTH_LONG).show();
                binding.discoveryPb.setVisibility(View.GONE);
                mDiscovering = false;
                binding.startChattingBtn.setText("Start Searching Conbow");
            }

            @Override
            public void onDiscoveryFailure(Throwable e) {
                Snackbar.make(binding.getRoot(),
                        "Something went wrong while searching for conbow",
                        Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onDiscoverableTimeout() {
                Toast.makeText(MainActivity.this, "You're not discoverable anymore", Toast.LENGTH_LONG).show();
            }
        };
    }

    @NonNull
    private NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(byte[] bytes, final Host sender) {
                if (bytes != null) {
                    switch (new String(bytes)) {
                        case MESSAGE_RESPONSE_DECLINE_REQUEST:
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(sender.getName() + " is busy at the moment.")
                                    .setNeutralButton("Ok", null).create().show();
                            break;
                        case MESSAGE_RESPONSE_ACCEPT_REQUEST:
                            stopNearServicesAndStartChat(sender);
                            break;
                    }
                }
            }

            @Override
            public void onSendComplete(long jobId) {
                ToastUtils.showLong("onSendComplete" + jobId);

            }

            @Override
            public void onSendFailure(Throwable e, long jobId) {
                ToastUtils.showLong("onSendFailure" + e.getMessage());
            }

            @Override
            public void onStartListenFailure(Throwable e) {
                ToastUtils.showLong("onStartListenFailure" + e.getMessage());
            }
        };
    }

    private void stopNearServicesAndStartChat(Host sender) {
        mNearConnect.stopReceiving(true);
        stopDiscovery();
        OldChatActivity.start(MainActivity.this, sender);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNearDiscovery.stopDiscovery();
        mNearConnect.stopReceiving(true);
    }

    private void stopDiscovery() {
        mNearDiscovery.stopDiscovery();
        mNearDiscovery.makeNonDiscoverable();
        mDiscovering = false;
        mDiscoveryInProgressSnackbar.dismiss();
        binding.participantsRv.setVisibility(View.GONE);
        binding.discoveryPb.setVisibility(View.GONE);
        binding.startChattingBtn.setText("Start Searching Conbow");
    }

    private void startDiscovery() {
        mDiscovering = true;
        mNearDiscovery.startDiscovery();
        mDiscoveryInProgressSnackbar = Snackbar.make(binding.getRoot(), "Looking for conbow...",
                Snackbar.LENGTH_INDEFINITE);
        mDiscoveryInProgressSnackbar.show();
        mParticipantsAdapter.setData(new ArraySet<Host>());
        binding.participantsRv.setVisibility(View.VISIBLE);
        binding.discoveryPb.setVisibility(View.VISIBLE);
        binding.startChattingBtn.setText("Stop Searching Conbow");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mNearConnect.startReceiving();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_check:
                binding.llBtnLayout.setVisibility( View.GONE);
                binding.llMenuLayout.setVisibility(View.VISIBLE);
                if (binding.menuCheck.getText().equals("切到手动控制")) {
                    binding.menuCheck.setText("切到康宝控制");
                    chatPresent.initWebView(binding.mainWebView, this);
                    chatPresent.setListener();
                } else {
                    binding.menuCheck.setText("切到手动控制");
                }

                break;
            case R.id.btn_Reset:
                binding.mainWebView.reload();
                break;
            case R.id.btn_connect:
                chatPresent.setListener();
                break;
        }

    }

    @Override
    public void showMsg() {

    }

    @Override
    public void sendChatMsg(String s, String s1, String type, String s2) {

    }
}
