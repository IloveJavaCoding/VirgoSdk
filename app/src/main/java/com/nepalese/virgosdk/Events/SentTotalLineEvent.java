package com.nepalese.virgosdk.Events;

public class SentTotalLineEvent {
    private int totalLines;

    public SentTotalLineEvent(int totalLines) {
        this.totalLines = totalLines;
    }

    public int getTotalLines() {
        return totalLines;
    }
}
