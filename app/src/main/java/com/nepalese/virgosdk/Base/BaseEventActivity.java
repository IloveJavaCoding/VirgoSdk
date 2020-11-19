package com.nepalese.virgosdk.Base;

import org.greenrobot.eventbus.EventBus;

/**
 * @author nepalese on 2020/11/19 12:14
 * @usage 自带EventBus 注册的 activity
 */
public class BaseEventActivity extends BaseActivity {
    public BaseEventActivity() {
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.registerEvent();
    }

    private void registerEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unregisterEvent() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void onDestroy() {
        this.unregisterEvent();
        super.onDestroy();
    }
}
