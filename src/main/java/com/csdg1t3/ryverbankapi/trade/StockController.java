package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Controller that manages HTTP requests to "/api/stocks"
 */
@RestController
public class StockController {
    private StockService stockSvc;
    
    public StockController(StockService stockSvc){
        this.stockSvc = stockSvc;
    }

    /**
     * Get all publicly traded stocks. This method calls updateStockDetails() 
     * to get the most recent information on ask, ask volume, bid and bid volume. 
     * 
     * @return list of all tradable stocks
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/stocks")
    public List<Stock> getStocks() {
        return stockSvc.getAllUpdatedStocks();
    }

    /**
     * Get an individual stock as specified by its symbol. This method calls updateStockDetails() 
     * to get the most recent information on bid volume, bid, ask volume and ask
     * 
     * @return the stock specified by the symbol
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/stocks/{symbol}")
    public Stock getStock(@PathVariable String symbol) {
        Optional<Stock> stockOpt = stockSvc.getUpdatedStock(symbol);
        if (stockOpt.isEmpty())
            throw new StockNotFoundException(symbol);

        return stockOpt.get();
    }
}

