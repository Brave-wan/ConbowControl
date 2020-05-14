package com.ijourney.conbowcontrol.bean;

public class FixedBean {
    private String name;
    private String content;
    private String socket_position;
    private String socket_page;
    private boolean isCheck;
    private String type;
    private int id;
    private String tag;

    private int visibility;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public FixedBean(String name, String content, String socket_position, String socket_page, String tag, String type) {
            this(name,content,socket_position,socket_page,tag,type,1);
    }
    public FixedBean(String name, String content, String socket_position, String socket_page, String tag, String type,int visibility) {
        this.name = name;
        this.content = content;
        this.socket_page = socket_page;
        this.socket_position = socket_position;
        this.tag = tag;
        this.type = type;
        this.visibility = visibility;

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

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
