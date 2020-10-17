package com.csdg1t3.ryverbankapi.trade;



import java.util.*;

public interface TradeService {
    List<Trade> listTrades();

    Trade getTrade(Long Id);

    /**
     * Goes through all open or parially filled trades and changes the status to "expired" if any
     * trades are expired
     * 
     * A trade is expired if the time is past 5pm on the day which the trade was posted
     */
    void updateTradeExpiry();

    /**
     * Retrieves all open or partial-filled buy trades
     * 
     * @return a list of open or partial-filled buy trades
     */
    List<Trade> listValidBuyTrades();

    /**
     * Retrieves all open or partial-filled buy trades
     * 
     * @return a list of open or partial-filled sell trades
     */
    List<Trade> listValidSellTrades();

    /**
     * Retrieves an open or partial-filled sell trade with the lowest ask price.
     * If multiple trades are found, returns the trade which was placed the earliest
     * 
     * @return trade with lowest ask price
     */
    Trade getLowestAskTrade();

    /**
     * Retrieves an open or partial-filled buy trade with the highest bid price.
     * If multiple trades are found, returns the trade which was placed the earliest
     * 
     * @return trade with highest bid price
     */
    Trade getHighestBidTrade();

    Trade createTrade(Trade trade);

    Trade updateTrade(Long Id, Trade trade);

    void cancelTrade(Long id);
}
