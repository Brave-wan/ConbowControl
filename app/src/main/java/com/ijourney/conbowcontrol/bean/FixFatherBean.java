package com.ijourney.conbowcontrol.bean;

import java.util.List;

public class FixFatherBean {

    private String text;
    private List<FixedBean> childListBean;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<FixedBean> getChildListBean() {
        return childListBean;
    }

    public void setChildListBean(List<FixedBean> childListBean) {
        this.childListBean = childListBean;
    }
}
