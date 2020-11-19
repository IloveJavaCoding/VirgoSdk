package com.nepalese.virgosdk.Events;

public class BookViewClickEvent {
    private boolean isShow;

    public BookViewClickEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }
}
