package com.ijourney.conbowcontrol.old;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.ijourney.conbowcontrol.R;
import com.ijourney.conbowcontrol.bean.FeaturesBean;

public class MessageDialog extends Dialog {

    EditText ed_btn_nick;
    Context context;
    TextView tx_dialog_sure;
    private onMessageListener listener;
    FeaturesBean featuresBean;

    public void setOnMessageListener(onMessageListener listener) {
        this.listener = listener;
    }

    public MessageDialog(Context mContext, FeaturesBean bean) {
        super(mContext, R.style.DialogTheme);
        context = mContext;
        this.featuresBean = bean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        ed_btn_nick = (EditText) findViewById(R.id.ed_btn_nick);
        tx_dialog_sure = (TextView) findViewById(R.id.tx_dialog_sure);
        tx_dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !StringUtils.isEmpty(ed_btn_nick.getText().toString().trim())) {
                    featuresBean.setName(ed_btn_nick.getText().toString().trim());
                    if (featuresBean.save()) {
                        listener.sendMessage(featuresBean);
                        ed_btn_nick.setText("");
                        dismiss();
                    } else {
                        Toast.makeText(context, "保存失败!", Toast.LENGTH_LONG).show();
                    }


                }

            }
        });
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.7); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }


    public interface onMessageListener {
        void sendMessage(FeaturesBean bean);
    }


}
