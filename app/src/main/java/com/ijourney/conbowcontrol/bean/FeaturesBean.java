package com.ijourney.conbowcontrol.bean;

import org.litepal.crud.DataSupport;

public class FeaturesBean extends DataSupport {
    private String name;
    private String content;
    private String socket_position;
    private String socket_page;
    private boolean isCheck;

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public FeaturesBean() {

    }

    public FeaturesBean(String name, String content, String socket_position, String socket_page) {
        this.name = name;
        this.content = content;
        this.socket_page = socket_page;
        this.socket_position = socket_position;

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
