package com.csdg1t3.ryverbankapi.trade;

import java.util.Scanner;
import java.util.Optional;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import javax.validation.Valid;

public class StockController {
    private StockRepository stockRepo;

    public StockController(StockRepository stockRepo){
        this.stockRepo = stockRepo;
        insertStocks();
    }

    public void insertStocks(){
        File stocks = new File("/Users/kweksinyee/Desktop/CS203 Project/ryverbank-api/data/stocks.txt");
        Scanner sc = new Scanner(stocks);
        while(sc.hasNextLine() ){
            String stockDetails = sc.nextLine();
            if(!stockDetails.matches(".*[,].*[,].*[,].*[,].*[,].*")){
                continue;
            }
            String[] stockComponents = stockDetails.split(",");
            String symbol = stockComponents[0];
            Double last_Price = stockComponents[1];
            Double bid_Volume = stockComponents[2];
            Double bid = stockComponents[3];
            Double ask_Volume = stockComponents[4];
            Double ask = stockComponents[5];
            stockRepo.save(new Stock(symbol, last_Price, bid_Volume, bid, ask_Volume, ask));
        }
    }

    /**
     * Get stocks 
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        return accountRepo.findAll();
    }

    /**
     * Get individual stock
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks/{id}")
    public Account getStock(@PathVariable Long id) {
        List<Stocks> stocks = getStocks();
        
        for (Stock stock : stocks) {
            if (stock.getId() == id)
                return stock;
        }

        throw new StockNotFoundException(id);
    }

}

