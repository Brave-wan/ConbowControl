package com.ijourney.conbowcontrol.bean;

public class FixedBean {
    private String name;
    private String content;
    private String socket_position;
    private String socket_page;
    private boolean isCheck;
    private String tag;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public FixedBean() {

    }

    public FixedBean(String name, String content, String socket_position, String socket_page, String tag) {
        this.name = name;
        this.content = content;
        this.socket_page = socket_page;
        this.socket_position = socket_position;
        this.tag = tag;

    }

    public boolean isCheck() {
        return isCheck;
    }

    public String getSocket_position() {
        return socket_position;
    }

    public void setSocket_position(String socket_position) {
        this.socket_position = socket_position;
    }

    public String getSocket_page() {
        return socket_page;
    }

    public void setSocket_page(String socket_page) {
        this.socket_page = socket_page;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
