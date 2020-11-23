package com.nepalese.virgosdk.Beans;

/**
 * @author nepalese on 2020/11/23 13:59
 * @usage
 */
public class M3U8Ts extends BaseBean{
    //example: 1606096666-1-1593893848.hls.ts
    private String content;
    private float second;

    public M3U8Ts(String content, float seconds) {
        this.content = content;
        this.second = seconds;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getSecond() {
        return second;
    }

    public void setSecond(float second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "M3U8Ts{" +
                "content='" + content + '\'' +
                ", second=" + second +
                '}';
    }
}
