package com.ijourney.conbowcontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ijourney.conbowcontrol.R;
import com.ijourney.conbowcontrol.bean.FixFatherBean;
import com.ijourney.conbowcontrol.bean.FixedBean;

import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private List<FixFatherBean> fixFatherBeans;
    private IItemListener listener;


    LayoutInflater mInflater;
    Context context;

    public ExpandableListViewAdapter(Context context,List<FixFatherBean> fixFatherBeans) {
        this.context = context;
        this.fixFatherBeans = fixFatherBeans;
        mInflater = LayoutInflater.from(context);
    }

    public void setListener(IItemListener listener) {
        this.listener = listener;
    }

    @Override
    public int getGroupCount() {
        return fixFatherBeans.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return fixFatherBeans.get(i).getChildListBean().size();
    }

    @Override
    public Object getGroup(int i) {
        return fixFatherBeans.get(i);
    }

    @Override
    public Object getChild(int i, int position) {
        return fixFatherBeans.get(i).getChildListBean().get(position);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        convertView = mInflater.inflate(R.layout.item_features,viewGroup,false);
        TextView btn_name = convertView.findViewById(R.id.btn_name);
        btn_name.setTextSize(20);
        btn_name.setText(fixFatherBeans.get(i).getText());
        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        convertView = mInflater.inflate(R.layout.item_features,null);
        TextView btn_name = convertView.findViewById(R.id.btn_name);
        final FixedBean bean = fixFatherBeans.get(i).getChildListBean().get(i1);
        btn_name.setTextSize(15);
        btn_name.setTextColor(context.getResources().getColor(bean.isCheck()?R.color.colorAccent:R.color.spots_dialog_color));
        btn_name.setText(bean.getName());
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener!=null){
//                    listener.result(bean);
//                }
//            }
//        });


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public interface IItemListener{
        void result(FixedBean bean);
    }
}
