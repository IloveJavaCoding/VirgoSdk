package com.nepalese.virgosdk.Events;


import com.nepalese.virgosdk.Beans.DownloadItem;

/**
 * @author nepalese on 2020/9/23 17:19
 * @usage
 */
public class DownloadResourceEvent {
    private DownloadItem item;

    public DownloadResourceEvent(DownloadItem item) {
        this.item = item;
    }

    public DownloadItem getItem() {
        return item;
    }
}
