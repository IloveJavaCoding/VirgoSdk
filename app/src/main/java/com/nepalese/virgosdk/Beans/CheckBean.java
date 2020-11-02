package com.nepalese.virgosdk.Beans;

/**
 * @author nepalese on 2020/11/2 09:20
 * @usage
 */
public class CheckBean {
    private int id;
    private boolean isChecked;

    public CheckBean(int id, boolean isChecked) {
        this.id = id;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
