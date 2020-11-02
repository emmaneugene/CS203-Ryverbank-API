package com.csdg1t3.ryverbankapi.trade;

import java.util.Comparator;

public class TradeTimeComparator implements Comparator<Trade> {
    public int compare(Trade a, Trade b) {
        return (int)(a.getDate() - b.getDate());
    }
}
