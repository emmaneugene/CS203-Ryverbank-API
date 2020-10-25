package com.csdg1t3.ryverbankapi.trade;

import java.util.*;

public interface TradeService {

    void updateTradeExpiry();

    List<Trade> listValidBuyTradesForStock(String symbol);

    List<Trade> listValidSellTradesForStock(String symbol);

    Trade getLowestAskTradeForStock(String symbol);

    Trade getHighestBidTradeForStock(String symbol);

    Trade getEarliestMarketBuyForStock(String symbol);

    Trade getEarliestMarketSellForStock(String symbol);

    Trade makeTrade(Trade trade);

    void processBuy(Trade trade);

    void processSell(Trade trade);

    void processMarketBuy(Trade trade);

    void processMarketSell(Trade trade);

    void fillTrades(Trade buy, Trade sell, Double price, int qty);

    void processCancelTrade(Trade trade);
}
