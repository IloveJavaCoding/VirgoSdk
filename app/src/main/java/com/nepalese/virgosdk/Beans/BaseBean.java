package com.nepalese.virgosdk.Beans;

import com.nepalese.virgosdk.Util.JsonUtil;

import java.io.Serializable;

/**
 * @author nepalese on 2020/9/25 13:55
 * @usage
 */
public class BaseBean implements Serializable {
    public BaseBean(){
    }

    public String toJson(){
        return JsonUtil.toJson(this);
    }
}
