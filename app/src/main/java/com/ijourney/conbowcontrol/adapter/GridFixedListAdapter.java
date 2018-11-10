package com.ijourney.conbowcontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ijourney.conbowcontrol.R;
import com.ijourney.conbowcontrol.bean.FixedBean;

import java.util.ArrayList;
import java.util.List;

public class GridFixedListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private List<FixedBean> list = new ArrayList<>();

    public GridFixedListAdapter(Context mContext, List<FixedBean> list) {
        this.mContext = mContext;
        this.list = list;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<FixedBean> list) {
        this.list = list;

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
            convertView = inflater.inflate(R.layout.item_features, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btn_name = (TextView) convertView.findViewById(R.id.btn_name);
        FixedBean bean = list.get(position);
        holder.btn_name.setBackground(mContext.getResources().getDrawable(bean.isCheck() ? R.drawable.shape_gride_un_check : R.drawable.shape_gride_check));
        holder.btn_name.setTextColor(mContext.getResources().getColor(bean.isCheck() ? R.color.white : R.color.white));
        holder.btn_name.setText(bean.getName());
        return convertView;
    }

    ViewHolder holder;

    class ViewHolder {
        TextView btn_name;

    }
}
