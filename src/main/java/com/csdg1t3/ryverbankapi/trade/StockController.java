package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import javax.validation.Valid;

import java.util.*;
import java.io.*;

@RestController
public class StockController {
    private StockRepository stockRepo;
    private TradeRepository tradeRepo;
    private TradeService tradeService;

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
     */
    public void createStocks(File data){
        Random random = new Random();
        try{
            Scanner sc = new Scanner(data);
            while(sc.hasNextLine()){              
                String[] stockComponents = sc.nextLine().split(",");
                String symbol = stockComponents[0];
                Double last_Price = Double.parseDouble(stockComponents[1]);

                int volume = 20000;
                Double bid = (last_Price * 100 - 1 - random.nextInt(20)) / 100;
                Double ask =  (last_Price * 100 + 1 + random.nextInt(20)) / 100;

                Trade marketBuy = new Trade();
                marketBuy.setAction("buy");
                marketBuy.setSymbol(symbol);
                marketBuy.setQuantity(volume);
                marketBuy.setBid(bid);
                marketBuy.setAvg_price(0);
                marketBuy.setDate(System.currentTimeMillis());
                marketBuy.setAccount_id(Long.valueOf(0));
                marketBuy.setCustomer_id(Long.valueOf(0));
                marketBuy.setStatus("open");

                Trade marketSell = new Trade();
                marketSell.setAction("sell");
                marketSell.setSymbol(symbol);
                marketSell.setQuantity(volume);
                marketSell.setAsk(ask);
                marketSell.setAvg_price(0);
                marketSell.setDate(System.currentTimeMillis());
                marketSell.setAccount_id(Long.valueOf(0));
                marketSell.setCustomer_id(Long.valueOf(0));
                marketSell.setStatus("open");

                tradeRepo.save(marketBuy);
                tradeRepo.save(marketSell);
                stockRepo.save(new Stock(symbol, last_Price, volume, bid, volume, ask));
            }
            sc.close();
        }catch(FileNotFoundException e){
            System.out.println("File not found");
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
     * @param stock
     */
    public void updateStockDetails(Stock stock) {
        double bid, ask;
        bid = ask = 0;
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

