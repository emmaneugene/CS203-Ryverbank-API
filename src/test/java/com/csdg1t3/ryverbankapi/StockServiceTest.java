package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.account.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
    @Mock
    private StockRepository stockRepo;

    @Mock
    private TradeService tradeSvc;

    @Mock
    private MarketMaker marketMaker;
    
    @InjectMocks
    private StockService stockSvc;

    @Test
    void getAllUpdatedStocks_emptyListOfStocks_ReturnEmptyStockList() {
        List<Stock> stocks = new ArrayList<Stock>();
        
        when(stockRepo.findAll()).thenReturn(stocks);

        List<Stock> returnedList = stockSvc.getAllUpdatedStocks();

        assertEquals(stocks, returnedList);
        verify(stockRepo).findAll();
    }

    @Test
    void getAllUpdatedStocks_listOfStocks_ReturnAllStocks() {
        Stock stock = new Stock("A17U", 3.23, 500, 3.33, 500, 3.35);
        List<Stock> stocks = new ArrayList<Stock>();
        stocks.add(stock);

        when(stockRepo.findAll()).thenReturn(stocks);

        List<Stock> returnedList = stockSvc.getAllUpdatedStocks();

        assertEquals(stocks, returnedList);
        verify(stockRepo).findAll();
    }

    @Test
    void getUpdatedStock_validStock_ReturnUpdatedStock() {
        Stock stock = new Stock("A17U", 3.23, 500, 3.33, 500, 3.35);
        Optional<Stock> found = Optional.of(stock);

        when(stockRepo.findBySymbol(any(String.class))).thenReturn(found);

        Optional<Stock> returnedStock = stockSvc.getUpdatedStock(stock.getSymbol());

        assertEquals(found, returnedStock);
        verify(stockRepo).findBySymbol(stock.getSymbol());
    }

    @Test
    void getUpdatedStock_invalidStock_ReturnUpdatedStock() {
        Optional<Stock> notFound = Optional.empty();

        when(stockRepo.findBySymbol(any(String.class))).thenReturn(notFound);

        Optional<Stock> returnedStock = stockSvc.getUpdatedStock("invalidStock");

        assertEquals(notFound, returnedStock);
        verify(stockRepo).findBySymbol("invalidStock");
    }

    @Test
    void updateStockDetails_validStock_usesAllMocks() {
        Long id = Long.valueOf(1);
        String symbol = "A17U";
        User user = new User(id, "Test", "S1234567G", "92307777", "Test Address", "Testing123", "testTest123", "ROLE_USER", true);
        Account account = new Account(id, user, id, 1000.00, 1000.00);
        Stock stock = new Stock(symbol, 3.23, 500, 3.33, 500, 3.35);
        Trade buy = new Trade(id, "buy", stock.getSymbol(), 200, 0.0, stock.getAsk(), 3.33, 200, System.currentTimeMillis(), account, user, "open", false, 0.0);
        Trade sell = new Trade(id, "sell", stock.getSymbol(), 200, 3.34, stock.getAsk(), 0.0, 0, System.currentTimeMillis(), account, user, "open", false, 0.0);
        
        when(tradeSvc.getHighestBidTradeForStock(symbol)).thenReturn(buy);
        when(tradeSvc.getLowestAskTradeForStock(symbol)).thenReturn(sell);
        when(stockRepo.save(stock)).thenReturn(stock);

        stockSvc.updateStockDetails(stock);

        verify(tradeSvc).getHighestBidTradeForStock(symbol);
        verify(tradeSvc).getLowestAskTradeForStock(symbol);
        verify(stockRepo).save(stock);
    }

    @Test
    void updateStockDetails_invalidStock_usesAllMocks() {
        String symbol = "inValid";
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        
        when(tradeSvc.getHighestBidTradeForStock(symbol)).thenReturn(null);
        when(tradeSvc.getLowestAskTradeForStock(symbol)).thenReturn(null);
        when(stockRepo.save(stock)).thenReturn(null);

        stockSvc.updateStockDetails(stock);

        verify(tradeSvc).getHighestBidTradeForStock(symbol);
        verify(tradeSvc).getLowestAskTradeForStock(symbol);
        verify(stockRepo).save(stock);
    }
}
