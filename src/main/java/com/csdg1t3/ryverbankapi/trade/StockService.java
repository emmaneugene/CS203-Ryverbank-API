package com.csdg1t3.ryverbankapi.trade;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service layer for stocks that helps to fetch up-to-date stock prices
 */
@Service
public class StockService {

    private StockRepository stockRepo;
    private TradeService tradeSvc;

    public StockService(StockRepository stockRepo, TradeService tradeSvc) {
        this.stockRepo = stockRepo;
        this.tradeSvc = tradeSvc;
    }

    public List<Stock> getAllUpdatedStocks() {
        List<Stock> stocks = stockRepo.findAll();

        for (Stock stock : stocks) {
            updateStockDetails(stock);
        }

        return stocks;
    }

    public Optional<Stock> getUpdatedStock(String symbol) {
        Optional<Stock> stockOpt = stockRepo.findBySymbol(symbol);
        if (stockOpt.isEmpty())
            return stockOpt;
        
        Stock stock = stockOpt.get();
        updateStockDetails(stock);

        return Optional.of(stock);
    }

    /**
     * Updates a stock's bid volume, bid, ask volume and ask using methods from
     * TradeService. It then saves the updated stock to the database
     * 
     * @param stock The stock to be updated
     */
    public void updateStockDetails(Stock stock) {
        double bid, ask;
        bid = ask = stock.getLast_price();
        int bid_volume, ask_volume;
        bid_volume = ask_volume = 0;
        Trade buy = tradeSvc.getHighestBidTradeForStock(stock.getSymbol());
        Trade sell = tradeSvc.getLowestAskTradeForStock(stock.getSymbol());
        
        if (buy != null) {
            bid_volume = buy.getRemaining_quantity();
            bid = buy.getBid();
        }

        if (sell != null) {
            ask_volume = sell.getRemaining_quantity();
            ask = sell.getAsk();
        }

        stock.setBid_volume(bid_volume);
        stock.setBid(bid);
        stock.setAsk_volume(ask_volume);
        stock.setAsk(ask);

        stockRepo.save(stock);
    }
}
