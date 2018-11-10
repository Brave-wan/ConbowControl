package com.ijourney.conbowcontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.ijourney.conbowcontrol.R;
import com.ijourney.conbowcontrol.bean.FeaturesBean;
import com.ijourney.conbowcontrol.old.MessageDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FeaturesAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private List<FeaturesBean> list = new ArrayList<>();

    public FeaturesAdapter(Context mContext, List<FeaturesBean> list) {
        this.mContext = mContext;
        this.list = list;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData() {
        list = DataSupport.findAll(FeaturesBean.class);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_message, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_message_content = (TextView) convertView.findViewById(R.id.tv_message_content);
        holder.tv_message_time = (TextView) convertView.findViewById(R.id.tv_message_time);
        holder.tv_message_alter = (TextView) convertView.findViewById(R.id.tv_message_alter);
        final FeaturesBean bean = list.get(position);
        holder.tv_message_content.setText(bean.getName());
        holder.tv_message_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bean.delete();
                setData();
            }
        });
        holder.tv_message_alter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog dialog = new MessageDialog(mContext, bean);
                dialog.show();
                dialog.setOnMessageListener(new MessageDialog.onMessageListener() {
                    @Override
                    public void sendMessage(FeaturesBean bean) {
                        Toast.makeText(mContext, bean.getName(), Toast.LENGTH_LONG).show();
                        setData();
                    }
                });
            }
        });
        return convertView;
    }

    ViewHolder holder;

    class ViewHolder {
        TextView tv_message_content;
        TextView tv_message_time;
        TextView tv_message_alter;
    }
}
