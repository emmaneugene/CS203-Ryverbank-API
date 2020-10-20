package com.csdg1t3.ryverbankapi.trade;

import java.util.Scanner;
import java.util.Optional;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


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

@RestController
public class StockController {
    private StockRepository stockRepo;
    private TradeService tradeService;

    public StockController(StockRepository stockRepo, TradeService tradeService){
        this.stockRepo = stockRepo;
        this.tradeService = tradeService;
    }

    /**  
     * Market maker function that creates stocks as well as market maker buy and sell bids 
     * given a list of stock symbols.
     * 
     * This method should be called by the main method during application startup
     */
    public void createStocks(File data){
        Random random = new Random();
        try{
            Scanner sc = new Scanner(data);
            while(sc.hasNextLine()){              
                String[] stockComponents = sc.nextLine().split(",");
                String symbol = stockComponents[0];
                Double last_Price = Double.parseDouble(stockComponents[1]);

                int bid_Volume = 20000;
                Double bid = last_Price - (random.nextDouble() % 0.2);
                int ask_Volume = 20000;
                Double ask =  last_Price + (random.nextDouble() % 0.2);

                Trade marketBuy =  new Trade();
                marketBuy.setSymbol(symbol);
                marketBuy.setAction("buy");
                marketBuy.setQuantity(bid_Volume);
                marketBuy.setBid(bid);
                marketBuy.setDate(System.currentTimeMillis()/1000);
                marketBuy.setAccountId(Long.valueOf(0));
                marketBuy.setAccountId(Long.valueOf(0));

                Trade marketSell =  new Trade();
                marketSell.setSymbol(symbol);
                marketSell.setAction("sell");
                marketSell.setQuantity(ask_Volume);
                marketSell.setAsk(ask);
                marketSell.setDate(System.currentTimeMillis()/1000);
                marketSell.setAccountId(Long.valueOf(0));
                marketSell.setAccountId(Long.valueOf(0));

                tradeService.createTrade(marketBuy);
                tradeService.createTrade(marketSell);
                stockRepo.save(new Stock(symbol, last_Price, bid_Volume, bid, ask_Volume, ask));
            }
            sc.close();
        }catch(FileNotFoundException e){
            System.out.println("File not found");
        }
        
    }

    /**
     * Get all publicly traded stocks. Controller will call TradeService to get the most recent 
     * information on
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        return stockRepo.findAll();
    }

    /**
     * Get individual stock
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks/{symbol}")
    public Stock getStock(@PathVariable String symbol) {
        Optional<Stock> result = stockRepo.findBySymbol(symbol);
        
        if (!result.isPresent())
            throw new StockNotFoundException(symbol);

        return result.get();
    }
}

