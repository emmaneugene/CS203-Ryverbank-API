package com.csdg1t3.ryverbankapi.trade;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Market maker class that creates stocks and market maker trades at application startup
 */
@Component
public class MarketMaker {
    private TradeRepository tradeRepo;
    private StockRepository stockRepo;

    private static final String[] SYMBOLS = 
    {"A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78", "C07", "J36", "J37",
     "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63", "Y92", "U11", 
     "U14", "V03", "F34", "BS6"};

    private static final Double[] LAST_PRICES = 
    {3.23, 1.65, 2.76, 1.91, 7.73, 1.46, 3.80, 21.39, 0.67, 3.76, 17.82, 40.72, 19.77, 4.56, 1.92, 
     3.18, 2.07, 8.74, 3.03, 1.44, 9.20, 3.51, 2.17, 3.59, 0.58, 19.60, 6.79, 20.79, 4.49, 0.97};

    public MarketMaker(TradeRepository tradeRepo, StockRepository stockRepo) {
        this.tradeRepo = tradeRepo;
        this.stockRepo = stockRepo;
    }

    /**  
     * Market maker function that creates stocks as well as market maker buy and sell bids 
     * given a list of stock symbols.
     * 
     * This method should be called by the main method during application startup.
     *
     */
    public void initMarket(){
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
}
