package com.ijourney.conbowcontrol.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
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
    private ProgressDialog dialog;

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
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("正在初始化...");
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
            ToastUtils.showShort("连接成功！");

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

    public String sendMsgData(String position, String page, String type) {
        String jsonHead = "";
        List<OrderListBean> listBeans = new ArrayList<>();
        OrderListBean bean = new OrderListBean();
        bean.setPage(position);
        bean.setShowImg(page);
        bean.setType(type);
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

        featuresBeans.add(new FixedBean("首页", getFixedName("fixed_home", ""), "20",
                "20.png", "fixed_home", getSocketState("home_state", "0")));
        featuresBeans.add(new FixedBean("视频播放", getFixedName("fixed_video", ""),
                "21", "21.png", "fixed_video", getSocketState("video_state", "0")));
        featuresBeans.add(new FixedBean("视频定格", getFixedName("fixed_freeze", ""),
                "22", "22.png", "fixed_freeze", getSocketState("freeze_state", "0")));
//        featuresBeans.add(new FixedBean("缴费", getFixedName("fixed_payment", (mContext.getResources().getString(R.string.first_paragraph))), "1_1", "1_1.png", "fixed_payment"));
//        featuresBeans.add(new FixedBean("报事", getFixedName("fixed_report", (mContext.getResources().getString(R.string.two_paragraph))), "1_2", "1_2.png", "fixed_report"));
//        featuresBeans.add(new FixedBean("放行", getFixedName("fixed_release", (mContext.getResources().getString(R.string.three_paragraph))), "1_3", "1_3.png", "fixed_release"));
//        featuresBeans.add(new FixedBean("呼叫", getFixedName("fixed_call", (mContext.getResources().getString(R.string.four_paragraph))), "1_4", "1_4.png", "fixed_call"));
        featuresBeans.add(new FixedBean("智慧出行", getFixedName("fixed_call", (mContext.getResources().getString(R.string.smart_travel))),
                "23", "23.png", "fixed_call", getSocketState("call_state", "0")));
        featuresBeans.add(new FixedBean("智慧通行", getFixedName("fixed_pass", (mContext.getResources().getString(R.string.wisdom_pass))),
                "24", "24.png", "fixed_pass", getSocketState("freeze_state", getSocketState("pass_state", "0"))));
        featuresBeans.add(new FixedBean("报事服务", getFixedName("fixed_service", (mContext.getResources().getString(R.string.report_service))),
                "25", "25.png", "fixed_service", getSocketState("service_state", "0")));
        featuresBeans.add(new FixedBean("启动天启", getFixedName("fixed_start", ""),
                "26", "26.png", "fixed_start", "0"));
//        featuresBeans.add(new FixedBean("天启首页", getFixedName("fixed_tq_home", ","), "26-1", "26-1.png", "fixed_tq_home"));
//        featuresBeans.add(new FixedBean("北京王府", getFixedName("fixed_beijing", ","), "26-2", "26-2.png", "fixed_beijing"));
//        featuresBeans.add(new FixedBean("十年城", getFixedName("fixed_ten", ","), "26-3", "26-3.png", "fixed_ten"));
//        featuresBeans.add(new FixedBean("西部区域", getFixedName("fixed_area", ","), "26-4", "26-4.png", "fixed_area"));
//        featuresBeans.add(new FixedBean("大管家", getFixedName("fixed_houseKeeper", ","), "26-5", "26-5.png", "fixed_houseKeeper"));
//        featuresBeans.add(new FixedBean("EBA", getFixedName("fixed_eba", ","), "26-6", "26-6.png", "fixed_eba"));
//        featuresBeans.add(new FixedBean("智慧社区", getFixedName("fixed_community", ","), "26-7", "26-7.png", "fixed_community"));

        String msg = mContext.getResources().getString(R.string.first_paragraph) + mContext.getResources().getString(R.string.two_paragraph) + mContext.getResources().getString(R.string.three_paragraph) + mContext.getResources().getString(R.string.four_paragraph);
//        featuresBeans.add(new FixedBean("自动播放", getFixedName("fixed_auto", msg), "1_auto", "1_auto.png", "fixed_auto"));
//        featuresBeans.add(new FixedBean("暂停", getFixedName("fixed_pause", ""), "", "", "fixed_pause"));
        return featuresBeans;
    }


    public String getFixedName(String tag, String initData) {
        String name = StringUtils.isEmpty(SharedPreferencesUtils.init(mContext).getString(tag)) ? initData
                : SharedPreferencesUtils.init(mContext).getString(tag);
        return name;
    }

    public String getSocketState(String tag, String state) {
        String name = StringUtils.isEmpty(SharedPreferencesUtils.init(mContext).getString(tag)) ? state
                : SharedPreferencesUtils.init(mContext).getString(tag);
        return name;

    }


    public void initWebView(WebView webView, Context mContext) {
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ToastUtils.showShort("加载完毕");
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ToastUtils.showShort("开始加载");
                if (dialog != null)
                    dialog.show();
            }
        });
        WebSettings webSetting = webView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setAppCachePath(mContext.getDir("appcache", 0).getPath());
//        webSetting.setDatabasePath(mContext.getDir("databases", 0).getPath());
//        webSetting.setGeolocationDatabasePath(mContext.getDir("geolocation", 0).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportZoom(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setGeolocationEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setUseWideViewPort(true); // 关键点
        webSetting.setAllowFileAccess(true); // 允许访问文件
        webSetting.setSupportZoom(true); // 支持缩放
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setPluginState(WebSettings.PluginState.ON);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容

        //支持屏幕缩放
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webView.loadUrl("http://101.200.194.246:9222/manageoperate.aspx");


    }

}
