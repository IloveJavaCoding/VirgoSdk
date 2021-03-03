package com.nepalese.virgosdk.Base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;

/**
 * @author nepalese on 2020/12/5 09:50
 * @usage 自带EventBus 注册的 Fragment
 */
public class BaseEventFragment extends Fragment {
    private static final String TAG = "BaseEventFragment";

    public BaseEventFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerEvent();
        return super.onCreateView(inflater, container, savedInstanceState);
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

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        unregisterEvent();
    }
}
