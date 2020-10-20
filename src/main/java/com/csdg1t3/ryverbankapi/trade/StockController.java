package com.csdg1t3.ryverbankapi.trade;

import java.util.Scanner;
import java.util.Optional;
import java.util.List;
import java.io.File;
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

    public StockController(StockRepository stockRepo){
        this.stockRepo = stockRepo;
        insertStocks();
    }

    public void insertStocks(){
        try{
            File stocks = new File("/Users/kweksinyee/Desktop/CS203 Project/ryverbank-api/data/stocks.txt");
            Scanner sc = new Scanner(stocks);
            while(sc.hasNextLine() ){
                String stockDetails = sc.nextLine();
                if(!stockDetails.matches(".*[,].*[,].*[,].*[,].*[,].*")){
                    continue;
                }
                String[] stockComponents = stockDetails.split(",");
                String symbol = stockComponents[0];
                Double last_Price = Double.parseDouble(stockComponents[1]);
                int bid_Volume = Integer.parseInt(stockComponents[2]);
                Double bid = Double.parseDouble(stockComponents[3]);
                int ask_Volume = Integer.parseInt(stockComponents[4]);
                Double ask =  Double.parseDouble(stockComponents[5]);
                stockRepo.save(new Stock(symbol, last_Price, bid_Volume, bid, ask_Volume, ask));
            }
            sc.close();
        }catch(IOException e){
            System.out.println("File not found");
        }
        // List<Stock> stored = getStocks();
        // for(Stock s : stored){
        //     System.out.println(s.toString());
        // }        
    }

    /**
     * Get stocks 
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        System.out.println("Im here");
        return stockRepo.findAll();
    }

    /**
     * Get individual stock
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stocks/{symbol}")
    public Stock getStock(@PathVariable String symbol) {
        System.out.println("In get stocks");
        List<Stock> stocks = getStocks();
        
        for (Stock stock : stocks) {
            if (stock.getSymbol().equals(symbol))
                return stock;
        }

        throw new StockNotFoundException(symbol);
    }

}

