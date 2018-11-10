package com.ijourney.conbowcontrol.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.ijourney.conbowcontrol.R;
import com.ijourney.conbowcontrol.bean.FixedBean;
import com.ijourney.conbowcontrol.bean.OrderListBean;
import com.ijourney.conbowcontrol.bean.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatPresent {
    private Context mContext;
    private long sendTime = 0L;
    // 发送心跳包
    private Handler mHandler = new Handler();
    // 每隔2秒发送一次心跳包，检测连接没有断开
    private static final long HEART_BEAT_RATE = 10 * 1000;
    private IChatView mView;

    // 发送心跳包
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                String message = sendHerData("100", "100.png");
                mSocket.send(message);
                Log.i("TAG", "message:" + message);
                sendTime = System.currentTimeMillis();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE); //每隔一定的时间，对长连接进行一次心跳检测
        }
    };

    public ChatPresent(Context mContext, IChatView mView) {
        this.mContext = mContext;
        this.mView = mView;
    }

    private WebSocket mSocket;

    public void setListener() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Request request = new Request.Builder().url("ws://47.95.35.97:80").build();
        EchoWebSocketListener socketListener = new EchoWebSocketListener();
        // 刚进入界面，就开启心跳检测
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        mOkHttpClient.newWebSocket(request, socketListener);
        mOkHttpClient.dispatcher().executorService().shutdown();
        connectData();
    }


    private final class EchoWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mSocket = webSocket;
            output("连接成功！");

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            output("receive bytes:" + bytes.hex());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            output("服务器端发送来的信息：" + text);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            output("closed:" + reason);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            output("closing:" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            output("failure:" + t.getMessage());
        }
    }

    private void output(final String text) {
        Log.e("TAG", "text: " + text);
    }

    private String connectData() {
        String jsonHead = "";
        Map<String, Object> mapHead = new HashMap<>();
        mapHead.put("type", "regist");
        mapHead.put("server", "server2010701");
        mapHead.put("value", "");
        mapHead.put("order", new ArrayList<>());
        jsonHead = buildRequestParams(mapHead);
        Log.e("TAG", "sendData: " + jsonHead);
        if (mSocket != null) {
            mSocket.send(jsonHead);
        }

        return jsonHead;
    }

    public String sendMsgData(String position, String page) {
        String jsonHead = "";
        List<OrderListBean> listBeans = new ArrayList<>();
        OrderListBean bean = new OrderListBean();
        bean.setPage(position);
        bean.setShowImg(page);
        listBeans.add(bean);
        Map<String, Object> mapHead = new HashMap<>();
        mapHead.put("type", "order");
        mapHead.put("server", "server2010701");
        mapHead.put("value", "");
        mapHead.put("order", listBeans);
        jsonHead = buildRequestParams(mapHead);
        Log.e("TAG", "sendData: " + jsonHead);
        if (mSocket != null) {
            mSocket.send(jsonHead);
        }
        return jsonHead;
    }

    public String sendHerData(String position, String page) {
        String jsonHead = "";
        List<OrderListBean> listBeans = new ArrayList<>();
        OrderListBean bean = new OrderListBean();
        bean.setPage(position);
        bean.setShowImg(page);
        listBeans.add(bean);
        Map<String, Object> mapHead = new HashMap<>();
        mapHead.put("type", "order");
        mapHead.put("server", "server2010701");
        mapHead.put("value", "");
        mapHead.put("order", listBeans);
        jsonHead = buildRequestParams(mapHead);
        return jsonHead;
    }

    public static String buildRequestParams(Object params) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(params);
        return jsonStr;
    }


    public List<FixedBean> getFixedTop() {
        List<FixedBean> featuresBeans = new ArrayList<>();

        featuresBeans.add(new FixedBean("首页", getFixedName("fixed_home", ""), "20", "20.png", "fixed_home"));
        featuresBeans.add(new FixedBean("视频播放", getFixedName("fixed_video", ""), "21", "21.png", "fixed_video"));
//        featuresBeans.add(new FixedBean("视频定格", getFixedName("fixed_freeze", ""), "11", "11.png", "fixed_freeze"));
        featuresBeans.add(new FixedBean("缴费", getFixedName("fixed_payment", (mContext.getResources().getString(R.string.first_paragraph))), "1_1", "1_1.png", "fixed_payment"));
        featuresBeans.add(new FixedBean("报事", getFixedName("fixed_report", (mContext.getResources().getString(R.string.two_paragraph))), "1_2", "1_2.png", "fixed_report"));
        featuresBeans.add(new FixedBean("放行", getFixedName("fixed_release", (mContext.getResources().getString(R.string.three_paragraph))), "1_3", "1_3.png", "fixed_release"));
        featuresBeans.add(new FixedBean("呼叫", getFixedName("fixed_call", (mContext.getResources().getString(R.string.four_paragraph))), "1_4", "1_4.png", "fixed_call"));

        String msg = mContext.getResources().getString(R.string.first_paragraph) + mContext.getResources().getString(R.string.two_paragraph) + mContext.getResources().getString(R.string.three_paragraph) + mContext.getResources().getString(R.string.four_paragraph);
//        featuresBeans.add(new FixedBean("自动播放", getFixedName("fixed_auto", msg), "1_auto", "1_auto.png", "fixed_auto"));
        featuresBeans.add(new FixedBean("启动天启", getFixedName("fixed_start", ""), "2", "2.png", "fixed_start"));
        featuresBeans.add(new FixedBean("暂停", getFixedName("fixed_pause", ""), "", "", "fixed_pause"));
        return featuresBeans;
    }


    public String getFixedName(String tag, String initData) {
        String name = StringUtils.isEmpty(SharedPreferencesUtils.init(mContext).getString(tag)) ? initData
                : SharedPreferencesUtils.init(mContext).getString(tag);
        return name;
    }

}
