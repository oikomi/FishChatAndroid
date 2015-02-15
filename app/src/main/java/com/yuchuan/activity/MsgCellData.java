package com.yuchuan.activity;

/**
 * Created by haroldmiao on 2015/2/13.
 */
public class MsgCellData {

    private String name;
    private String msg;
    private int image;

    public MsgCellData(int image, String name, String msg) {
        this.image = image;
        this.name = name;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public int getImage() {
        return image;
    }
}
