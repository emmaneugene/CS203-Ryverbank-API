package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Controller that manages HTTP requests to "/stocks"
 */
@RestController
public class StockController {
    private StockRepository stockRepo;
    private TradeRepository tradeRepo;
    private TradeService tradeService;

    private static final String[] SYMBOLS = 
    {"A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78", "C07", "J36", "J37",
     "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63", "Y92", "U11", 
     "U14", "V03", "F34", "BS6"};

    private static final Double[] LAST_PRICES = 
    {3.23, 1.65, 2.76, 1.91, 7.73, 1.46, 3.80, 21.39, 0.67, 3.76, 17.82, 40.72, 19.77, 4.56, 1.92, 
     3.18, 2.07, 8.74, 3.03, 1.44, 9.20, 3.51, 2.17, 3.59, 0.58, 19.60, 6.79, 20.79, 4.49, 0.97};

    public StockController(StockRepository stockRepo, TradeRepository tradeRepo, 
    TradeService tradeService){
        this.stockRepo = stockRepo;
        this.tradeRepo = tradeRepo;
        this.tradeService = tradeService;
    }

    /**  
     * Market maker function that creates stocks as well as market maker buy and sell bids 
     * given a list of stock symbols.
     * 
     * This method should be called by the main method during application startup, with a file
     * containing a list stock symbols and last prices provided
     *
     * @param data A txt file containing information for stocks
     */
    public void createStocks(){
        
        
        int volume = 20000;
        Random random = new Random();

        for (int i = 0; i < SYMBOLS.length; i++) {
            Double bid = (LAST_PRICES[i] * 100 - 1 - random.nextInt(20)) / 100;
            Double ask =  (LAST_PRICES[i] * 100 + 1 + random.nextInt(20)) / 100;

            Trade marketBuy = new Trade();
            marketBuy.setAction("buy");
            marketBuy.setSymbol(SYMBOLS[i]);
            marketBuy.setQuantity(volume);
            marketBuy.setBid(bid);
            marketBuy.setAvg_price(0);
            marketBuy.setDate(System.currentTimeMillis());
            marketBuy.setAccount_id(Long.valueOf(0));
            marketBuy.setCustomer_id(Long.valueOf(0));
            marketBuy.setStatus("open");
            marketBuy.setProcessed(true);

            Trade marketSell = new Trade();
            marketSell.setAction("sell");
            marketSell.setSymbol(SYMBOLS[i]);
            marketSell.setQuantity(volume);
            marketSell.setAsk(ask);
            marketSell.setAvg_price(0);
            marketSell.setDate(System.currentTimeMillis());
            marketSell.setAccount_id(Long.valueOf(0));
            marketSell.setCustomer_id(Long.valueOf(0));
            marketSell.setStatus("open");
            marketSell.setProcessed(false);

            tradeRepo.save(marketBuy);
            tradeRepo.save(marketSell);
            stockRepo.save(new Stock(SYMBOLS[i], LAST_PRICES[i], volume, bid, volume, ask));
        }
        
    }

    /**
     * Get all publicly traded stocks. This method calls updateStockDetails() 
     * to get the most recent information on ask, ask volume, bid and bid volume. 
     * 
     * @return list of all tradable stocks
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        List<Stock> stocks = stockRepo.findAll();

        for (Stock stock : stocks) {
            updateStockDetails(stock);
        }

        return stocks;
    }

    /**
     * Get an individual stock as specified by its symbol. This method calls updateStockDetails() 
     * to get the most recent information on bid volume, bid, ask volume and ask
     * 
     * @return the stock specified by the symbol
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks/{symbol}")
    public Stock getStock(@PathVariable String symbol) {
        if (!stockRepo.existsBySymbol(symbol))
            throw new StockNotFoundException(symbol);
        
        Stock stock = stockRepo.findBySymbol(symbol).get();
        updateStockDetails(stock);

        return stock;
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
        Trade buy = tradeService.getHighestBidTradeForStock(stock.getSymbol());
        Trade sell = tradeService.getLowestAskTradeForStock(stock.getSymbol());
        
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

