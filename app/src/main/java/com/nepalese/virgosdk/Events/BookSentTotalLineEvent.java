package com.nepalese.virgosdk.Events;

public class BookSentTotalLineEvent {
    private int totalLines;

    public BookSentTotalLineEvent(int totalLines) {
        this.totalLines = totalLines;
    }

    public int getTotalLines() {
        return totalLines;
    }
}
