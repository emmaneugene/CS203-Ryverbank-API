package com.csdg1t3.ryverbankapi.trade;
import java.util.List;

public interface TradeService {
    List<Trade> listTrades();
    Trade getTrade(Long id);

    /**
     * Return the newly added Trade
     */
    Trade addTrade(Trade Trade);

    /**
     * Return the updated Trade
     * @param id
     * @param Trade
     * @return
     */
    Trade updateTrade(Long id, Trade Trade);

    /**
     * Return status of the delete
     * If it's 1: the Trade has been removed
     * If it's 0: the Trade does not exist
     * @param id
     * @return 
     */
    int deleteTrade(Long id);
}
