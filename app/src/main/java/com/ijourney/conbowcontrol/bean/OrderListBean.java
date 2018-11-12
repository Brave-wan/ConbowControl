package com.ijourney.conbowcontrol.bean;

public class OrderListBean {
    private String showImg;
    private String page;
    private String type; //1播放0不播

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setShowImg(String showImg) {
        this.showImg = showImg;
    }

    public String getPage() {
        return page;
    }

    public String getShowImg() {
        return showImg;
    }
}
